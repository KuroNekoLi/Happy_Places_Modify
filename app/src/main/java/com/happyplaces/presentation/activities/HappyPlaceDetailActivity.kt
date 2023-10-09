package com.happyplaces.presentation.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.happyplaces.database.HappyPlace
import com.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.happyplaces.presentation.activities.MainActivity.Companion.EXTRA_PLACE_DETAILS

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlaceDetailBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var happyPlace: HappyPlace? = null
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            happyPlace =
                intent.getParcelableExtra(EXTRA_PLACE_DETAILS, HappyPlace::class.java)
        }

        if (happyPlace != null) {
            setSupportActionBar(binding.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlace.title

            binding.toolbarHappyPlaceDetail.setNavigationOnClickListener { onBackPressed() }

            binding.ivPlaceImage.setImageURI(happyPlace.image)
            binding.tvDescription.text = happyPlace.description
            binding.tvLocation.text = happyPlace.location

            binding.btnViewOnMap.setOnClickListener {
                val intent = Intent(this@HappyPlaceDetailActivity, MapActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, happyPlace)
                startActivity(intent)
            }
        }

    }
}