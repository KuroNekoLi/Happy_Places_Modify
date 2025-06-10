package com.happyplaces.presentation.ui.compose.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.happyplaces.presentation.ui.compose.AddHappyPlaceScreen
import com.happyplaces.presentation.ui.compose.HappyPlaceDetailScreen
import com.happyplaces.presentation.ui.compose.MapScreen
import com.happyplaces.presentation.ui.compose.MyMapScreen
import com.happyplaces.presentation.ui.compose.RecommendScreen
import com.happyplaces.presentation.ui.compose.SearchScreen
import com.happyplaces.presentation.ui.compose.common.MainScaffold
import com.happyplaces.presentation.ui.compose.profile.ProfileScreen
import com.happyplaces.presentation.ui.compose.profile.SettingsScreen
import com.happyplaces.presentation.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

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
                    ProfileScreen(
                        modifier = Modifier.padding(paddingValues),
                        onSettingsClick = { navController.navigate(Settings) },
                        onItemClick = { haHapPlace ->
                            navController.navigate(Detail(haHapPlace.id))
                        },
                        onEdit = { haHapPlace ->
                            navController.navigate(Edit(haHapPlace.id))
                        }
                    )
                }
            )
        }
        composable<Settings> {
            val authViewModel: AuthViewModel = koinViewModel(
                viewModelStoreOwner = LocalActivity.current as ComponentActivity
            )
            SettingsScreen {
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
                    MyMapScreen(
                        modifier = Modifier.padding(paddingValues),
                        onMarkerClick = { haHapPlace ->
                            navController.navigate(Detail(haHapPlace.id))
                        }
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
                    SearchScreen(
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
            AddHappyPlaceScreen { navController.navigateTopLevel(Profile) }
        }
        composable<Edit> { backStackEntry ->
            val detail: Edit = backStackEntry.toRoute()
            AddHappyPlaceScreen(
                id = detail.id,
                onBack = { navController.navigateTopLevel(Profile) }
            )
        }
    }
}
