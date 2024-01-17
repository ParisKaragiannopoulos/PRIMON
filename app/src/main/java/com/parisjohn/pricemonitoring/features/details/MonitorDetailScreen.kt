package com.parisjohn.pricemonitoring.features.details


import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.parisjohn.pricemonitoring.R
import com.parisjohn.pricemonitoring.base.ui.ContentWithProgress
import com.parisjohn.pricemonitoring.base.ui.ExpandableText
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import com.parisjohn.pricemonitoring.ui.theme.Purple40
import com.parisjohn.pricemonitoring.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun MonitorDetailScreen(
    onBackClick: () -> Unit,
    viewModel: MonitorDetailViewModel = hiltViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var graphPrice by remember { mutableStateOf(GraphPrice(emptyList(), emptyList())) }
    val response by viewModel.list.collectAsState()
    if (graphPrice.axis_x.isNotEmpty()) {
        BottomSheet(graphPrice) {
            graphPrice = graphPrice.copy(axis_x = emptyList())
        }
    }
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
    LaunchedEffect(key1 = true) {
        viewModel.graph.collectLatest {
            graphPrice = it
        }
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(id = R.string.login_background),
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(8.dp)
                    .size(32.dp),
            )
        }
        ExpandableList(response, viewModel)

    }
    if (isLoading) {
        ContentWithProgress()
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(graphPrice: GraphPrice, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        coroutineScope.launch {
            modalBottomSheetState.show()
        }
        AndroidView(
            factory = { ctx ->
                //  Initialize a View or View hierarchy here
                LineChart(ctx).apply {
                    description.isEnabled = false;
                    // enable touch gestures
                    setTouchEnabled(true);
                    dragDecelerationFrictionCoef = 0.9f;
                    // enable scaling and dragging
                    isDragEnabled = true;
                    setScaleEnabled(true);
                    setDrawGridBackground(false);
                    isHighlightPerDragEnabled = true;
                    // get the legend (only possible after setting data)
                    // get the legend (only possible after setting data)
                    val l: Legend = legend
                    l.isEnabled = false

                    val xAxis: XAxis = xAxis
                    xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
                    xAxis.textSize = 10f
                    xAxis.textColor = Color.WHITE
                    xAxis.setDrawAxisLine(false)
                    xAxis.setDrawGridLines(true)
                    xAxis.textColor = Color.rgb(255, 192, 56)
                    xAxis.setCenterAxisLabels(true)
                    xAxis.granularity = 1f // one hour

                    xAxis.valueFormatter = XDateFormatter(graphPrice.axis_x)
                    // set an alternative background color
                    setBackgroundColor(Color.WHITE);
                    setViewPortOffsets(0f, 0f, 0f, 0f)
                    val values = ArrayList(graphPrice.axis_y.mapIndexed { index, d ->
                        Entry(
                            index.toFloat(),
                            d.toFloat()
                        )
                    }.toList())
                    data = lineChart(values)
                    invalidate()
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .size(120.dp)
        )
        Column(modifier = Modifier.padding(10.dp)) {

        }

    }
}

fun lineChart(values: ArrayList<Entry>): LineData {
    val set1 = LineDataSet(values, "DataSet 1")
    set1.axisDependency = AxisDependency.LEFT
    set1.color = ColorTemplate.getHoloBlue()
    set1.valueTextColor = ColorTemplate.getHoloBlue()
    set1.lineWidth = 1.5f
    set1.setDrawCircles(true)
    set1.setDrawValues(false)
    set1.fillAlpha = 65
    set1.fillColor = ColorTemplate.getHoloBlue()
    set1.highLightColor = Color.rgb(244, 117, 117)
    set1.setDrawCircleHole(false)

    val data = LineData(set1)
    data.setValueTextColor(Color.WHITE)
    data.setValueTextSize(9f)
    return data
}

private class XDateFormatter(private val axisX: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        if(value<0 || value>=axisX.size)
            return ""
        Log.e("testt", value.toString())
        return axisX[value.toInt()]
    }
}

@Composable
fun SectionHeader(
    item: MonitorListResponse.Room,
    onHeaderClicked: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        border = BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_price_history),
                    contentDescription = stringResource(id = R.string.login_background),
                    colorFilter = ColorFilter.tint(Purple40),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onHeaderClicked()
                        },
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light
            )
            ExpandableText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                text = item.attributes.joinToString(", "),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun ExpandableList(sections: List<MonitorListResponse.Room>, viewModel: MonitorDetailViewModel) {
    LazyColumn(
        content = {
            sections.onEachIndexed { index, sectionData ->
                Section(
                    sectionData = sectionData,
                    onHeaderClick = {
                        viewModel.getGraph(sectionData.roomID)
                    }
                )
            }
        }
    )
}

fun LazyListScope.Section(
    sectionData: MonitorListResponse.Room,
    onHeaderClick: () -> Unit
) {

    item {
        SectionHeader(
            item = sectionData,
            onHeaderClicked = onHeaderClick
        )
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
            MonitorDetailScreen({})
        }
    }
}