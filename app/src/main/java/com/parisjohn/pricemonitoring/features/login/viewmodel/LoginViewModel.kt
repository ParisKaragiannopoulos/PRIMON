package com.parisjohn.pricemonitoring.features.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parisjohn.pricemonitoring.base.data.SessionManager
import com.parisjohn.pricemonitoring.domain.repositories.UserRepository
import com.parisjohn.pricemonitoring.features.login.User
import com.parisjohn.pricemonitoring.features.login.UserIntent
import com.parisjohn.pricemonitoring.features.login.viewmodel.events.UserUiEvents
import com.parisjohn.pricemonitoring.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _loginEvent: MutableSharedFlow<UserUiEvents> = MutableSharedFlow()
    var loginEvent = _loginEvent.asSharedFlow()
        private set

    fun processIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.Login -> {
                loginUser(intent.user.email, intent.user.password)
            }

            is UserIntent.Register -> {
                registerUser(intent.user)
            }

            is UserIntent.OTP ->{
                validateOtp(intent.value)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            userRepository.loginUser(email, password)
                .onStart {
                    _loginEvent.emit(
                        UserUiEvents.Loading
                    )
                }.catch {
                    var message = "Something went wrong"
                    if(it is retrofit2.HttpException){
                        if(it.code() == 401){
                            message = "Wrong Credentials"
                        }
                    }
                    _loginEvent.emit(UserUiEvents.Failure(message))
                }.collect {
                    it.body()?.let { it1 -> sessionManager.saveAuthToken(it1) }
                    _loginEvent.emit(
                        UserUiEvents.LoginSuccess
                    )
                }
        }
    }

    private fun registerUser(user: User) {
        viewModelScope.launch {
            if(user.isEmpty()){
                _loginEvent.emit(UserUiEvents.Failure("Add your info!"))
                return@launch
            }
            if(user.email.isValidEmail().not()){
                _loginEvent.emit(UserUiEvents.Failure("Wrong email format!"))
                return@launch
            }
            if(user.repeatedPassword != user.password){
                _loginEvent.emit(UserUiEvents.Failure("Passwords don't match!"))
                return@launch
            }
            userRepository.signupUser(user)
                .onStart {
                    _loginEvent.emit(
                        UserUiEvents.Loading
                    )
                }.catch {
                    var message = "Something went wrong"
                    if(it is retrofit2.HttpException){
                        if(it.code() == 401){
                            message = "Wrong Credentials"
                        }
                    }
                    _loginEvent.emit(UserUiEvents.Failure(message))
                }.collect {
                    _loginEvent.emit(
                        UserUiEvents.LoginSuccess
                    )
                }
        }
    }

    private fun validateOtp(value: String) {
        viewModelScope.launch {
            userRepository.validateEmail(value)
                .onStart {
                    _loginEvent.emit(
                        UserUiEvents.Loading
                    )
                }.catch {
                    var message = "Something went wrong"
                    if(it is retrofit2.HttpException){
                        if(it.code() == 401){
                            message = "Wrong Credentials"
                        }
                    }
                    _loginEvent.emit(UserUiEvents.Failure(message))
                }.collect {
                    _loginEvent.emit(
                        UserUiEvents.LoginSuccess
                    )
                }
        }
    }
}

