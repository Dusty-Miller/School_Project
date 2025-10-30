package com.example.environmentmoniter.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.environmentmoniter.data.RetrofitInstance
import com.example.environmentmoniter.data.SensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnvViewModel : ViewModel() {
    private val _sensorData = MutableStateFlow(SensorData(0f, 0f, 0f))
    val sensorData: StateFlow<SensorData> = _sensorData

    private val _history = MutableStateFlow<List<SensorData>>(emptyList())
    val history: StateFlow<List<SensorData>> = _history

    fun fetchSensorData() {
        viewModelScope.launch {
            try {
                Log.d("RETROFIT_DEBUG", "📡 요청 시작: http://192.168.200.17:5000/api/data/latest")
                val response = RetrofitInstance.api.getLatestData() // Response<SensorData>

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        Log.d("RETROFIT_DEBUG", "✅ 응답 성공: $data")
                        _sensorData.value = data
                        _history.value = _history.value + data
                    } else {
                        Log.e("RETROFIT_DEBUG", "⚠️ 응답 Body가 null임")
                    }
                } else {
                    Log.e("RETROFIT_DEBUG", "❌ 서버 응답 실패: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("RETROFIT_DEBUG", "💥 요청 중 예외 발생: ${e.message}")
            }
        }
    }
}
