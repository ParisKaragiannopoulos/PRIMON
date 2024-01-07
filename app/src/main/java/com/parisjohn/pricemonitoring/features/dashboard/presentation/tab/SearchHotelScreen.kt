package com.parisjohn.pricemonitoring.features.dashboard.presentation.tab

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisjohn.pricemonitoring.R
import com.parisjohn.pricemonitoring.base.ui.ContentWithProgress
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.features.dashboard.DashboardIntent
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardViewModel
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import com.parisjohn.pricemonitoring.ui.theme.Purple40
import com.parisjohn.pricemonitoring.utils.showToast
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SearchHotelScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    var isLoading by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val response by viewModel.hotelDetails.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.dashboardEvent.collectLatest {
            isLoading = when (it) {
                DashboardEvents.Loading -> true
                is DashboardEvents.Failure -> {
                    context.showToast(it.msg)
                    false
                }

                else -> {
                    false
                }
            }
        }
    }

    Box {
        Box(modifier = Modifier.height(250.dp)) {
            Image(
                painter = painterResource(id = R.drawable.search_bg),
                contentDescription = stringResource(id = R.string.login_background),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    if (it.contains(".html"))
                        viewModel.processIntent(DashboardIntent.searchText(searchText))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(Icons.Filled.Search, "", tint = Purple40)
                },
                placeholder = { Text(text = "Search") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent, //hide the indicator
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 235.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
                .background(Color.White),
        ) {
            response?.let {
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = it.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = "Description: \n\n" + it.description,
                    fontSize = 20.sp,
                )
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = "Score: \n\n${it.score} / 10",
                    fontSize = 20.sp,
                )
                ShareMap(
                    latitude = it.location.latitude.toString(),
                    longitude = it.location.longitude.toString(),
                    label = it.location.address
                )
                it.rooms.forEach { room ->
                    HotelDetailItem(room)
                }
            }
        }
    }


    if (isLoading) {
        ContentWithProgress()
    }
}

@Composable
fun ShareMap(latitude: String, longitude: String, label: String) {
    Text(
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp),
        text = "Address:",
        fontSize = 20.sp,
    )
    val context = LocalContext.current

    ClickableText(
        modifier = Modifier.padding(start = 10.dp),
        text = AnnotatedString(label),
        style = TextStyle(
            color = Color(0xff64B5F6),
            fontSize = 20.sp,
            textDecoration = TextDecoration.Underline
        ),
        onClick = {
            val mapUri = Uri.parse("geo:$latitude,$longitude?q=$label")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }
    )
}

@Composable
fun HotelDetailItem(item: HotelInfoResponse.Room) {
    val bullet = "\u2022"
    androidx.compose.material3.Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { /* navigate to chat*/ }
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )
                val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
                Text(
                    buildAnnotatedString {
                        item.attributes.forEach { attribute ->
                            withStyle(style = paragraphStyle) {
                                append(bullet)
                                append("\t\t")
                                append(attribute)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchHotelPreview() {
    PriceMonitoringTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SearchHotelScreen()
        }
    }
}