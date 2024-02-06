package com.parisjohn.pricemonitoring.features.hotel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.parisjohn.pricemonitoring.base.BaseViewModel
import com.parisjohn.pricemonitoring.base.data.SessionManager
import com.parisjohn.pricemonitoring.data.network.request.MonitorListRequest
import com.parisjohn.pricemonitoring.data.network.request.UpdateMonitorListRequest
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.data.network.response.PriceRoomResponse
import com.parisjohn.pricemonitoring.domain.repositories.MonitorRepository
import com.parisjohn.pricemonitoring.features.dashboard.viewmodel.DashboardEvents
import com.parisjohn.pricemonitoring.features.details.GraphPrice
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
class HotelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sessionManager: SessionManager,
    private val monitorRepository: MonitorRepository
) : BaseViewModel(sessionManager) {

    private val _dashboardEvent: MutableStateFlow<DashboardEvents> = MutableStateFlow(
        DashboardEvents.Loading
    )
    private val _list: MutableStateFlow<List<MonitorListsResponse.MonitorListsResponseItem>> =
        MutableStateFlow(
            emptyList()
        )

    var list = _list
        private set
    private val _hotelDetails = MutableStateFlow<HotelInfoResponse?>(null)
    val hotelDetails = _hotelDetails.asStateFlow()

    var dashboardEvent = _dashboardEvent
        private set

    private val _rooms: MutableStateFlow<List<HotelInfoResponse.Room>> =
        MutableStateFlow(
            emptyList()
        )

    var rooms = _rooms
        private set
    private var monitorListID = 0L
    init {
        savedStateHandle.get<String>("hotelId")?.let { id ->
            monitorListID = id.toLong()
            viewModelScope.launch {
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
                        showMonitorLists()
                    }
            }
        }
    }


    fun addRoomInList(listName: String, roomID: Int) {
        if (listName.isEmpty()) return
        val isExist = list.value.any { it.monitorListName == listName }
        if (isExist) {
            viewModelScope.launch {
                val id = list.value.findLast { it.monitorListName == listName }?.monitorListID ?: -1
                if (id == -1) return@launch
                monitorRepository.getListDetails(id.toString())
                    .onStart {
                        _dashboardEvent.emit(
                            DashboardEvents.Loading
                        )
                    }.catch {
                        val message = "Something went wrong"
                        _dashboardEvent.emit(DashboardEvents.Failure(message))
                    }.collect {
                        val rooms = it.rooms.map { t -> t.roomID }.toMutableList()
                        rooms.add(roomID)
                        monitorRepository.updateMonitorList(
                            UpdateMonitorListRequest(
                                listName,
                                rooms,
                                it.monitorListID,
                                listOf(30,70)
                            )
                        ).catch {
                            val message = "Something went wrong"
                            _dashboardEvent.emit(DashboardEvents.Failure(message))
                        }.collect {
                            showMonitorLists()
                            var message = "Monitor list updated successfully"
                            _dashboardEvent.emit(DashboardEvents.Failure(message)) // is success
                        }
                    }
            }
        } else {
            viewModelScope.launch {
                monitorRepository.postNewMonitorList(MonitorListRequest(listName, listOf(roomID),listOf(30,70)))
                    .onStart {
                        _dashboardEvent.emit(
                            DashboardEvents.Loading
                        )
                    }.catch {
                        var message = "Something went wrong"
                        _dashboardEvent.emit(DashboardEvents.Failure(message))
                    }.collect {
                        showMonitorLists()
                        var message = "Monitor list created successfully"
                        _dashboardEvent.emit(DashboardEvents.Failure(message)) // is success
                    }
            }
        }
    }


    private fun showMonitorLists() {
        viewModelScope.launch {
            monitorRepository.getAllMonitorLists()
                .onStart {
                    _dashboardEvent.emit(
                        DashboardEvents.Loading
                    )
                }.catch {
                    val message = "Something went wrong"
                    _dashboardEvent.emit(DashboardEvents.Failure(message))
                }.collect {
                    _list.emit(it)
                    _dashboardEvent.emit(
                        DashboardEvents.ShowMonitorLists(it)
                    )
                }
        }
    }


}
