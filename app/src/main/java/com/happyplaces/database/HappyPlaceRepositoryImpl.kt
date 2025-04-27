package com.happyplaces.database

class HappyPlaceRepositoryImpl(private val dao: UserDao) : HappyPlaceRepository {
    override val dataList = dao.getAllData()
    override suspend fun insert(happyPlace: HappyPlace): Long = dao.insertData(happyPlace)
    override suspend fun update(happyPlace: HappyPlace): Int = dao.updateData(happyPlace)
    override suspend fun delete(happyPlace: HappyPlace): Int = dao.deleteData(happyPlace)
}