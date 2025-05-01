package com.happyplaces.data.repository

import com.happyplaces.data.model.HappyPlace
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockHappyPlaceRepository(
    private val mockHappyPlaceLists: MutableList<HappyPlace> = mutableListOf()
) : HappyPlaceRepository {
    // Create：傳回新插入的 rowId（模擬為目前 size + 1）
    override fun insert(happyPlace: HappyPlace): Flow<ApiResource<Long>> = flow {
        emit(ApiResource.Loading())
        val newId = (mockHappyPlaceLists.size + 1).toLong()
        // 模擬新增到本地
        mockHappyPlaceLists.add(happyPlace.copy(id = newId.toString()))
        emit(ApiResource.Success(newId))
    }

    // Update：傳回修改的 index；找不到就回報 Error
    override fun update(happyPlace: HappyPlace): Flow<ApiResource<Int>> = flow {
        emit(ApiResource.Loading())
        val idx = mockHappyPlaceLists.indexOfFirst { it.id == happyPlace.id }
        if (idx >= 0) {
            mockHappyPlaceLists[idx] = happyPlace
            emit(ApiResource.Success(idx))
        } else {
            emit(ApiResource.Error("找不到 id = ${happyPlace.id} 的項目"))
        }
    }

    // Delete：傳回刪除的 index；找不到就回報 Error
    override fun delete(happyPlace: HappyPlace): Flow<ApiResource<Int>> = flow {
        emit(ApiResource.Loading())
        val idx = mockHappyPlaceLists.indexOfFirst { it.id == happyPlace.id }
        if (idx >= 0) {
            mockHappyPlaceLists.removeAt(idx)
            emit(ApiResource.Success(idx))
        } else {
            emit(ApiResource.Error("找不到 id = ${happyPlace.id} 的項目"))
        }
    }

    // Read single：先發 Loading，再發 Success/​Error
    override  fun getHappyPlaceById(id: String): Flow<ApiResource<HappyPlace>> = flow {
        emit(ApiResource.Loading())
        val item = mockHappyPlaceLists.find { it.id == id }
        if (item != null) {
            emit(ApiResource.Success(item))
        } else {
            emit(ApiResource.Error("查無 id = $id 的資料"))
        }
    }

    // Read all：包裝成 ApiResource 的列表 Flow
    override fun getAllHappyPlaces(): Flow<ApiResource<List<HappyPlace>>> = flow {
        emit(ApiResource.Loading())
        emit(ApiResource.Success(mockHappyPlaceLists.toList()))
    }
}
