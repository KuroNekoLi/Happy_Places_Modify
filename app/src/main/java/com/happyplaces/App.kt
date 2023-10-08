package com.happyplaces

import android.app.Application
import com.happyplaces.database.HappyPlaceRepository
import com.happyplaces.database.UserDao
import com.happyplaces.database.UserDatabase
import com.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.presentation.di.HappyPlaceAdapter
import com.happyplaces.presentation.di.HappyPlaceViewModel
import com.happyplaces.presentation.di.HappyPlaceViewModelFactory
import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    private lateinit var dao: UserDao
    private lateinit var repository: HappyPlaceRepository
    lateinit var factory: HappyPlaceViewModelFactory

    override fun onCreate() {
        super.onCreate()
        instance = this

        dao = UserDatabase.getInstance(this).dao
        repository = HappyPlaceRepository(dao)
        factory = HappyPlaceViewModelFactory(repository)
    }
}