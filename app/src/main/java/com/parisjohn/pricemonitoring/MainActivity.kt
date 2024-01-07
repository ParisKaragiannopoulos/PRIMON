package com.parisjohn.pricemonitoring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PriceMonitoringTheme {
                PriceMonitorApp()
            }
        }
    }
}