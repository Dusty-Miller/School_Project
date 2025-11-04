package com.example.map

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.net.URLDecoder
import android.util.Log
class AirQualityRepository {

    private val TAG = "AirQualityRepository"

    // 공공데이터포털 일반 인증키 (그대로 사용)
    private val serviceKey = "6c549226b1d12a833ada73516d6c902581e58510a3cf814f84d69714a4af21f8"

    private val baseUrl = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc"

    suspend fun fetchAirQualityData(sido: String): List<AirQualityPoint> =
        withContext(Dispatchers.IO) {
            try {
                val encodedSido = URLEncoder.encode(sido, "UTF-8")

                val urlStr = "$baseUrl/getCtprvnRltmMesureDnsty" +
                        "?serviceKey=$serviceKey" +   // 인코딩하지 말 것!
                        "&returnType=json" +
                        "&numOfRows=50" +
                        "&pageNo=1" +
                        "&sidoName=$encodedSido" +
                        "&ver=1.0"

                Log.d(TAG, "API URL = $urlStr")

                val conn = URL(urlStr).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 30000
                conn.readTimeout = 90000

                val responseCode = conn.responseCode
                Log.d(TAG, "Response code = $responseCode")

                val responseText = if (responseCode == 200) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }

                Log.d(TAG, "API Response = $responseText")

                if (responseCode != 200) return@withContext emptyList<AirQualityPoint>()

                val json = JSONObject(responseText)
                val items = json.getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONArray("items")

                val result = mutableListOf<AirQualityPoint>()
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val region = item.getString("stationName")
                    val pm10 = item.optString("pm10Value", "0").toIntOrNull() ?: 0
                    val coords = getCoordinates(region)
                    result.add(AirQualityPoint(region, coords.first, coords.second, pm10))
                }

                result

            } catch (e: Exception) {
                Log.e(TAG, "❌ API 호출 중 예외 발생", e)
                emptyList()
            }
        }

    private fun getCoordinates(region: String): Pair<Double, Double> = when (region) {
        "강남구" -> 37.5172 to 127.0473
        "서초구" -> 37.4836 to 127.0325
        "종로구" -> 37.5730 to 126.9794
        "마포구" -> 37.5665 to 126.9018
        "송파구" -> 37.5146 to 127.1059
        "강서구" -> 37.5509 to 126.8495
        else -> 37.5665 to 126.9780
    }
}

