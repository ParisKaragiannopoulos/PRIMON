package com.parisjohn.pricemonitoring.data.network.request


import com.google.gson.annotations.SerializedName

data class UserRegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("repeatedPassword")
    val repeatedPassword: String
)