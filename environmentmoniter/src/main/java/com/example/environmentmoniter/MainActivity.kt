package com.example.environmentmoniter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.environmentmoniter.ui.EnvMonitoringScreen
import com.example.environmentmoniter.ui.theme.EnvMonitorTheme

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
    // ✅ 권한 요청 로직
    RequestBlePermissions()
    // ✅ 메인 화면
    EnvMonitoringScreen()
}

@Composable
fun RequestBlePermissions() {
    val context = LocalContext.current // ✅ 반드시 Composable 내부에서 선언해야 함

    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            Log.d("BLE_DEBUG", "✅ 모든 BLE 권한 허용됨")
        } else {
            Log.e("BLE_DEBUG", "❌ 일부 BLE 권한 거부됨 — BLE 통신 불가")
        }
    }

    LaunchedEffect(Unit) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            Log.d("BLE_DEBUG", "📡 권한 요청 실행")
            permissionLauncher.launch(permissions)
        } else {
            Log.d("BLE_DEBUG", "✅ 모든 BLE 권한 이미 허용됨")
        }
    }
}
