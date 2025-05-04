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
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.HappyPlaceNavHost
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<HappyPlaceViewModel>()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                user.uid.apply { Log.i("LinLi", "uid: $this") }
                // 使用者已登入，user.uid / user.email… 都可拿到
            } else {
                Log.i("LinLi", "logout")
                // 使用者已登出
            }
        }
//
//        AuthUI.getInstance()
//            .signOut(this)
//            .addOnCompleteListener {
//                // Create and launch sign-in intent
//                val signInIntent = AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .setAvailableProviders(providers)
//                    .setLogo(R.drawable.icon) // Set logo drawable
//                    .setTheme(R.style.AppTheme) // Set theme
//                    .setCredentialManagerEnabled(false)
//                    .build()
//                signInLauncher.launch(signInIntent)
//            }


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
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Log.i("LinLi", "onSignInResult error: ${response?.getError()?.getErrorCode()}")
        }
    }

}
