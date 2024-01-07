package com.parisjohn.pricemonitoring.base.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response


class ResponseInterceptor(
    @ApplicationContext private var context: Context,
) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            sessionManager.fetchAuthToken()?.let {
                sessionManager.saveAuthToken(null)
                sessionManager.clearSession()
            }
        }
        return response;
    }
}