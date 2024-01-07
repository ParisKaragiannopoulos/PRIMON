package com.parisjohn.pricemonitoring

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.parisjohn.pricemonitoring.base.ui.BottomNavItem
import com.parisjohn.pricemonitoring.features.dashboard.presentation.DashboardScreen
import com.parisjohn.pricemonitoring.features.dashboard.presentation.tab.MonitorListScreen
import com.parisjohn.pricemonitoring.features.dashboard.presentation.tab.ProfileTabScreen
import com.parisjohn.pricemonitoring.features.dashboard.presentation.tab.SearchHotelScreen
import com.parisjohn.pricemonitoring.features.details.MonitorDetailScreen
import com.parisjohn.pricemonitoring.features.login.composable.LoginScreen
import com.parisjohn.pricemonitoring.features.login.composable.SignupScreen
import com.parisjohn.pricemonitoring.features.splash.SplashScreen
import com.parisjohn.pricemonitoring.features.verifyEmail.OTPScreen


@Composable
fun PriceMonitorApp() {
    val navController = rememberNavController()
    MonitorAppNavHost(
        navController = navController
    )
}

@Composable
fun MonitorAppNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(onSignUpClick = {
                navController.navigate(Screen.Signup.route)
            }, onLoginClick = {
                navController.navigate(Screen.Dashboard.route)
            })
        }
        composable(route = Screen.Splash.route) {
            SplashScreen(goToDashboard = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                }
            }, goToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(route = Screen.Signup.route) {
            SignupScreen(
                onBackClick = { navController.navigateUp() },
                onValidateEmail = { navController.navigate(Screen.OTP.route) })
        }
        composable(route = Screen.OTP.route) {
            OTPScreen() {
                navController.navigate(Screen.Login.route)
            }
        }
        composable(route = Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}

@Composable
fun HomeNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
) {

    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {

        composable(BottomNavItem.Home.route) { MonitorListScreen(
            onMonitorClick = {
                navController.navigate(Screen.HotelDetailScreen.createRoute(it.monitorListID.toString()))
            }
        ) }
        composable(BottomNavItem.Search.route) { SearchHotelScreen() }
        composable(BottomNavItem.Profile.route) { ProfileTabScreen() }

        composable(
            route = Screen.HotelDetailScreen.route,
            arguments = Screen.HotelDetailScreen.navArguments
        ) {
            MonitorDetailScreen(
                onBackClick = { navController.navigateUp() },
            )
        }
    }
}
