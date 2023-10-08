package com.happyplaces.database

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
    suspend fun insertData(happyPlace: HappyPlace) : Long

    @Update
    suspend fun updateData(happyPlace: HappyPlace) : Int

    @Delete
    suspend fun deleteData(happyPlace: HappyPlace) : Int
    @Query("SELECT * FROM place_data_table")
    fun getAllData() : Flow<List<HappyPlace>>
}