package com.parisjohn.pricemonitoring.features.dashboard.presentation.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.features.dashboard.DashboardIntent
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardViewModel
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import kotlinx.coroutines.flow.collectLatest
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun MonitorListScreen(onMonitorClick: (MonitorListsResponse.MonitorListsResponseItem) -> Unit = {}, viewModel: DashboardViewModel = hiltViewModel()) {
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var list by remember {mutableStateOf(emptyList<MonitorListsResponse.MonitorListsResponseItem>()) }
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        refreshThreshold = 140.dp,
        onRefresh = {
            viewModel.processIntent(DashboardIntent.refreshList)
        })
    LaunchedEffect(key1 = true) {
        viewModel.dashboardEvent.collectLatest {
            isLoading = when (it) {
                is DashboardEvents.ShowMonitorLists -> {
                    false
                }
                is DashboardEvents.DeleteList -> {
                    viewModel.processIntent(DashboardIntent.refreshList)
                    false
                }
                DashboardEvents.Loading -> true
                else -> {
                    false
                }
            }
        }
    }
    LaunchedEffect(key1 = true){
        viewModel.list.collectLatest {
            list = it
        }
    }
    Box(
        modifier = Modifier
            .pullRefresh(pullToRefreshState),
        contentAlignment = Alignment.Center
    ) {

        Column {
            Text(
                text = "My lists",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            UpdateList(
                list = list,
                onSwipeToDelete = viewModel::processIntent,
                onMonitorClick
            )
        }
        PullRefreshIndicator(
            isRefreshing,
            pullToRefreshState,
            Modifier
                .align(Alignment.TopCenter)
        )
    }

}

@Composable
fun UpdateList(
    list: List<MonitorListsResponse.MonitorListsResponseItem>,
    onSwipeToDelete: (intent: DashboardIntent) -> Unit,
    onMonitorClick: (MonitorListsResponse.MonitorListsResponseItem) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(list) {
            val delete = SwipeAction(
                onSwipe = {
                    onSwipeToDelete(DashboardIntent.onSwipeToDelete(it.monitorListID.toLong()))
                },
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(16.dp),
                        tint = Color.White
                    )
                }, background = Color.Red.copy(alpha = 0.5f),
                isUndo = true
            )
            SwipeableActionsBox(
                modifier = Modifier,
                swipeThreshold = 200.dp,
                endActions = listOf(delete)
            ) {
                MonitorListItem(it,onMonitorClick)
            }

        }
    }
}

@Composable
fun MonitorListItem(
    item: MonitorListsResponse.MonitorListsResponseItem,
    onMonitorClick: (MonitorListsResponse.MonitorListsResponseItem) -> Unit
) {
    Surface(
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
                .clickable { onMonitorClick.invoke(item) }
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.monitorListID.toString()+". ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.monitorListName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,

                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonitorListScreenPreview() {
    PriceMonitoringTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MonitorListScreen()
        }
    }
}