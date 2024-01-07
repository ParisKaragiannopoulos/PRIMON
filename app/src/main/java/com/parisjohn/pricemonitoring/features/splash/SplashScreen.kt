package com.parisjohn.pricemonitoring.features.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisjohn.pricemonitoring.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(splashViewModel: SplashViewModel = hiltViewModel(),
    goToDashboard : () -> Unit,
    goToLogin: () -> Unit
) {

    LaunchedEffect(key1 = true) {
        delay(1000)
        splashViewModel.splashEvent.collectLatest {
            when (it) {
                SplashUiEvents.Dashboard -> goToDashboard.invoke()
                SplashUiEvents.Login -> goToLogin.invoke()
            }
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val pulsate by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = ""
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = null,
            modifier = Modifier.size(pulsate.dp),
        )
    }
    splashViewModel.checkScreen()
}