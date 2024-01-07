package com.parisjohn.pricemonitoring.features.dashboard


sealed class DashboardIntent {
    object refreshList : DashboardIntent()
    data class onSwipeToDelete(val id:Long) : DashboardIntent()
    data class searchText(val link: String) : DashboardIntent()
}