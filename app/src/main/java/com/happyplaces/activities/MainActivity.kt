package com.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.happyplaces.App
import com.happyplaces.database.HappyPlace
import com.happyplaces.database.HappyPlaceRepository
import com.happyplaces.database.UserDatabase
import com.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.presentation.di.HappyPlaceAdapter
import com.happyplaces.presentation.di.HappyPlaceViewModel
import com.happyplaces.presentation.di.HappyPlaceViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HappyPlaceViewModel

    private val factory by lazy { App.instance.factory }
    private lateinit var mAdapter: HappyPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        val dao = UserDatabase.getInstance(application).dao
//        val repository = HappyPlaceRepository(dao)
//        val factory = HappyPlaceViewModelFactory(repository)

        mAdapter = HappyPlaceAdapter()
        binding.rvHappyPlace.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel = ViewModelProvider(this,factory)[HappyPlaceViewModel::class.java]
        viewModel.apply {
            message.observe(this@MainActivity){
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
            getDataList().observe(this@MainActivity){
                mAdapter.submitList(it)
            }
        }
        setContentView(binding.root)

        binding.fabAddHappyPlace.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }
    }
}
