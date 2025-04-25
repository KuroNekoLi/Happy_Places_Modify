package com.happyplaces.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HappyPlace::class], version = 1)
@TypeConverters(UriTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {
    abstract val dao: UserDao
}