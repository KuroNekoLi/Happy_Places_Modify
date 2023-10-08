package com.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.happyplaces.database.HappyPlace
import com.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.presentation.di.HappyPlaceAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddHappyPlace.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }

        val myAdapter = HappyPlaceAdapter()
        binding.rvHappyPlace.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
        val happyPlaceList = listOf<HappyPlace>(
            HappyPlace(
                0,
                "測試",
                null,
                "測試",
                "",
                "",
                0.0,
                0.0
            )
        )

        myAdapter.submitList(happyPlaceList)
    }
}
