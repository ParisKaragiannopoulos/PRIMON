package com.parisjohn.pricemonitoring.data.network.request


import com.google.gson.annotations.SerializedName

data class SearchHotelRequest(
    @SerializedName("link")
    val link: String
)