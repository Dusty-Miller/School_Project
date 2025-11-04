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

    private var isFetching = false // ✅ 중복 호출 방지용 플래그

    fun fetchSensorData() {
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            try {
                // ✅ 에뮬레이터에서 PC 서버 접근은 반드시 10.0.2.2 사용
                val url = "http://10.0.2.2:5000/api/data/latest"
                Log.d("RETROFIT_DEBUG", "📡 요청 시작: $url")

                val response = RetrofitInstance.api.getLatestData()

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        Log.d("RETROFIT_DEBUG", "✅ 응답 성공: $data")

                        _sensorData.value = data

                        // 📈 그래프용 히스토리 누적 (최근 50개까지만 유지)
                        val updatedList = _history.value.toMutableList().apply {
                            add(data)
                            if (size > 50) removeAt(0)
                        }
                        _history.value = updatedList
                    } else {
                        Log.w("RETROFIT_DEBUG", "⚠️ 응답 Body가 null임")
                    }
                } else {
                    Log.e("RETROFIT_DEBUG", "❌ 서버 응답 실패: ${response.code()} ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e("RETROFIT_DEBUG", "💥 요청 중 예외 발생: ${e.localizedMessage}")
            } finally {
                isFetching = false
            }
        }
    }
}

