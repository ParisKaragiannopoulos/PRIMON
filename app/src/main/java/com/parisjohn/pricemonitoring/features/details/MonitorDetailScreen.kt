package com.parisjohn.pricemonitoring.features.details


import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisjohn.pricemonitoring.R
import com.parisjohn.pricemonitoring.base.ui.ContentWithProgress
import com.parisjohn.pricemonitoring.base.ui.ExpandableText
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import com.parisjohn.pricemonitoring.ui.theme.BackgroundFade
import com.parisjohn.pricemonitoring.ui.theme.PriceMonitoringTheme
import com.parisjohn.pricemonitoring.ui.theme.Purple40
import com.parisjohn.pricemonitoring.utils.showToast
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.marker.markerComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShader
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun MonitorDetailScreen(onBackClick: () -> Unit,viewModel: MonitorDetailViewModel = hiltViewModel()) {
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
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.Left) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(id = R.string.login_background),
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .padding(8.dp)
                        .size(32.dp),
                )
            }
            ExpandableList(response,viewModel)

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
        val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val zippedListIterative = graphPrice.axis_x.zip(graphPrice.axis_y) { a, b -> a to b }.toList()
        val data = zippedListIterative.associate { (dateString, yValue) ->
            LocalDate.parse(dateString,dateTimeFormat) to yValue
        }
        val xValuesToDates = data.keys.associateBy { it.toEpochDay().toFloat() }
        val chartEntryModel = entryModelOf(xValuesToDates.keys.zip(data.values, ::entryOf))
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
        val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            (xValuesToDates[value] ?: LocalDate.ofEpochDay(value.toLong())).format(dateTimeFormatter)
        }
        val horizontalAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter,
            labelRotationDegrees = 90f,
            sizeConstraint = Axis.SizeConstraint.Exact(120f),
            label = axisLabelComponent(textSize = 8.sp),
            titleComponent = axisLabelComponent(),
        )
        Column(modifier = Modifier.padding(10.dp)) {
            Chart(
                chart = lineChart(lines = listOf(LineChart.LineSpec(
                    lineColor = Purple40.toArgb(),
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        brush = Brush.verticalGradient(listOf(
                            Purple40.copy(com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                            Purple40.copy(com.patrykandpatrick.vico.core.DefaultAlpha.LINE_BACKGROUND_SHADER_END),))
                    ),
                ))),
                marker = markerComponent(
                    label = textComponent(),
                    indicator = shapeComponent(),
                    guideline = lineComponent(color = BackgroundFade.copy(alpha = 0.8f)),
                ),
                isZoomEnabled = true,
                model = chartEntryModel,
                startAxis = rememberStartAxis(
                    valueFormatter = { value,_ ->
                        "${value.toInt()}€"
                    },
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 6),
                ),
                modifier = Modifier.height(300.dp),
                bottomAxis = horizontalAxis,
            )
        }

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
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)) {
            Row (
                modifier = Modifier.fillMaxWidth()){
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
                text = item.attributes.joinToString(", ") ,
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