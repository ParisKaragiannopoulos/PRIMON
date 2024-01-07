package com.parisjohn.pricemonitoring.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parisjohn.pricemonitoring.base.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _splashEvent: MutableSharedFlow<SplashUiEvents> = MutableSharedFlow()
    var splashEvent = _splashEvent.asSharedFlow()
        private set

    fun checkScreen() {
        viewModelScope.launch {
            sessionManager.fetchAuthToken()?.let {
                _splashEvent.emit(SplashUiEvents.Dashboard)
            } ?: _splashEvent.emit(SplashUiEvents.Login)
        }
    }

}

sealed class SplashUiEvents {
    object Dashboard : SplashUiEvents()
    object Login : SplashUiEvents()
}