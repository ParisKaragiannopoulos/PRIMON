package com.parisjohn.pricemonitoring.base.data


import com.parisjohn.pricemonitoring.data.network.request.MonitorListRequest
import com.parisjohn.pricemonitoring.data.network.request.MonitorListUpdateRequest
import com.parisjohn.pricemonitoring.data.network.request.SearchHotelRequest
import com.parisjohn.pricemonitoring.data.network.request.UserLoginRequest
import com.parisjohn.pricemonitoring.data.network.request.UserRegisterRequest
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.data.network.response.PriceRoomResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MonitorApiService {

    @GET("/monitor_list/{id}")
    suspend fun getMonitorListDetails(@Path("id") id: String): MonitorListResponse

    @POST("customer/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest): Response<String>

    @POST("customer/register")
    suspend fun register(@Body userRegisterRequest: UserRegisterRequest): ResponseBody

    @POST("hotel/info")
    suspend fun getHotelInfo(
      @Body searchHotelRequest: SearchHotelRequest
    ): HotelInfoResponse

    @POST("monitor_list")
    suspend fun postNewMonitorList(@Body monitorListRequest: MonitorListRequest): ResponseBody

    @GET("monitor_list")
    suspend fun getAllMonitorLists(): MonitorListsResponse

    @DELETE("monitor_list/{id}")
    suspend fun deleteMonitorList(@Path("id") id: Long): ResponseBody

    @PUT("monitor_list")
    suspend fun updateMonitorList(@Body monitorListRequest: MonitorListUpdateRequest): ResponseBody

    @GET("/prices/{roomID}")
    suspend fun getPricesOfSpecificRoom(@Path("roomID") roomID: String): PriceRoomResponse
}