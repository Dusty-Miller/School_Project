package com.example.environmentmoniter.data

import android.os.Build
import com.github.mikephil.charting.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val BASE_URL = if (Build.FINGERPRINT.contains("generic")) {
        // 에뮬레이터 환경
        "http://10.0.2.2:5000/"
    } else {
        // 실제 기기 (라즈베리파이 IP)
        "http://192.168.200.21:5000/"
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
