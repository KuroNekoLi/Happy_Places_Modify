package com.happyplaces.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.happyplaces.presentation.ui.compose.HappyPlaceDetailScreen
import com.happyplaces.presentation.ui.compose.MainScreen
import kotlinx.serialization.Serializable


@Serializable
object Add
@Serializable
data class Edit(val id: Int)
@Serializable
data class Detail(val id: Int)
@Serializable
object Map
@Serializable
object Home

@Composable
fun HappyPlaceNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            MainScreen(
                onAddClick = { navController.navigate(Add) },
                onEdit = { haHapPlace ->
                    navController.navigate(Edit(haHapPlace.id))
                },
                onItemClick = { haHapPlace ->
                    navController.navigate(Detail(haHapPlace.id))
                }
            )
        }
        composable<Detail> { backStackEntry ->
            val id:Int = backStackEntry.toRoute()
            HappyPlaceDetailScreen(id = id,onBackClick = { navController.popBackStack() }, onViewOnMapClick = { })
        }
    }
}