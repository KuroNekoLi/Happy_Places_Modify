package com.happyplaces.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HappyPlaceRepositoryTest {

    private lateinit var database: UserDatabase
    private lateinit var dao: UserDao
    private lateinit var repository: HappyPlaceRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.dao
        repository = HappyPlaceRepository(dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insert_happy_place() = runBlocking {
        val place = HappyPlace(0, "Title", null, "Desc", "Date", "Location", 0.0, 0.0)
        val id = repository.insert(place)

        val list = repository.dataList.first()
        assertEquals(1, list.size)
        assertEquals(id.toInt(), list[0].id)
    }

    @Test
    fun update_happy_place() = runBlocking {
        val place = HappyPlace(0, "Title", null, "Desc", "Date", "Location", 0.0, 0.0)
        val id = repository.insert(place)
        val inserted = repository.dataList.first()[0]
        val updated = inserted.copy(title = "Updated")

        repository.update(updated)

        val list = repository.dataList.first()
        assertEquals(1, list.size)
        assertEquals("Updated", list[0].title)
    }

    @Test
    fun delete_happy_place() = runBlocking {
        val place = HappyPlace(0, "Title", null, "Desc", "Date", "Location", 0.0, 0.0)
        repository.insert(place)
        val inserted = repository.dataList.first()[0]

        repository.delete(inserted)

        val list = repository.dataList.first()
        assertTrue(list.isEmpty())
    }
}

