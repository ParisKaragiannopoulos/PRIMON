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
        @SerializedName("description")
        val description: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("roomID")
        val roomID: Int
    )
}

fun getMock(): MonitorListResponse.Room {
    return MonitorListResponse.Room(
        listOf(
            "'View'",
            "'Washing machine'",
            "'Free WiFi'",
            "'Balcony'",
            "'Private pool'",
            "'Kitchen'",
            "'Private kitchen'",
            "'Entire apartment'",
            "'Ensuite bathroom'",
            "'Air conditioning'",
            "'Flat-screen TV'",
            "'Terrace'",
            "'160 m²'"
        ), "Description", "Name", 1
    )
}