package com.example.environmentmoniter.data

import retrofit2.http.GET

data class SensorData(
    val dust: Float,
    val temperature: Float,
    val humidity: Float
)

interface SensorApi {
    @GET("data")
    suspend fun getSensorData(): SensorData
}
