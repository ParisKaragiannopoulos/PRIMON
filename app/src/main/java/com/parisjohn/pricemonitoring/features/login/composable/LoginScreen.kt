package com.parisjohn.pricemonitoring.features.login.composable

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisjohn.pricemonitoring.R
import com.parisjohn.pricemonitoring.base.ui.ContentWithProgress
import com.parisjohn.pricemonitoring.features.login.User
import com.parisjohn.pricemonitoring.features.login.UserIntent
import com.parisjohn.pricemonitoring.features.login.viewmodel.LoginViewModel
import com.parisjohn.pricemonitoring.features.login.viewmodel.events.UserUiEvents
import com.parisjohn.pricemonitoring.ui.theme.BackgroundFade
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import com.parisjohn.pricemonitoring.utils.showToast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = hiltViewModel(),
                  onSignUpClick: ()-> Unit, onLoginClick: () -> Unit) {
    var loginEmail by remember { mutableStateOf("test@email.com") }
    var loginPassword by remember { mutableStateOf("TestPass123!") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        loginViewModel.loginEvent.collectLatest {
            isLoading = when (it) {
                is UserUiEvents.LoginSuccess -> {
                    onLoginClick.invoke()
                    false
                }
                is UserUiEvents.Failure -> {
                    Log.d("main", "UserScreen:${it.msg} ")
                    context.showToast(it.msg)
                    false
                }
                UserUiEvents.Loading -> true
                else -> {false}
            }
        }
    }
    Image(
        painter = painterResource(id = R.drawable.login_bg),
        contentDescription = stringResource(id = R.string.login_background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    val brush = Brush.verticalGradient(listOf(BackgroundFade,Color(0xAE000000), Color.Black))
    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush))

    Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize().padding(20.dp,10.dp)) {
        LoginField(
            value = loginEmail,
            onChange = { loginEmail = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(
            value = loginPassword,
            onChange = { loginPassword = it },
            submit = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {loginViewModel.processIntent(UserIntent.Login(user = User(email = loginEmail, password = loginPassword)))},
            enabled = true,
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = AnnotatedString("Don't have an account? Sign Up"),
            onClick = { onSignUpClick() },
            style = TextStyle(
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

    }

    if(isLoading){
        ContentWithProgress()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    PriceMonitoringTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen(onSignUpClick = {}, onLoginClick = {})
        }
    }
}