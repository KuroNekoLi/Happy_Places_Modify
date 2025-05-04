package com.happyplaces.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.happyplaces.util.UriTypeConverter

@Database(entities = [HappyPlaceEntity::class], version = 4)
@TypeConverters(UriTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {
    abstract val dao: UserDao
}