package com.happyplaces.presentation.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.happyplaces.R
import com.happyplaces.presentation.ui.compose.navigation.HappyPlaceNavHost
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.AuthViewModel
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<HappyPlaceViewModel>()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
    private val authViewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            // keep splash screen visible while registration status is still loading
            authViewModel.isRegistered.value == null
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                authViewModel.showToast.collect {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.updateAllHappyPlaces()
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )
        Firebase.auth.currentUser?.let {
            Log.i("LinLi", "onCreate: currentUser = $it")
            showMainScreen()
        } ?: run {
            Log.i("LinLi", "onCreate: currentUser is null")

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
                    showMainScreen()
                }
        }

        viewModel.apply {
            message.observe(this@MainActivity) {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }

    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.i("LinLi", "onSignInResult: $user")
            user?.uid?.let {
                Log.i("LinLi", "onSignInResult: uid = $it")
                authViewModel.checkUserProfileCompleted(it)
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Log.i("LinLi", "onSignInResult error: ${response?.getError()?.getErrorCode()}")
        }
    }

    private fun showMainScreen() {
        setContent {
            val navController = rememberNavController()
            val isRegistered by authViewModel.isRegistered.collectAsState()
            isRegistered?.let {
                HappyPlacesTheme {
                    HappyPlaceNavHost(
                        navController = navController,
                        isRegistered = it
                    )
                }
            }

        }
    }
}

