package com.happyplaces.presentation.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.compose.HappyPlacesTheme
import com.happyplaces.database.HappyPlace
import com.happyplaces.presentation.activities.MainActivity.Companion.EXTRA_PLACE_DETAILS
import com.happyplaces.presentation.ui.compose.MapScreen

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val happyPlace: HappyPlace? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_PLACE_DETAILS, HappyPlace::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PLACE_DETAILS)
        }
        enableEdgeToEdge()
        setContent {
            HappyPlacesTheme {
                happyPlace?.let { place ->
                    MapScreen(
                        place = place,
                        onBackClick = { finish() },
                        onInfoWindowClick = {}
                    )
                }
            }
        }
    }
}