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
        // 回到該 NavGraph 的 startDestination，但 **不要**保存
        // 目前頂層目的地以下的子堆疊（例如 Add、Detail 等非頂層頁面），
        // 以避免「Recommend → Add → Profile → Recommend」時
        // 還原到 Add 的情況。
        popUpTo(graph.findStartDestination().id) {
            saveState = false   // 不保存子目的地的 back stack
        }
        launchSingleTop = true      // 避免建立重複實例
        restoreState = true         // 若先前已儲存頂層目的地本身的狀態，則還原
    }
}

@Composable
fun rememberCurrentDestination(navController: NavHostController): NavDestination? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination
}