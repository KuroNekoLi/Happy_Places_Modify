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

//        val db = FirebaseFirestore.getInstance()
//        val articleRef = db.collection("articles").document()  // 自動 ID
//        val data = mapOf(
//            "creatorId" to "123",
//            "title" to "test",
//            "description" to "test content",
//            "visitDate" to 1682832000000,      // long (毫秒)
//            "address" to "taipei",
//            "latitude" to 25.0330,             // double
//            "longitude" to 121.5654,           // double
//            "imageUrl" to "https://cdn2.ettoday.net/activity/images/115/article_25908_1_b.jpg",
//            "createdAt" to System.currentTimeMillis()
//        )
//        articleRef.set(data)

//        db.collection("articles")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d("LinLi", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.w("TAG", "Error getting documents.", e)
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
}
