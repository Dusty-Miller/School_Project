package com.example.environmentmoniter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.environmentmoniter.ui.EnvMonitoringScreen
import com.example.environmentmoniter.ui.theme.EnvMonitorTheme
import com.example.map.NaverMapScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnvMonitorTheme {
                EnvMonitorApp()
            }
        }
    }
}

@Composable
fun EnvMonitorApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "env_monitor"
    ) {
        // 🌍 환경 모니터링 메인
        composable("env_monitor") {
            EnvMonitoringScreen(
                onNavigateToMap = { navController.navigate("map_screen") }
            )
        }

        // 🗺 네이버 지도 화면
        composable("map_screen") {
            NaverMapScreen()
        }
    }
}
