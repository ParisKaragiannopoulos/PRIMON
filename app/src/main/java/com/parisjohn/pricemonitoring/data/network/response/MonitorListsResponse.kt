package com.parisjohn.pricemonitoring.data.network.response


import com.google.gson.annotations.SerializedName

class MonitorListsResponse : ArrayList<MonitorListsResponse.MonitorListsResponseItem>(){
    data class MonitorListsResponseItem(
        @SerializedName("monitorListID")
        val monitorListID: Int,
        @SerializedName("monitorListName")
        val monitorListName: String
    )
}