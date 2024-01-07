package com.parisjohn.pricemonitoring.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parisjohn.pricemonitoring.base.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _baseEvent: MutableSharedFlow<BaseEvent> = MutableSharedFlow()
    var baseEvent = _baseEvent.asSharedFlow()
        private set

    fun logout() {
        viewModelScope.launch {
            sessionManager.saveAuthToken(null)
            sessionManager.clearSession()
        }
    }
}