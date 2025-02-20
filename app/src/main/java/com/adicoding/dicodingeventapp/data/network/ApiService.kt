package com.adicoding.dicodingeventapp.data.network

import com.adicoding.dicodingeventapp.data.response.DataEventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int,
        @Query("q") query: String? = null
    ): DataEventResponse

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): DataEventResponse
}
