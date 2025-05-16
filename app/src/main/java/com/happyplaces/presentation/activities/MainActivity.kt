package com.happyplaces.presentation.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.gms.tasks.Task
import com.happyplaces.R
import com.happyplaces.presentation.ui.compose.navigation.HappyPlaceNavHost
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.AuthUiState
import com.happyplaces.presentation.ui.viewmodel.AuthViewModel
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<HappyPlaceViewModel>()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->

    }
    private val authViewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                authViewModel.authUiState.collect {
                    when (it) {
                        is AuthUiState.LoggedIn -> {
                            keepSplash = false
                            showMainScreen(it.user.profileCompleted)
                        }

                        is AuthUiState.LoggedOut -> {
                            // Choose authentication providers
                            keepSplash = false
                            val providers = arrayListOf(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.AnonymousBuilder().build()
                            )
                            showLoginUI(providers)
                        }

                        is AuthUiState.Loading -> {}

                        is AuthUiState.Error -> {
                            keepSplash = false
                        }
                    }
                }
            }
        }
        viewModel.updateAllHappyPlaces()

        viewModel.apply {
            message.observe(this@MainActivity) {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun MainActivity.showLoginUI(providers: ArrayList<AuthUI.IdpConfig>): Task<Void?> =
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // Create and launch sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.icon) // Set logo drawable
                    .setTheme(R.style.AppTheme) // Set theme
                    .setCredentialManagerEnabled(false)
                    .build()
                signInLauncher.launch(signInIntent)
            }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showMainScreen(isRegistered: Boolean) {
        setContent {
            val navController = rememberNavController()
            HappyPlacesTheme {
                HappyPlaceNavHost(
                    navController = navController,
                    isRegistered = isRegistered
                )
            }
        }
    }
}

