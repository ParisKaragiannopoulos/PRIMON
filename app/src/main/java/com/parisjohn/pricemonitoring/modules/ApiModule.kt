package com.parisjohn.pricemonitoring.modules

import android.content.Context
import com.parisjohn.pricemonitoring.base.data.AuthInterceptor
import com.parisjohn.pricemonitoring.base.data.MonitorApiService
import com.parisjohn.pricemonitoring.base.data.ResponseInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = "http://195.251.123.174:8085/"

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(@ApplicationContext context: Context): AuthInterceptor {
        return AuthInterceptor(context)
    }
    @Singleton
    @Provides
    fun provideResponseInterceptorInterceptor(@ApplicationContext context: Context): ResponseInterceptor {
        return ResponseInterceptor(context)
    }
    @Singleton
    @Provides
    fun providesOkHttpClient(authInterceptor: AuthInterceptor,responseInterceptor : ResponseInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(responseInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): MonitorApiService = retrofit.create(MonitorApiService::class.java)

//    @Singleton
//    @Provides
//    fun providesRepository(apiService: MonitorApiService) = Repository(apiService)
}
