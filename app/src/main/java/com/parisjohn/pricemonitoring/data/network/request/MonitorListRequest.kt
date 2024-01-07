package com.parisjohn.pricemonitoring.data.network.request


import com.google.gson.annotations.SerializedName

data class MonitorListRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("rooms")
    val rooms: List<Int>
)