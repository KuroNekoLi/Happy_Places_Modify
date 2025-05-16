package com.happyplaces.data.datasource.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/** 文章資料庫 */
const val ARTICLE_COLLECTION = "articles"

/** Firebase Storage 路徑 */
const val ARTICLE_IMAGE_STORAGE = "article_images"

class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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
        val finalImageUrl = place.imageUrl.let { raw ->
            val uri = raw.toUri()
            if (uri.scheme == "content" || uri.scheme == "file") {
                uploadImage(uri, storage.reference.child("$ARTICLE_IMAGE_STORAGE/${place.id}"))
            } else {
                raw
            }
        }

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

        // 3. 查詢 firestore 中以 field "id" 為條件的文件並取得參考
        val querySnapshot = firestore
            .collection(ARTICLE_COLLECTION)
            .whereEqualTo("id", place.id)
            .limit(1)
            .get()
            .await()

        val docRef = querySnapshot.documents
            .firstOrNull()?.reference
            ?: throw NoSuchElementException("找不到 id = ${place.id} 的文件")

        // 4. 使用該文件參考更新內容
        docRef.set(updated, SetOptions.merge())
            .await()
    }

    override suspend fun deletePlace(id: String) {
        // 1. 執行查詢並取得最多一筆結果
        val querySnapshot = firestore
            .collection(ARTICLE_COLLECTION)
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()

        // 2. 如果有符合的文件，就呼叫 delete()
        querySnapshot.documents.firstOrNull()?.reference
            ?.delete()
            ?.await()
    }

    override fun getPlaceByIdFlow(id: String): Flow<PlaceDto> = flow {
        val querySnapshot = firestore
            .collection(ARTICLE_COLLECTION)
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()

        querySnapshot.documents
            .firstOrNull()
            ?.toObject(PlaceDto::class.java)?.also {
                emit(it)
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
}