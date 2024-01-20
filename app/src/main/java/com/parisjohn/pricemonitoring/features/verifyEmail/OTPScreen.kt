package com.parisjohn.pricemonitoring.features.verifyEmail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisjohn.pricemonitoring.R
import com.parisjohn.pricemonitoring.base.ui.ContentWithProgress
import com.parisjohn.pricemonitoring.features.login.UserIntent
import com.parisjohn.pricemonitoring.features.login.viewmodel.LoginViewModel
import com.parisjohn.pricemonitoring.features.login.viewmodel.events.UserUiEvents
import com.parisjohn.pricemonitoring.ui.theme.BackgroundFade
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import com.parisjohn.pricemonitoring.utils.showToast
import kotlinx.coroutines.flow.collectLatest


@Composable
fun OTPScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onVerifiedClick: () -> Unit,
) {
    var otpValue by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        loginViewModel.loginEvent.collectLatest {
            isLoading = when (it) {
                is UserUiEvents.LoginSuccess -> {
                    onVerifiedClick.invoke()
                    false
                }

                is UserUiEvents.Failure -> {
                    context.showToast(it.msg)
                    false
                }

                UserUiEvents.Loading -> true
                else -> {
                    false
                }
            }
        }
    }
    Image(
        painter = painterResource(id = R.drawable.email_verify_bg),
        contentDescription = stringResource(id = R.string.login_background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    val brush = Brush.verticalGradient(listOf(Color.Black,Color(0xD0000000),BackgroundFade))
    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush))
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp, 10.dp)) {
        // Create references for the composable to constrain
        val (titleText,textField, checkEmailText) = createRefs()
        Text(
            text = AnnotatedString("Add one-time password"),
            style = TextStyle(
                color = Color.White,
            ),
            fontSize = 24.sp,
            modifier = Modifier.constrainAs(titleText){
                top.linkTo(parent.top, margin = 24.dp)
            },
        )
        OtpTextField(
            otpText = otpValue,
            onOtpTextChange = { value,isFinished ->
                otpValue = value
                if(isFinished) {
                    loginViewModel.processIntent(UserIntent.OTP(value))
                }
            }, modifier = Modifier.constrainAs(textField) {
                top.linkTo(titleText.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            text = AnnotatedString("Please check your email!"),
            style = TextStyle(
                color = Color.White,
            ),
            modifier = Modifier.constrainAs(checkEmailText){
                top.linkTo(textField.bottom, margin = 8.dp)
                start.linkTo(textField.start, margin = 4.dp)
            },
        )
    }
    if (isLoading) {
        ContentWithProgress()
    }

}


@Preview(showBackground = true)
@Composable
fun OTPPreview() {
    PriceMonitoringTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OTPScreen {}
        }
    }
}