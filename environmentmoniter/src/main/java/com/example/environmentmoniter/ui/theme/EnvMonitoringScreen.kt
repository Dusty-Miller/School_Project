package com.example.environmentmoniter.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.environmentmoniter.data.SensorData
import com.example.environmentmoniter.data.RetrofitInstance
import com.example.environmentmoniter.ui.theme.EnvViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

// 🌈 버튼 공통
@Composable
fun GradientButton(
    text: String,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(38.dp)
            .background(
                brush = Brush.horizontalGradient(colors),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

// 🌍 환경 모니터링 메인 화면
@Composable
fun EnvMonitoringScreen(viewModel: EnvViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val scope = rememberCoroutineScope()
    val background = Color(0xFF0B1228)
    val textColor = Color.White

    val data by viewModel.sensorData.collectAsState()
    val history by viewModel.history.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .padding(top = 12.dp)
    ) {
        // 상단 제목 + 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("환경 모니터링", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text("실시간 대기 상태를 확인하세요", fontSize = 14.sp, color = Color.LightGray)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔄 데이터 새로고침
                GradientButton(
                    "새로고침",
                    listOf(Color(0xFF2196F3), Color(0xFF42A5F5)),
                    onClick = {
                        Log.d("RETROFIT_DEBUG", "🔄 새로고침 버튼 클릭됨 → fetchSensorData 호출")
                        viewModel.fetchSensorData()
                    }
                )

            }
        }

        Spacer(Modifier.height(20.dp))

        // 📦 실시간 카드 3개
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoCard(
                title = "미세먼지",
                value = "${data.dust} µg/m³",
                status = getPmStatus(data.dust),
                statusColor = getPmColor(data.dust),
                modifier = Modifier.weight(1f)
            )

            InfoCard(
                title = "온도",
                value = "${data.temperature}℃",
                status = getTempStatus(data.temperature),
                statusColor = getTempColor(data.temperature),
                modifier = Modifier.weight(1f)
            )

            InfoCard(
                title = "습도",
                value = "${data.humidity}%",
                status = getHumiStatus(data.humidity),
                statusColor = getHumiColor(data.humidity),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // 📈 실시간 데이터 추이
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(Color(0xFF131B3C), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("실시간 데이터 추이", color = Color.White, fontSize = 16.sp)
            EnvLineChart(history = history)
        }
    }
}

// 📊 상태 표시 로직
fun getPmStatus(value: Float): String =
    when {
        value.isNaN() || value <= 0f -> "좋음"
        value < 30 -> "좋음"
        value < 50 -> "보통"
        value < 75 -> "나쁨"
        else -> "매우나쁨"
    }

fun getPmColor(value: Float): Color =
    when {
        value < 30 -> Color(0xFF4CAF50)
        value < 50 -> Color(0xFFFFC107)
        value < 75 -> Color(0xFFFF5252)
        else -> Color(0xFFD50000)
    }

fun getTempStatus(value: Float): String =
    when {
        value < 10 -> "추움"
        value < 20 -> "선선함"
        value < 28 -> "따뜻함"
        else -> "더움"
    }

fun getTempColor(value: Float): Color =
    when {
        value < 10 -> Color(0xFF2196F3)
        value < 20 -> Color(0xFF03A9F4)
        value < 28 -> Color(0xFFFFC107)
        else -> Color(0xFFFF5722)
    }

fun getHumiStatus(value: Float): String =
    when {
        value < 30 -> "건조함"
        value < 60 -> "적정함"
        else -> "습함"
    }

fun getHumiColor(value: Float): Color =
    when {
        value < 30 -> Color(0xFF4FC3F7)
        value < 60 -> Color(0xFF81C784)
        else -> Color(0xFF1976D2)
    }

// 💡 정보 카드
@Composable
fun InfoCard(
    title: String,
    value: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF131B3C), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .background(statusColor, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(status, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// 📈 실시간 라인 차트
@Composable
fun EnvLineChart(history: List<SensorData>) {
    if (history.isEmpty()) return

    val pm25Entries = history.mapIndexed { i, data -> Entry(i.toFloat(), data.dust) }
    val tempEntries = history.mapIndexed { i, data -> Entry(i.toFloat(), data.temperature) }
    val humiEntries = history.mapIndexed { i, data -> Entry(i.toFloat(), data.humidity) }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                val pm25Set = LineDataSet(pm25Entries, "미세먼지").apply {
                    color = android.graphics.Color.CYAN
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                }
                val tempSet = LineDataSet(tempEntries, "온도").apply {
                    color = android.graphics.Color.YELLOW
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                }
                val humiSet = LineDataSet(humiEntries, "습도").apply {
                    color = android.graphics.Color.GREEN
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                }

                data = LineData(pm25Set, tempSet, humiSet)
                axisRight.isEnabled = false
                xAxis.textColor = android.graphics.Color.LTGRAY
                axisLeft.textColor = android.graphics.Color.LTGRAY
                legend.textColor = android.graphics.Color.WHITE
                description = Description().apply { text = "" }
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .background(Color(0xFF131B3C), RoundedCornerShape(16.dp))
            .padding(8.dp)
    )
}
