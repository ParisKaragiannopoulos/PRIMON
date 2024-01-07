package com.parisjohn.pricemonitoring.features.dashboard.viewmodel

import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse


sealed class DashboardEvents {
    class ShowMonitorLists(val list: List<MonitorListsResponse.MonitorListsResponseItem>) :
        DashboardEvents()
    class DeleteList(id: Long) : DashboardEvents()
    data class Failure(val msg: String) : DashboardEvents()
    object Loading : DashboardEvents()
    object Success : DashboardEvents()
}
