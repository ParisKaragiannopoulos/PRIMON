package com.parisjohn.pricemonitoring

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument


sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    object Splash: Screen("splash")
    object Login : Screen("login")
    object Signup: Screen("signup")
    object OTP: Screen("otp")
    object Dashboard: Screen("dashboard")
    object HotelDetailScreen : Screen(
        route = "monitorList/{monitorId}",
        navArguments = listOf(navArgument("monitorId") {
            type = NavType.StringType
        })
    ) {
        fun createRoute(monitorId: String) = "monitorList/${monitorId}"
    }
}