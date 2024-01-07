package com.parisjohn.pricemonitoring.base

sealed class BaseEvent() {
    object Logout : BaseEvent()
}