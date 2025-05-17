package com.happyplaces.data.datasource.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.happyplaces.util.resolveUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/** 文章資料庫 */
const val ARTICLE_COLLECTION = "articles"

/** 用戶資料庫 */
const val USER_COLLECTION = "users"

/** Firebase Storage 路徑 */
const val ARTICLE_IMAGE_STORAGE = "article_images"
const val USER_IMAGE_STORAGE = "user_images"

class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val firebaseAuth: FirebaseAuth = Firebase.auth
) : PlaceService {
    // Firebase Storage 實例
    private val storage: FirebaseStorage = Firebase.storage

    override fun getPlaces(): Flow<List<PlaceDto>> {
        return firestore.collection(ARTICLE_COLLECTION)
            .snapshots()
            .map {
                Log.i("LinLi", "getPlaces() called $it")
                it.toObjects(PlaceDto::class.java)
            }
            .catch { e ->
                Log.e("LinLi", "getPlaces() failed", e)
                emit(emptyList())
            }
    }

    override suspend fun addPlace(place: PlaceDto): String {
        // 先為即將加入的文件建立參照，並以其產生唯一 ID
        val docRef = firestore.collection(ARTICLE_COLLECTION).document(place.id)

        // 如有本地圖片（content:// 或 file://），先行上傳並取得下載網址
        val finalImageUrl = place.imageUrl.resolveUri(
            onLocal = { uri ->
                uploadImage(uri, storage.reference.child("$ARTICLE_IMAGE_STORAGE/${place.id}"))
            },
            onRemote = { it }
        )

        // 確保寫入的 PlaceDto 內含正確的圖片網址
        val finalPlace = place.copy(
            imageUrl = finalImageUrl
        )

        return try {
            docRef.set(finalPlace).await()
            docRef.id
        } catch (e: Exception) {
            Log.e("LinLi", "addPlace() failed", e)
            throw e
        }
    }

    override suspend fun updatePlace(place: PlaceDto) {
        // 1. 如果有本地圖片，先上傳並取得新 URL
        val imageUrl = place.imageUrl.toUri().let { uri ->
            if (uri.scheme == "content" || uri.scheme == "file") {
                uploadImage(uri, storage.reference.child("$ARTICLE_IMAGE_STORAGE/${place.id}"))
            } else {
                place.imageUrl
            }
        }

        // 2. 合併更新資料
        val updated = place.copy(imageUrl = imageUrl)

        // 3. 查詢並取得文件參考
        val docRef = findPlaceDocRefByIdOrNull(place.id)

        // 4. 使用該文件參考更新內容
        docRef?.set(updated, SetOptions.merge())
            ?.await()
    }

    override suspend fun deletePlace(id: String) {
        // 1. 刪除 Firebase Storage 上的圖片
        val imageRef = storage.reference.child("$ARTICLE_IMAGE_STORAGE/$id")
        try {
            imageRef.delete().await()
            Log.i("LinLi", "deletePlace: image deleted for id=$id")
        } catch (e: Exception) {
            Log.e("LinLi", "deletePlace: failed to delete image for id=$id", e)
        }

        // 2. 刪除 FireStore 文件
        findPlaceDocRefByIdOrNull(id)?.let { docRef ->
            try {
                docRef.delete().await()
                Log.i("LinLi", "deletePlace: document deleted for id=$id")
            } catch (e: Exception) {
                Log.e("LinLi", "deletePlace: failed to delete document for id=$id", e)
            }
        }
    }

    override fun getPlaceByIdFlow(id: String): Flow<PlaceDto> = flow {
        // 使用文件參考查詢
        val docRef = findPlaceDocRefByIdOrNull(id)
        if (docRef != null) {
            val snapshot = docRef.get().await()
            snapshot.toObject(PlaceDto::class.java)?.let { emit(it) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 上傳本地圖片至 Firebase Storage，並回傳下載 URL
     *
     * @param localUri 本地圖片 Uri
     * @param storageReference Firebase Storage 參考
     * @return 圖片的公開下載 URL
     */
    suspend fun uploadImage(localUri: Uri, storageReference: StorageReference): String {
        Log.d("LinLi", "uploadImage() called with localUri=$localUri")
        Log.d("LinLi", "  ▶ uploadImage start putFile")
        // 1. 上傳檔案
        try {
            storageReference.putFile(localUri).await()
            Log.d("LinLi", "  ▶ putFile.await() returned, upload complete")
        } catch (e: Exception) {
            Log.e("LinLi", "  ▶ putFile.await() failed", e)
            throw e
        }
        // 2. 取得並回傳下載 URL
        val downloadUrl: String = try {
            storageReference.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("LinLi", "  ▶ downloadUrl.await() failed", e)
            throw e
        }
        Log.d("LinLi", "  ▶ downloadUrl retrieved: $downloadUrl")
        return downloadUrl
    }

    /**
     * 取得 Firestore documents 中，field "id" 等於給定 id 的文件參考，若找不到回傳 null。
     */
    private suspend fun findPlaceDocRefByIdOrNull(id: String) =
        firestore.collection(ARTICLE_COLLECTION)
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()?.reference

    /**
     * Checks whether the user profile is completed based on the user's ID.
     *
     * @param id The unique identifier of the user whose profile completion status is to be checked.
     * @return A boolean value where `true` indicates that the user profile is completed, and `false` otherwise.
     */
    override suspend fun isUserProfileCompleted(id: String): Boolean {
        Log.i("LinLi", "isUserProfileCompleted: uid")
        val snapshot = firestore.collection(USER_COLLECTION)
            .document(id)
            .get()
            .await()
        return snapshot.exists() && snapshot.getBoolean("profileCompleted") == true
    }

    override suspend fun getCurrentUser(): UserDto? =
        firebaseAuth.currentUser?.uid?.let {
            firestore.document("$USER_COLLECTION/$it").get().await().toObject(UserDto::class.java)
        }


    /**
     * 取得所有使用者資料
     */
    override fun getUsers(): Flow<List<UserDto>> {
        return firestore.collection(USER_COLLECTION)
            .snapshots()
            .map {
                it.toObjects(UserDto::class.java)
            }
            .catch { e ->
                Log.e("LinLi", "getUsers() failed", e)
                emit(emptyList())
            }
    }

    /**
     * 新增使用者資料並回傳文件 ID
     */
    override suspend fun addUser(user: UserDto): String {
        val finalImageUrl = user.avatarUrl.resolveUri(
            onLocal = { uri ->
                uploadImage(
                    user.avatarUrl.toUri(),
                    storage.reference.child("$USER_IMAGE_STORAGE/${user.id}")
                )
            },
            onRemote = { it }
        )
        val docRef = firestore.collection(USER_COLLECTION).document(user.id)
        val userWithImageUrl = user.copy(avatarUrl = finalImageUrl)
        return try {
            docRef.set(userWithImageUrl).await()
            docRef.id
        } catch (e: Exception) {
            Log.e("LinLi", "addUser() failed", e)
            throw e
        }
    }

    /**
     * 更新使用者資料
     */
    override suspend fun updateUser(user: UserDto) {
        firestore.collection(USER_COLLECTION)
            .document(user.id)
            .set(user, SetOptions.merge())
            .await()
    }

    /**
     * 刪除使用者資料
     */
    override suspend fun deleteUser(id: String) {
        firestore.collection(USER_COLLECTION)
            .document(id)
            .delete()
            .await()
    }

    /**
     * 根據 ID 取得單一使用者資料
     */
    override fun getUserByIdFlow(id: String): Flow<UserDto> = flow {
        val snapshot = firestore.collection(USER_COLLECTION)
            .document(id)
            .get()
            .await()
        snapshot.toObject(UserDto::class.java)?.let { emit(it) }
    }.flowOn(Dispatchers.IO)
}