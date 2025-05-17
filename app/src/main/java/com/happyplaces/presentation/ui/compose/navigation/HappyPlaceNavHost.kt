package com.happyplaces.presentation.ui.compose.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.happyplaces.presentation.ui.compose.AddHappyPlaceScreen
import com.happyplaces.presentation.ui.compose.HappyPlaceDetailScreen
import com.happyplaces.presentation.ui.compose.MainScaffold
import com.happyplaces.presentation.ui.compose.MapScreen
import com.happyplaces.presentation.ui.compose.MyMap
import com.happyplaces.presentation.ui.compose.Profile
import com.happyplaces.presentation.ui.compose.Recommend
import com.happyplaces.presentation.ui.compose.RecommendScreen
import com.happyplaces.presentation.ui.compose.Search
import com.happyplaces.presentation.ui.compose.profile.ProfileScreen
import com.happyplaces.presentation.ui.compose.profile.SettingsScreen
import com.happyplaces.presentation.ui.viewmodel.AuthViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


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

@Serializable
data object Onboarding

@Serializable
data object Settings

@Composable
fun HappyPlaceNavHost(
    navController: NavHostController,
    isRegistered: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isRegistered) Home else Onboarding
    ) {
        onboardingNavGraph(navController)
        mainNavGraph(navController)
    }
}

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation<Home>(startDestination = Recommend) {
        composable<Recommend> {
            val currentDestination = rememberCurrentDestination(navController)
            MainScaffold(
                currentDestination = currentDestination,
                onBottomNavigate = navController::navigateTopLevel,
                onAddClick = { navController.navigate(Add) },
                content = { paddingValues ->
                    RecommendScreen(
                        modifier = Modifier.padding(paddingValues),
                        onEdit = { haHapPlace ->
                            navController.navigate(Edit(haHapPlace.id))
                        },
                        onItemClick = { haHapPlace ->
                            navController.navigate(Detail(haHapPlace.id))
                        }
                    )
                }
            )
        }
        composable<Profile> {
            val currentDestination = rememberCurrentDestination(navController)
            MainScaffold(
                currentDestination = currentDestination,
                onBottomNavigate = navController::navigateTopLevel,
                onAddClick = { navController.navigate(Add) },
                content = { paddingValues ->
                    ProfileScreen(modifier = Modifier.padding(paddingValues)){
                        navController.navigate(Settings)
                    }
                }
            )
        }
        composable<Settings> {
            val authViewModel: AuthViewModel = koinViewModel()
            SettingsScreen{
                authViewModel.signOut()
            }
        }
        composable<MyMap> {
            val currentDestination = rememberCurrentDestination(navController)
            MainScaffold(
                currentDestination = currentDestination,
                onBottomNavigate = navController::navigateTopLevel,
                onAddClick = { navController.navigate(Add) },
                content = { paddingValues ->
                    Text(
                        modifier = Modifier.padding(paddingValues),
                        text = "MyMap"
                    )
                }
            )
        }
        composable<Search> {
            val currentDestination = rememberCurrentDestination(navController)
            MainScaffold(
                currentDestination = currentDestination,
                onBottomNavigate = navController::navigateTopLevel,
                onAddClick = { navController.navigate(Add) },
                content = { paddingValues ->
                    Text(
                        modifier = Modifier.padding(paddingValues),
                        text = "Search"
                    )
                }
            )
        }
        composable<Detail> { backStackEntry ->
            val detail: Detail = backStackEntry.toRoute()
            HappyPlaceDetailScreen(
                id = detail.id,
                onBackClick = { navController.popBackStack() },
                onViewOnMapClick = { navController.navigate(Map(detail.id)) }
            )
        }
        composable<Map> {
            MapScreen(id = it.toRoute<Map>().id, onBackClick = { navController.popBackStack() })
        }
        composable<Add> {
            AddHappyPlaceScreen { navController.popBackStack() }
        }
        composable<Edit> { backStackEntry ->
            val detail: Edit = backStackEntry.toRoute()
            AddHappyPlaceScreen(
                id = detail.id,
                onBack = { navController.popBackStack() }
            )
        }
    }

}

/**
 * 讓所有「頂層目的地」都用同一組 NavOptions。
 * 若使用 typed navigation (composable<Foo>)，把 route 型別調成 Any。
 */
fun NavController.navigateTopLevel(route: Any) {
    navigate(route) {
        // 回到 graph 的 startDestination，並保留其狀態
        popUpTo(graph.findStartDestination().id) { saveState = true }
        // 避免在 back stack 上出現多個相同目的地
        launchSingleTop = true
        // 若目的地曾被儲存過，回復其狀態
        restoreState = true
    }
}

@Composable
fun rememberCurrentDestination(navController: NavHostController): NavDestination? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination
}