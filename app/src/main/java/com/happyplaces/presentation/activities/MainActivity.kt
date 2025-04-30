package com.happyplaces.presentation.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.HappyPlaceNavHost
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<HappyPlaceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            HappyPlacesTheme {
                HappyPlaceNavHost(navController = navController)
//                MainScreen(
//                    onAddClick = {
//                        startActivity(Intent(this@MainActivity, AddHappyPlaceActivity::class.java))
//                    },
//                    onEdit = { happyPlace:HappyPlace ->
//                        Intent(this@MainActivity, AddHappyPlaceActivity::class.java).let {
//                            it.putExtra(EXTRA_PLACE_DETAILS, happyPlace)
//                            startActivity(it)
//                        }
//                    },
//                    onItemClick = { happyPlace:HappyPlace ->
//                        Log.i("LinLi", "onItemClick")
//                        Intent(this@MainActivity, HappyPlaceDetailActivity::class.java).let {
//                            it.putExtra(EXTRA_PLACE_DETAILS, happyPlace)
//                            startActivity(it)
//                        }
//                    }
//                )
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
