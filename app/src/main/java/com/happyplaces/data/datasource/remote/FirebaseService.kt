package com.happyplaces.data.datasource.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import java.util.UUID

/** 文章資料庫 */
const val ARTICLE_COLLECTION = "articles"

/** Firebase Storage 路徑 */
const val ARTICLE_IMAGE_STORAGE = "article_images"

class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlaceService {
    // Firebase Storage 實例
    private val storage: FirebaseStorage = Firebase.storage

    private companion object {
        private const val TAG = "FirebaseService"
    }

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
        // 1. 方法一開始就印出來，確認有沒有呼到
        Log.d("LinLi", "addPlace() called with place: $place")

        val docRef = firestore.collection(ARTICLE_COLLECTION).document()
        var withId = place

        // 2. 只有在 imageUrl 非 null 時才上傳
        place.imageUrl.let { raw ->
            Log.d("LinLi", "  ▶ imageUrl non-null, 開始上傳前準備")
            val uri = raw.toUri()
            val finalUrl = if (uri.scheme == "content" || uri.scheme == "file") {
                uploadImage(uri, docRef.id)
            } else {
                raw
            }
            Log.d("LinLi", "  ▶ uploadImage 完成，拿到 URL: $finalUrl")
            withId = withId.copy(imageUrl = finalUrl)
        }

        // 3. 寫入 Firestore，並捕捉例外
        try {
            Log.d("LinLi", "  ▶ 準備寫入 Firestore，withId = $withId")
            docRef.set(withId).await()
            Log.d("LinLi", "Document written for ID: ${docRef.id}")
        } catch (e: Exception) {
            Log.e("LinLi", "  ▶ Failed to write place document", e)
            throw e
        }

        return docRef.id
    }
    override suspend fun updatePlace(place: PlaceDto) {
        var updated = place
        place.imageUrl.let { raw ->
            val uri = raw.toUri()
            if (uri.scheme == "content" || uri.scheme == "file") {
                val newUrl = uploadImage(uri, place.id)
                updated = updated.copy(imageUrl = newUrl)
            }
        }
        firestore.collection(ARTICLE_COLLECTION)
            .document(place.id)
            .set(updated, SetOptions.merge())
            .await()
    }

    override suspend fun deletePlace(id: String) {
        firestore.collection(ARTICLE_COLLECTION)
            .document(id)
            .delete()
            .await()  // 需引入 kotlinx-coroutines-play-services 才能 await()
    }

    override fun getPlaceByIdFlow(id: String): Flow<PlaceDto> =
        firestore.collection(ARTICLE_COLLECTION)
            .document(id)
            .snapshots()
            .mapNotNull { it.toObject(PlaceDto::class.java)?.copy(id = it.id) }

    /**
     * 上傳本地圖片至 Firebase Storage，並回傳下載 URL
     *
     * @param localUri 本地圖片 Uri
     * @param placeId  對應的文章 ID
     * @return 圖片的公開下載 URL
     */
    suspend fun uploadImage(localUri: Uri, placeId: String): String {
        Log.d("LinLi", "uploadImage() called with localUri=$localUri, placeId=$placeId")
        Log.d("LinLi", "  ▶ uploadImage start putFile")
        // 1. 建立 storage 參考
        val imageRef = storage.reference
            .child("$ARTICLE_IMAGE_STORAGE/$placeId/${UUID.randomUUID()}")
        // 2. 上傳檔案
        try {
            imageRef.putFile(localUri).await()
            Log.d("LinLi", "  ▶ putFile.await() returned, upload complete")
        } catch (e: Exception) {
            Log.e("LinLi", "  ▶ putFile.await() failed", e)
            throw e
        }
        // 3. 取得並回傳下載 URL
        val downloadUrl: String = try {
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("LinLi", "  ▶ downloadUrl.await() failed", e)
            throw e
        }
        Log.d("LinLi", "  ▶ downloadUrl retrieved: $downloadUrl")
        return downloadUrl
    }
}