package com.happyplaces.database

class HappyPlaceRepository(private val dao: UserDao) {

    val dataList = dao.getAllData()

    suspend fun insert(happyPlace: HappyPlace): Long = dao.insertData(happyPlace)
    suspend fun update(happyPlace: HappyPlace): Int = dao.updateData(happyPlace)
    suspend fun delete(happyPlace: HappyPlace): Int = dao.deleteData(happyPlace)
}