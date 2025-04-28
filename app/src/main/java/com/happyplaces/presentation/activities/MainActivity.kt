package com.happyplaces.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.compose.HappyPlacesTheme
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.compose.MainScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<HappyPlaceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyPlacesTheme {
                MainScreen(
                    onAddClick = {
                        startActivity(Intent(this@MainActivity, AddHappyPlaceActivity::class.java))
                    },
                    onEdit = { happyPlace ->
                        Intent(this@MainActivity, AddHappyPlaceActivity::class.java).let {
                            it.putExtra(EXTRA_PLACE_DETAILS, happyPlace)
                            startActivity(it)
                        }
                    },
                    onDelete = { happyPlace ->
                        viewModel.delete(happyPlace)
                    },
                    onItemClick = { happyPlace ->
                        Intent(this@MainActivity, HappyPlaceDetailActivity::class.java).let {
                            it.putExtra(EXTRA_PLACE_DETAILS, happyPlace)
                            startActivity(it)
                        }
                    }
                )
            }
        }

        viewModel.apply {
            message.observe(this@MainActivity) {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_PLACE_DETAILS = "EXTRA_PLACE_DETAILS"
    }
}
