package com.parisjohn.pricemonitoring.features.dashboard.viewmodel

import androidx.lifecycle.viewModelScope
import com.parisjohn.pricemonitoring.base.BaseViewModel
import com.parisjohn.pricemonitoring.base.data.SessionManager
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.domain.repositories.MonitorRepository
import com.parisjohn.pricemonitoring.features.dashboard.DashboardIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val monitorRepository: MonitorRepository
) : BaseViewModel(sessionManager) {

    private val _dashboardEvent: MutableStateFlow<DashboardEvents> = MutableStateFlow(DashboardEvents.Loading)
    var dashboardEvent = _dashboardEvent
        private set

    private val _list: MutableStateFlow< List<MonitorListsResponse.MonitorListsResponseItem>> = MutableStateFlow(
        emptyList()
    )


    private val _hotelDetails = MutableStateFlow<HotelInfoResponse?>(null)
    val hotelDetails = _hotelDetails.asStateFlow()

    var list = _list
        private set


    init {
        showMonitorLists()
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
                    _dashboardEvent.emit(
                        DashboardEvents.DeleteList(id)
                    )
                }
        }
    }

}
