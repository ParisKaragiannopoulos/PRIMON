package com.parisjohn.pricemonitoring.domain.repositories

import android.content.Context
import com.google.gson.Gson
import com.parisjohn.pricemonitoring.base.data.MonitorApiService
import com.parisjohn.pricemonitoring.data.network.request.MonitorListRequest
import com.parisjohn.pricemonitoring.data.network.request.SearchHotelRequest
import com.parisjohn.pricemonitoring.data.network.request.UpdateMonitorListRequest
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.data.network.response.PriceRoomResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import javax.inject.Inject


class MonitorRepository @Inject constructor(
    @ApplicationContext private var context: Context,
    private val service: MonitorApiService, private val dispatcher: CoroutineDispatcher,
) {

    suspend fun postNewMonitorList(monitorListRequest: MonitorListRequest): Flow<ResponseBody> =
        flow {
            emit(service.postNewMonitorList(monitorListRequest))
        }.flowOn(dispatcher)

    suspend fun getAllMonitorLists(): Flow<MonitorListsResponse> = flow {
        emit(service.getAllMonitorLists())
    }.flowOn(dispatcher)

    suspend fun getListDetails(id: String): Flow<MonitorListResponse> = flow {
        emit(service.getMonitorListDetails(id))
    }.flowOn(dispatcher)
    suspend fun deleteMonitorList(id: Long): Flow<ResponseBody> = flow {
        emit(service.deleteMonitorList(id))
    }.flowOn(dispatcher)

    suspend fun updateMonitorList(monitorListRequest: UpdateMonitorListRequest): Flow<ResponseBody> = flow {
      emit(service.updateMonitorList(monitorListRequest))
    }.flowOn(dispatcher)
    suspend fun searchHotelDetails(link: String): Flow<HotelInfoResponse> {
        val fl = service.getHotelInfo(SearchHotelRequest(link))
        if(fl.rooms.isEmpty()){
            try {
                val assetManager = context.assets
                val ims = assetManager.open("hotel_search.json")
                val gson = Gson()
                val reader: Reader = InputStreamReader(ims)
                val gsonObj:HotelInfoResponse  = gson.fromJson(reader, HotelInfoResponse::class.java)
                fl.rooms = gsonObj.rooms
            } catch (e: IOException) {
                e.printStackTrace()
                return flow{emit(fl)}
            }
        }
        return flow{emit(fl)}
    }

    suspend fun getPricesOfSpecificRoom(id: String, size: Int): Flow<PriceRoomResponse> = flow {
        emit(service.getPricesOfSpecificRoom(id,size))
    }.flowOn(dispatcher)
}