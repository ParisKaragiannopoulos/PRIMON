package com.parisjohn.pricemonitoring.base.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.parisjohn.pricemonitoring.ui.theme.Purple40

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Purple40,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val bottomNavigationItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Profile,
        )
        bottomNavigationItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}