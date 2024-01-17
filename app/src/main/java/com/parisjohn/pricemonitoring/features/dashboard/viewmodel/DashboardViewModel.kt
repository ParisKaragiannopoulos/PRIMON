package com.parisjohn.pricemonitoring.features.dashboard.viewmodel

import androidx.compose.runtime.MutableIntState
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.parisjohn.pricemonitoring.base.BaseViewModel
import com.parisjohn.pricemonitoring.base.data.SessionManager
import com.parisjohn.pricemonitoring.data.network.request.MonitorListRequest
import com.parisjohn.pricemonitoring.data.network.request.MonitorListUpdateRequest
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.domain.repositories.MonitorRepository
import com.parisjohn.pricemonitoring.features.dashboard.DashboardIntent
import com.parisjohn.pricemonitoring.features.details.GraphPrice
import com.parisjohn.pricemonitoring.features.details.toGraph
import com.parisjohn.pricemonitoring.features.notification.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val monitorRepository: MonitorRepository,
) : BaseViewModel(sessionManager) {
    private val _dashboardEvent: MutableStateFlow<DashboardEvents> =
        MutableStateFlow(DashboardEvents.Loading)
    var dashboardEvent = _dashboardEvent
        private set
    private val workManager = WorkManager.getInstance(sessionManager.getContext())

    private val _list: MutableStateFlow<List<MonitorListsResponse.MonitorListsResponseItem>> =
        MutableStateFlow(
            emptyList()
        )

    private val _graph: MutableStateFlow<GraphPrice> = MutableStateFlow(
        GraphPrice(
            emptyList(),
            emptyList()
        )
    )
    var graph = _graph
        private set

    private val _hotelDetails = MutableStateFlow<HotelInfoResponse?>(null)
    val hotelDetails = _hotelDetails.asStateFlow()

    private val _notificationStatus = MutableStateFlow(sessionManager.getNotificationStatus())
    val notificationStatus = _notificationStatus.asStateFlow()

    var list = _list
        private set

    init {
        showMonitorLists()
        if (sessionManager.getNotificationStatus())
            scheduleReminder(1000, TimeUnit.MILLISECONDS)
    }

    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.refreshList -> {
                showMonitorLists()
            }

            is DashboardIntent.onSwipeToDelete -> {
                onSwipeToDelete(intent.id)
            }

            is DashboardIntent.searchText -> {
                searchHotelDetails(intent.link)
            }
        }
    }

    private fun searchHotelDetails(link: String) {
        viewModelScope.launch {
            monitorRepository.searchHotelDetails(link)
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

    fun getGraph(roomID: Int) {
        viewModelScope.launch {
            monitorRepository.getPricesOfSpecificRoom(roomID.toString())
                .onStart {
                    _dashboardEvent.emit(
                        DashboardEvents.Loading
                    )
                }.catch {
                    val message = "Something went wrong"
                    _dashboardEvent.emit(DashboardEvents.Failure(message))
                }.collect {
                    _dashboardEvent.emit(DashboardEvents.Success)
                    _graph.emit(it.toGraph())
                }
        }
    }

    private fun onSwipeToDelete(id: Long) {
        viewModelScope.launch {
            monitorRepository.deleteMonitorList(id)
                .onStart {
                    _dashboardEvent.emit(
                        DashboardEvents.Loading
                    )
                }.catch {
                    var message = "Something went wrong"
                    _dashboardEvent.emit(DashboardEvents.Failure(message))
                }.collect {
                    showMonitorLists()
                    var message = "Monitor list has been deleted"
                    _dashboardEvent.emit(DashboardEvents.Failure(message))
                }
        }
    }

    private fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
    ) {
        val myWorkRequestBuilder = OneTimeWorkRequestBuilder<ReminderWorker>()
        myWorkRequestBuilder.setInitialDelay(duration, unit)
        workManager.enqueue(myWorkRequestBuilder.build())
    }

    fun setNotificationEnabled(enabled: Boolean) {
        sessionManager.saveNotificationAllow(enabled)
        _notificationStatus.value = enabled
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
                            MonitorListUpdateRequest(
                                listName,
                                rooms,
                                it.monitorListID
                            )
                        ).catch {
                            var message = "Something went wrong"
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
                monitorRepository.postNewMonitorList(MonitorListRequest(listName, listOf(roomID)))
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

}
