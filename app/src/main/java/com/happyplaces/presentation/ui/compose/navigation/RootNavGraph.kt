package com.happyplaces.presentation.ui.compose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.happyplaces.presentation.ui.compose.onboarding.EmailStepScreen
import com.happyplaces.presentation.ui.compose.onboarding.ProfileStepScreen
import com.happyplaces.presentation.ui.compose.onboarding.UsernameStepScreen
import com.happyplaces.presentation.ui.compose.onboarding.WelcomeStepScreen
import kotlinx.serialization.Serializable


@Serializable
    data object Email
@Serializable
    data object Username
@Serializable
    data object Profile
@Serializable
    data object Welcome


fun NavGraphBuilder.onboardingNavGraph(
    navController: NavHostController
) {
    navigation<Onboarding>(
        startDestination = Email
    ) {
        composable<Email> { EmailStepScreen { navController.navigate(Username) } }
        composable<Username> {
            UsernameStepScreen {
                navController.navigate(
                    Profile
                )
            }
        }
        composable<Profile>{
            ProfileStepScreen {
                navController.navigate(
                    Welcome
                )
            }
        }
        composable<Welcome>{
            WelcomeStepScreen {
                // 完成後把 Graph 設定為 Main，並清空 backStack
                navController.navigate(Home) {
                    popUpTo(Onboarding) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}
