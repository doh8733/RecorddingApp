package com.example.recorddingapp

import android.Manifest
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

class BluetoothStateMonitor() : BroadcastReceiver() {
    var isConnectedBluetooth = false
    lateinit var btA2dp: BluetoothA2dp
    lateinit var a2dpConnectedDevice: List<BluetoothDevice>
    override fun onReceive(p0: Context?, p1: Intent?) {
        Toast.makeText(p0, "okokokok", Toast.LENGTH_SHORT).show()
        val stateConnection = p1!!.extras!!.getInt(BluetoothAdapter.EXTRA_CONNECTION_STATE)
        onReceiveBluetooth(p0)
    }

    private fun onReceiveBluetooth(context: Context?) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var mProfileListener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(p0: Int, p1: BluetoothProfile?) {
                if (p0 == BluetoothProfile.A2DP) {
                    isConnectedBluetooth = false
                    btA2dp = p1 as BluetoothA2dp
                    if (context?.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        } != PackageManager.PERMISSION_GRANTED
                    ) {
//                        Toast.makeText(context, "Chua ket noi bluetooth", Toast.LENGTH_SHORT).show()

                        return
                    }
//                    a2dpConnectedDevice = btA2dp.connectedDevices
//                    if (a2dpConnectedDevice.isNotEmpty()){
//                        a2dpConnectedDevice.forEach {device ->
//                            if (device.name.contains("DEVICE_NAME")){
//                                isConnectedBluetooth = true
//                            }
//                        }
//                    }
//                    if (!isConnectedBluetooth){
//                        Toast.makeText(context, "Thiet bi khong ket noi bluetooth", Toast.LENGTH_SHORT).show()
//                    }
//                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,btA2dp,)
                }
            }

            override fun onServiceDisconnected(p0: Int) {
                Toast.makeText(context, "Thiet bi ngat ket noi bluetooth", Toast.LENGTH_SHORT).show()
            }

        }
        mBluetoothAdapter.getProfileProxy(context,mProfileListener,BluetoothProfile.A2DP)
    }
}