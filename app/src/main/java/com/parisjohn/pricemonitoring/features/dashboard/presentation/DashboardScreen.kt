package com.parisjohn.pricemonitoring.features.dashboard.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.parisjohn.pricemonitoring.HomeNavHost
import com.parisjohn.pricemonitoring.base.ui.BottomNavItem
import com.parisjohn.pricemonitoring.base.ui.BottomNavigationBar
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme

@Composable
fun DashboardScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            HomeNavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                navController = navController,
                startDestination = BottomNavItem.Home.route,
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    PriceMonitoringTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DashboardScreen()
        }
    }
}