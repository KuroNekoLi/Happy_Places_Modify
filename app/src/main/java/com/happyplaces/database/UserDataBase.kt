package com.happyplaces.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HappyPlace::class],version = 1)
@TypeConverters(UriTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract val dao : UserDao

    companion object{
        @Volatile
        private var INSTANCE : UserDatabase? = null
        @SuppressLint("SuspiciousIndentation")
        fun getInstance(context: Context):UserDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "happy_place_data_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}