package com.happyplaces.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(happyPlaceEntity: HappyPlaceEntity): Long

    @Update
    suspend fun updateData(happyPlaceEntity: HappyPlaceEntity): Int

    @Delete
    suspend fun deleteData(happyPlaceEntity: HappyPlaceEntity): Int

    @Query("SELECT * FROM place_data_table")
    fun getAllData(): Flow<List<HappyPlaceEntity>>

    @Query("SELECT * FROM place_data_table WHERE id = :id")
    fun getHappyPlaceById(id: Int): HappyPlaceEntity?
}