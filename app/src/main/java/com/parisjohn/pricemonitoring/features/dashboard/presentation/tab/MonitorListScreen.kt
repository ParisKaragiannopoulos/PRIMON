package com.parisjohn.pricemonitoring.features.dashboard.presentation.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.parisjohn.pricemonitoring.base.ui.ContentWithProgress
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.features.dashboard.DashboardIntent
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardViewModel
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import kotlinx.coroutines.flow.collectLatest
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import kotlin.random.Random
import kotlin.random.nextInt


@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun MonitorListScreen(
    onMonitorClick: (MonitorListsResponse.MonitorListsResponseItem) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var list by remember { mutableStateOf(emptyList<MonitorListsResponse.MonitorListsResponseItem>()) }
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
                    //viewModel.processIntent(DashboardIntent.refreshList)
                    false
                }

                DashboardEvents.Loading -> true
                else -> {
                    false
                }
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.list.collectLatest {
            list = it
        }
    }
    Box(
        modifier = Modifier
            .pullRefresh(pullToRefreshState),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Dashboard",
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                StatisticsScreen()
            }
            item {
                UpdateList(
                    list = list,
                    onSwipeToDelete = viewModel::processIntent,
                    onMonitorClick
                )
            }
        }
        PullRefreshIndicator(
            isRefreshing,
            pullToRefreshState,
            Modifier
                .align(Alignment.TopCenter)
        )
    }
    if (isLoading) {
        ContentWithProgress()
    }
}

@Composable
fun StatisticsScreen() {
    //Ratings of hotels
    val typeAmountMap: MutableMap<String, Int> = HashMap()
    typeAmountMap["until 6"] = Random.nextInt(100)
    typeAmountMap["7"] = Random.nextInt(100)
    typeAmountMap["8"] = Random.nextInt(100)
    typeAmountMap["9"] = Random.nextInt(100)
    typeAmountMap["10"] = Random.nextInt(100)
    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Text(
                text = "Monitored Hotels Ratings Distribution",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            AndroidView(factory = { ctx ->
                //  Initialize a View or View hierarchy here
                PieChart(ctx).apply {
                    description.isEnabled = false;
                    data = createPie(typeAmountMap)
                    invalidate()
                    val l = legend
                    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    l.orientation = Legend.LegendOrientation.VERTICAL
                    l.setDrawInside(false)
                    l.xEntrySpace = 7f
                    l.yEntrySpace = 0f
                    l.yOffset = 0f

                    // entry label styling
                    setDrawEntryLabels(false)
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .size(120.dp))
        }

    }

    val barEntries = ArrayList<BarEntry>()
    barEntries.add(BarEntry(1f, Random.nextInt(40,100).toFloat()))
    barEntries.add(BarEntry(2f,  Random.nextInt(40,100).toFloat()))
    barEntries.add(BarEntry(3f, Random.nextInt(40,100).toFloat()))
    barEntries.add(BarEntry(4f, Random.nextInt(40,100).toFloat()))
    barEntries.add(BarEntry(5f, Random.nextInt(40,100).toFloat()))

    val xAxisName = ArrayList<String>()
    xAxisName.add("Electra")
    xAxisName.add("Electra")
    xAxisName.add("Zeus")
    xAxisName.add("Ibis")
    xAxisName.add("Brown")
    xAxisName.add("Porto")

    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Text(
                text = "Top 5 cheapest hotels",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            AndroidView(factory = { ctx ->
                //  Initialize a View or View hierarchy here
                BarChart(ctx).apply {
                    setDrawBarShadow(false)
                    setFitBars(true)
                    setDrawValueAboveBar(false)
                    setPinchZoom(false)
                    setDrawGridBackground(false)
                    setClipValuesToContent(false)
                    description.isEnabled = false
                    data = barChart(barEntries)
                    invalidate()
                    setBackgroundColor(android.graphics.Color.TRANSPARENT) //set whatever color you prefer
                    legend.textSize = 10f
                    legend.formSize = 10f //To set components of x axis
                    xAxis.textSize = 13f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.valueFormatter = IndexAxisValueFormatter(xAxisName)
                    xAxis.setDrawGridLines(false)
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .size(120.dp))
        }
    }

    val typeAmountMapMx: MutableMap<String, Int> = HashMap()
    typeAmountMapMx["1 Max persons"] = Random.nextInt(100)
    typeAmountMapMx["2 Max persons"] = Random.nextInt(100)
    typeAmountMapMx["3 Max persons"] = Random.nextInt(100)
    typeAmountMapMx["4+ Max persons"] = Random.nextInt(100)
    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Text(
                text = "Monitored Hotels Rooms Size Distribution",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            AndroidView(factory = { ctx ->
                //  Initialize a View or View hierarchy here
                PieChart(ctx).apply {
                    description.isEnabled = false;
                    data = createPie(typeAmountMapMx)
                    invalidate()
                    val l = legend
                    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    l.orientation = Legend.LegendOrientation.VERTICAL
                    l.setDrawInside(false)
                    l.xEntrySpace = 7f
                    l.yEntrySpace = 0f
                    l.yOffset = 0f

                    // entry label styling
                    setDrawEntryLabels(false)
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .size(120.dp))
        }
    }
    //
}

fun barChart(barEntries: ArrayList<BarEntry>): BarData {
    val barDataSet = BarDataSet(barEntries, "")
    barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
    barDataSet.setDrawValues(false)
    val barData = BarData(barDataSet)
    barData.barWidth = 0.9f
    barData.setValueTextSize(0f)
    barData.setDrawValues(false)
    return barData
}
fun createPie(typeAmountMap: MutableMap<String, Int>):PieData{
    val pieEntries: ArrayList<PieEntry> = ArrayList<PieEntry>()
    //initializing colors for the entries

    //initializing colors for the entries
    val colors = ArrayList<Int>()
    for (i in 0..5){
        colors.add(android.graphics.Color.parseColor(String.format("#%06X", 0xFFFFFF and android.graphics.Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)))))
    }

    for (type in typeAmountMap.keys) {
        pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat(), type))
    }
    val pieDataSet = PieDataSet(pieEntries,"")
    pieDataSet.valueTextSize = 12f
    pieDataSet.colors = colors
    val pieData = PieData(pieDataSet)
    pieData.setDrawValues(false)
    return pieData
}

@Composable
fun UpdateList(
    list: List<MonitorListsResponse.MonitorListsResponseItem>,
    onSwipeToDelete: (intent: DashboardIntent) -> Unit,
    onMonitorClick: (MonitorListsResponse.MonitorListsResponseItem) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        list.forEachIndexed { index, it ->
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
                MonitorListItem(index+1,it, onMonitorClick)
            }

        }
    }
}

@Composable
fun MonitorListItem(
    index: Int,
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
                    text = "$index. ",
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