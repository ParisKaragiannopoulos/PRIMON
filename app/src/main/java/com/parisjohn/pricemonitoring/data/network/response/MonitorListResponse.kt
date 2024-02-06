package com.parisjohn.pricemonitoring.data.network.response


import com.google.gson.annotations.SerializedName

data class MonitorListResponse(
    @SerializedName("monitorListID")
    val monitorListID: Int,
    @SerializedName("monitorListName")
    val monitorListName: String,
    @SerializedName("rooms")
    val rooms: List<Room>
) {
    data class Room(
        @SerializedName("attributes")
        val attributes: List<String>,
        @SerializedName("hotelName")
        val hotelName: String,
        @SerializedName("hotelID")
        val hotelID: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("roomID")
        val roomID: Int
    )
}