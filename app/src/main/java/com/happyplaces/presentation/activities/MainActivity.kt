package com.happyplaces.presentation.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.happyplaces.data.datasource.remote.PlaceRemoteDataSource
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.HappyPlaceNavHost
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<HappyPlaceViewModel>()
    private val remoteDataSource by inject<PlaceRemoteDataSource>()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Log.i("LinLi", "signInLauncher: $res")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build(),
        )

// Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        setContent {
            val navController = rememberNavController()
            HappyPlacesTheme {
                HappyPlaceNavHost(navController = navController)
            }
        }

        viewModel.apply {
            message.observe(this@MainActivity) {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
