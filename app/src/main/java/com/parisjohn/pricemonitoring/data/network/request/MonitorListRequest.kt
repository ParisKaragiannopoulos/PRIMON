package com.parisjohn.pricemonitoring.data.network.request


import com.google.gson.annotations.SerializedName

data class MonitorListRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("rooms")
    val rooms: List<Int>,
)

data class UpdateMonitorListRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("rooms")
    val rooms: List<Int>,
    @SerializedName("id")
    val id: Int = -1
)