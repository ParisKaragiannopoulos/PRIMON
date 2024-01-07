package com.parisjohn.pricemonitoring.features.dashboard.presentation.tab
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.parisjohn.pricemonitoring.R
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardViewModel
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import kotlinx.coroutines.flow.collectLatest


data class FeatureList(
    val name: String,
    val listIcon: DCodeIcon,
)
sealed class DCodeIcon {
    data class ImageVectorIcon(val imageVector: ImageVector) : DCodeIcon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : DCodeIcon()
}

object MyIcons {
    val List = Icons.Rounded.List
    val Info = Icons.Rounded.Info
    val Email = Icons.Filled.Email
    val Share = Icons.Filled.Share
    val Logout = Icons.Default.Logout
    val KeyboardArrowRight = Icons.Default.KeyboardArrowRight
}
val moreOptionsList = listOf(
    FeatureList("About", DCodeIcon.ImageVectorIcon(MyIcons.Info)),
    FeatureList("Share App", DCodeIcon.ImageVectorIcon(MyIcons.Share)),
    FeatureList("Logout",DCodeIcon.ImageVectorIcon(MyIcons.Logout)),
    )

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ProfileTabScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    var sizeOfMonitorList by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = true) {
        viewModel.list.collectLatest {
            sizeOfMonitorList = it.size
        }
    }

    Scaffold(
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { padding ->

        ProfileContent(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            TopProfileLayout(sizeOfMonitorList)
            MainProfileContent()
            FooterContent { viewModel.logout() }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        content()
    }
}

@Composable
fun TopProfileLayout(sizeOfMonitorList: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .drawBehind {
                            drawCircle(
                                color = Color.Black,
                                radius = this.size.maxDimension
                            )
                        },
                    text = "P",
                    fontSize = 18.sp,
                    color = Color.White
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {
                    ImageTextContent(
                        modifier = Modifier.padding(vertical = 5.dp),
                        icon = {
                            Icon(
                                imageVector = DCodeIcon.ImageVectorIcon(MyIcons.Email).imageVector,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        },
                        text = {
                            Text(
                                text = "paris@gmail.com",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    )

                    ImageTextContent(
                        modifier = Modifier.padding(vertical = 5.dp),
                        icon = {
                            Icon(
                                imageVector = DCodeIcon.ImageVectorIcon(MyIcons.List).imageVector,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        },
                        text = {
                            Text(
                                text = "$sizeOfMonitorList list",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun ImageTextContent(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(5.dp))
        text()
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun MainProfileContent() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8),
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "Manage Subscription",
                fontSize = 22.sp,
            )
            SubscriptionView()

            Divider(modifier = Modifier.padding(vertical = 15.dp))
        }
    }
}

@Composable
fun SubscriptionView() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pro))
    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier
                .padding(5.dp)
                .weight(1f)) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Pro",
                        fontSize = 32.sp,
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = "4,99 €",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                    )
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    text = "paid monthly",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Button(
                    onClick = {},
                    enabled = true,
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .width(150.dp)
                        .padding(vertical = 5.dp)
                ) {
                    Text("UPGRADE")
                }
            }
            LottieAnimation(
                modifier = Modifier.size(100.dp),
                composition = composition,
                iterations = 1,
            )
        }
    }
}


@Composable
fun FooterContent(onLogoutClick: ()-> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8),
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "More Section",
                style = MaterialTheme.typography.titleMedium,
            )
            moreOptionsList.forEach {
                MoreOptionsComp(it) {
                    if (it.name == "Logout") {
                        onLogoutClick.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun MoreOptionsComp(
    featureList: FeatureList,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (featureList.listIcon) {
            is DCodeIcon.ImageVectorIcon -> Icon(
                imageVector = featureList.listIcon.imageVector,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(6.dp)
            )

            is DCodeIcon.DrawableResourceIcon -> Icon(
                painter = painterResource(id = featureList.listIcon.id),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(6.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f)
        ) {
            Text(
                text = featureList.name,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Icon(
            imageVector = MyIcons.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.padding(4.dp)
        )
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
            ProfileTabScreen()
        }
    }
}