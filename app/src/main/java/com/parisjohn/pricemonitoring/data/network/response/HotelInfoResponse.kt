package com.parisjohn.pricemonitoring.data.network.response


import com.google.gson.annotations.SerializedName

data class HotelInfoResponse(
    @SerializedName("description")
    val description: String,
    @SerializedName("hotelID")
    val hotelID: Int,
    @SerializedName("hotelType")
    val hotelType: String,
    @SerializedName("location")
    val location: Location,
    @SerializedName("name")
    val name: String,
    @SerializedName("rooms")
    val rooms: List<Room>,
    @SerializedName("score")
    val score: String,
    @SerializedName("url")
    val url: String
) {
    data class Location(
        @SerializedName("address")
        val address: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double
    )

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