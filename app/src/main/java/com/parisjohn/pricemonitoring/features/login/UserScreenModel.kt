package com.parisjohn.pricemonitoring.features.login


data class UserState(val user: User = User(),
                     var loadingState: LoadingState = LoadingState.IDLE,
                     var isAuthenticated: Boolean = false)

data class User(val email: String = "", val password: String= "",val name: String= "", val repeatedPassword: String= "") {
    fun isEmpty(): Boolean {
        return email.isEmpty() || password.isEmpty() || name.isEmpty() || repeatedPassword.isEmpty()
    }
}

sealed class UserIntent {
    data class Login(val user: User = User()) : UserIntent()
    data class Register(val user: User = User()) : UserIntent()
    data class OTP(val value: String = ""): UserIntent()
}

enum class LoadingState {
    LOADING,
    IDLE
}