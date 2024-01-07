package com.parisjohn.pricemonitoring.base.data

sealed class NetworkResult<T>(var data: T? = null, val message: String? = null) {
    class Loading<T>(data: T? = null) : NetworkResult<T>(data)
    class Success<T>(data: T?) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
}