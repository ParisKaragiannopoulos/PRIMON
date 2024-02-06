package com.parisjohn.pricemonitoring.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.parisjohn.pricemonitoring.base.BaseViewModel
import com.parisjohn.pricemonitoring.base.data.SessionManager
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.data.network.response.PriceRoomResponse
import com.parisjohn.pricemonitoring.domain.repositories.MonitorRepository
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class MonitorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sessionManager: SessionManager,
    private val monitorRepository: MonitorRepository
) : BaseViewModel(sessionManager) {

    private val _dashboardEvent: MutableStateFlow<DashboardEvents> = MutableStateFlow(
        DashboardEvents.Loading
    )
    private val _graph: MutableStateFlow<GraphPrice> = MutableStateFlow(GraphPrice(emptyList(),
        emptyList()
    ))

    private val _hotelDetails = MutableStateFlow<HotelInfoResponse?>(null)
    val hotelDetails = _hotelDetails.asStateFlow()

    var graph = _graph
        private set
    var dashboardEvent = _dashboardEvent
        private set

    private val _list: MutableStateFlow<List<MonitorListResponse.Room>> =
        MutableStateFlow(
            emptyList()
        )
    var list = _list
        private set
    var monitorListID = ""
    init {
        savedStateHandle.get<String>("monitorId")?.let {
            monitorListID = it
            viewModelScope.launch {
                monitorRepository.getListDetails(it)
                    .onStart {
                        _dashboardEvent.emit(
                            DashboardEvents.Loading
                        )
                    }.catch {
                        val message = "Something went wrong"
                        _dashboardEvent.emit(DashboardEvents.Failure(message))
                    }.collect {
                        _dashboardEvent.emit(DashboardEvents.Success)
                        _list.emit(it.rooms)
                    }
            }
        }
    }

    fun getHotelByID(id: String) {
        _dashboardEvent.tryEmit(
            DashboardEvents.Loading
        )
        viewModelScope.launch {
            try{
                monitorRepository.getHotelByID(id.toLong())
                    .onStart {
                        _dashboardEvent.emit(
                            DashboardEvents.Loading
                        )
                    }.catch {
                        val message = "Something went wrong"
                        _dashboardEvent.emit(DashboardEvents.Failure(message))
                    }.collect {
                        _dashboardEvent.emit(DashboardEvents.Success)
                        _hotelDetails.emit(it)
                    }
            } catch (e : Exception){
                val message = "Something went wrong"
                _dashboardEvent.tryEmit(DashboardEvents.Failure(message))
            }
        }
    }

    fun getGraph(roomID: Int,size: Int = 0) {
        viewModelScope.launch {
            if(roomID==-1){
                _graph.emit(GraphPrice(emptyList(), emptyList()))
                return@launch
            }
            monitorRepository.getPricesOfSpecificRoom(monitorListID,roomID.toString(),size)
                .onStart {
                    _dashboardEvent.emit(
                        DashboardEvents.Loading
                    )
                }.catch {
                    val message = "Something went wrong"
                    _dashboardEvent.emit(DashboardEvents.Failure(message))
                }.collect {
                    if(size==0 && it.page.totalElements != 0){
                        getGraph(roomID,it.page.totalElements)
                        return@collect
                    }
                    _dashboardEvent.emit(DashboardEvents.Success)
                    it.embedded?.let { t->
                        if(t.priceInfoList.isEmpty()) return@collect
                        _graph.emit(it.toGraph())
                    }?: return@collect
                }
        }
    }
}

fun PriceRoomResponse.toGraph(): GraphPrice {
    this.embedded.priceInfoList = this.embedded.priceInfoList.map { it.copy(date = it.timestamp.split(".")[0].toDateTime().plusDays(it.distanceDays.toLong()).toStringPattern())}
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val formatter = DateTimeFormatter.ofPattern("MMM/yyyy")
    val result = this.embedded.priceInfoList.groupBy { LocalDate.parse(it.date, dateTimeFormatter).format(formatter) }.mapValues { it.value.map { pair -> pair.price }.average() }
    return GraphPrice(axis_x = result.map { it.key },
        axis_y = result.map { it.value })
}

fun String.toDateTime(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): LocalDateTime {
    val patternFormatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDateTime.parse(this, patternFormatter)
}

fun LocalDateTime.toStringPattern(pattern: String = "dd-MM-yyyy"): String {
    val patternFormatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(patternFormatter)
}