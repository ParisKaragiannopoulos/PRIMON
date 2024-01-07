package com.parisjohn.pricemonitoring.features.login.viewmodel.events


sealed class UserUiEvents() {
    object LoginSuccess : UserUiEvents()
    object RegisterSuccess : UserUiEvents()
    data class Failure(val msg: String) : UserUiEvents()
    object Loading : UserUiEvents()
}