package com.example.environmentmoniter.data

import android.bluetooth.*
import android.content.Context
import android.util.Log

class BleService(private val context: Context) {

    private var bluetoothGatt: BluetoothGatt? = null

    // 🔹 BluetoothDevice를 인자로 받는 함수
    fun connect(device: BluetoothDevice) {
        try {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            Log.d("BLE_DEBUG", "🔵 GATT 연결 시도: ${device.address}")
        } catch (e: SecurityException) {
            Log.e("BLE_DEBUG", "❌ GATT 연결 시도 실패 (권한 문제): ${e.message}")
        } catch (e: Exception) {
            Log.e("BLE_DEBUG", "❌ GATT 연결 오류: ${e.message}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("BLE_DEBUG", "✅ GATT 연결 완료 → 서비스 탐색 시작")
                    try {
                        gatt.discoverServices()
                    } catch (e: SecurityException) {
                        Log.e("BLE_DEBUG", "❌ discoverServices() 권한 오류: ${e.message}")
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.e("BLE_DEBUG", "🔴 GATT 연결 끊김")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE_DEBUG", "🧩 GATT 서비스 발견됨")
                gatt.services.forEach { service ->
                    Log.d("BLE_DEBUG", "→ 서비스 UUID: ${service.uuid}")
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val value = characteristic.value
            Log.d("BLE_DEBUG", "📡 받은 데이터: ${value?.contentToString()}")
        }
    }

    fun disconnect() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        Log.d("BLE_DEBUG", "🔴 GATT 연결 종료")
    }
}
