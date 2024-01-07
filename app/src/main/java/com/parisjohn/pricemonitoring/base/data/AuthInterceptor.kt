package com.parisjohn.pricemonitoring.base.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(@ApplicationContext context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken().let {
            if(it.isNullOrEmpty().not()){
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}