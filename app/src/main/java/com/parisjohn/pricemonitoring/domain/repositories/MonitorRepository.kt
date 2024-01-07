package com.parisjohn.pricemonitoring.domain.repositories

import com.parisjohn.pricemonitoring.base.data.MonitorApiService
import com.parisjohn.pricemonitoring.data.network.request.MonitorListRequest
import com.parisjohn.pricemonitoring.data.network.request.SearchHotelRequest
import com.parisjohn.pricemonitoring.data.network.response.HotelInfoResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListResponse
import com.parisjohn.pricemonitoring.data.network.response.MonitorListsResponse
import com.parisjohn.pricemonitoring.data.network.response.PriceRoomResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import javax.inject.Inject


class MonitorRepository @Inject constructor(
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

    suspend fun searchHotelDetails(link: String): Flow<HotelInfoResponse> = flow {
        emit(service.getHotelInfo(SearchHotelRequest(link)))
    }.flowOn(dispatcher)

    suspend fun getPricesOfSpecificRoom(id: String): Flow<PriceRoomResponse> = flow {
        emit(service.getPricesOfSpecificRoom(id))
    }.flowOn(dispatcher)
}