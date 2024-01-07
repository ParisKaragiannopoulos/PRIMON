package com.parisjohn.pricemonitoring.domain.repositories

import com.parisjohn.pricemonitoring.base.data.MonitorApiService
import com.parisjohn.pricemonitoring.data.network.request.UserLoginRequest
import com.parisjohn.pricemonitoring.data.network.request.UserRegisterRequest
import com.parisjohn.pricemonitoring.features.login.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


class UserRepository @Inject constructor(private val service: MonitorApiService, private val dispatcher: CoroutineDispatcher,
) {

    suspend fun loginUser(email: String, password: String): Flow<Response<String>> = flow {
       emit(service.login(UserLoginRequest(email,password)))
    }.flowOn(dispatcher)

    suspend fun signupUser(user: User): Flow<ResponseBody> = flow {
        emit(service.register(UserRegisterRequest(user.email,user.name,user.password,user.repeatedPassword)))
    }.flowOn(dispatcher)

    suspend fun validateEmail(value: String): Flow<String> = flow {
        Thread.sleep(3000)
        emit("")
    }.flowOn(dispatcher)
}