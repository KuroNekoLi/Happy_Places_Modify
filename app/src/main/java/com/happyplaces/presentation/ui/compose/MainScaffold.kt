package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.happyplaces.R
import kotlinx.serialization.Serializable


data class BottomNavigationRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

@Serializable
data object Profile

@Serializable
data object MyMap

@Serializable
data object Search

@Serializable
data object Recommend

val bottomNavigationRoutes = listOf(
    BottomNavigationRoute("Recommend", Recommend, Icons.Default.Recommend),
    BottomNavigationRoute("Search", Search, Icons.Default.Search),
    BottomNavigationRoute("My Map", MyMap, Icons.Default.Map),
    BottomNavigationRoute("Profile", Profile, Icons.Default.Person),
)

@Composable
fun MainScaffold(
    currentDestination: NavDestination?,
    onBottomNavigate: (Any) -> Unit,
    onAddClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavigationRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                topLevelRoute.icon,
                                contentDescription = topLevelRoute.name
                            )
                        },
                        label = { Text(topLevelRoute.name) },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                        onClick = {
                            onBottomNavigate(topLevelRoute.route)
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                        .offset(y = 45.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }
        },
        topBar = {
            HappyPlaceToolBar(false, stringResource(id = R.string.app_name))
        },
        floatingActionButtonPosition = FabPosition.Center,

        ) { innerPadding ->
        content(innerPadding)
    }
}


@Preview
@Composable
fun MainScaffoldPreview() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    MainScaffold(
        currentDestination = currentDestination,
        onBottomNavigate = { topLevelRoute ->
            navController.navigate(topLevelRoute) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        },
        content = { paddingValues ->
            NavHost(navController, startDestination = Profile, Modifier.padding(paddingValues)) {
                composable<Profile> { Text("Profile") }
                composable<MyMap> { Text("MyMap") }
            }
        }
    )
}