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
data object Add
@Serializable
data class Edit(val id: String)
@Serializable
data class Detail(val id: String)
@Serializable
data class Map(val id: String)
@Serializable
data object Home

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
                onViewOnMapClick = { navController.navigate(Map(detail.id)) }
            )
        }
        composable<Map> {
            MapScreen(id = it.toRoute<Map>().id,onBackClick = { navController.popBackStack() })
        }
        composable<Add> {
            AddHappyPlaceScreen { navController.popBackStack() }
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
