package com.example.environmentmoniter.data

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/api/data/latest")
    suspend fun getLatestData(): Response<SensorData>
}
