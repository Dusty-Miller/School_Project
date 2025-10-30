package com.example.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.launch

@Composable
fun NaverMapScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var airData by remember { mutableStateOf(listOf<AirQualityPoint>()) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                getMapAsync { naverMap ->
                    naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(37.5665, 126.9780)))

                    scope.launch {
                        val repo = AirQualityRepository()
                        airData = repo.fetchAirQualityData("서울")

                        airData.forEach { data ->
                            val marker = Marker().apply {
                                position = LatLng(data.lat, data.lng)
                                captionText = "${data.region} (${data.pm10}㎍/㎥)"
                                iconTintColor = getPmColor(data.pm10).toArgb()
                            }
                            marker.map = naverMap
                        }
                    }
                }
            }
        }
    )
}

fun getPmColor(value: Int): Color = when {
    value < 30 -> Color(0xFF4CAF50)
    value < 50 -> Color(0xFFFFC107)
    value < 75 -> Color(0xFFFF5252)
    else -> Color(0xFFD50000)
}

data class AirQualityPoint(
    val region: String,
    val lat: Double,
    val lng: Double,
    val pm10: Int
)
