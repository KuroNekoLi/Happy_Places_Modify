package com.happyplaces.presentation.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.happyplaces.R
import com.happyplaces.database.HappyPlace
import com.happyplaces.databinding.ActivityMapBinding
import com.happyplaces.presentation.activities.MainActivity.Companion.EXTRA_PLACE_DETAILS
import com.google.android.gms.maps.SupportMapFragment as SupportMapFragment1

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private var happyPlace : HappyPlace? = null
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(EXTRA_PLACE_DETAILS)){
            happyPlace = intent.getParcelableExtra(EXTRA_PLACE_DETAILS,HappyPlace::class.java)
        }

        if (happyPlace!=null){
            setSupportActionBar(binding.toolbarMap)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlace!!.title
            binding.toolbarMap.setNavigationOnClickListener { onBackPressed() }

            val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync {googleMap->
                val position = LatLng(happyPlace!!.latitude, happyPlace!!.longitude)
                googleMap.addMarker(MarkerOptions().position(position).title(happyPlace!!.location))
                val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
                googleMap.animateCamera(newLatLngZoom)
            }
        }
    }
}