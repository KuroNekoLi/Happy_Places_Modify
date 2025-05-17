package com.happyplaces.presentation.ui.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState

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