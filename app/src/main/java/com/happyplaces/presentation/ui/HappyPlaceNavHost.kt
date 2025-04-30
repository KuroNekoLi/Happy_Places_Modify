package com.happyplaces.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.happyplaces.presentation.ui.compose.AddHappyPlaceScreen
import com.happyplaces.presentation.ui.compose.HappyPlaceDetailScreen
import com.happyplaces.presentation.ui.compose.MainScreen
import com.happyplaces.presentation.ui.compose.MapScreen
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
    navController: NavHostController
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
            val detail:Detail = backStackEntry.toRoute()
            HappyPlaceDetailScreen(
                id = detail.id,
                onBackClick = { navController.popBackStack() },
                onViewOnMapClick = { }
            )
        }
        composable<Map> {
            MapScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Add> {
            AddHappyPlaceScreen(

            ) { navController.popBackStack() }
        }
        composable<Edit> { backStackEntry ->
            val detail:Edit = backStackEntry.toRoute()
            AddHappyPlaceScreen(
                id = detail.id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
