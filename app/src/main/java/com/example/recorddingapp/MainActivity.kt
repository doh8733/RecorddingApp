package com.example.recorddingapp

import android.Manifest.permission.*
import android.bluetooth.*
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val REQUEST_AUDIO_PERMISSION_CODE = 101

class MainActivity : AppCompatActivity() {
    private val btnStart: Button by lazy { findViewById<Button>(R.id.btn_start) }
    private val btnStopsRecord: Button by lazy { findViewById<Button>(R.id.btn_stops_record) }
    private val btnPlay: Button by lazy { findViewById<Button>(R.id.btn_play) }
    private val btnStop: Button by lazy { findViewById<Button>(R.id.btn_stop) }
    private val bluetoothStateMonitor: BluetoothStateMonitor by lazy { BluetoothStateMonitor() }
    val btAdapter = BluetoothAdapter.getDefaultAdapter()
    private val filter: IntentFilter by lazy { IntentFilter(BluetoothDevice.ACTION_FOUND) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        applicationContext.registerReceiver(bluetoothStateMonitor, filter)
        connectionBluetooth()
        mPlay = MediaPlayer()
        mediaRecord = MediaRecorder()

//        if (checkPermissions()) {
//            onReceiveBluetooth(this)
//        } else {
//            requestPermissions()
//        }
//        onReceiveBluetooth(this)
        btnStart.setOnClickListener {
            startRecord()
        }
        btnStopsRecord.setOnClickListener {
            pauseRecording()
        }
        btnPlay.setOnClickListener {
            play()
        }
        btnStop.setOnClickListener {
            pause()
        }
//        if (isBluetoothHeadsetConnected()){
//            Toast.makeText(this, "Thiet bi da ket noi bluetooth", Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(this, "Thiet bi khong ket noi bluetooth", Toast.LENGTH_SHORT).show()
//
//        }
    }

    fun connectionBluetooth() {
        val headsetBluetooth = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(p0: Int, p1: BluetoothProfile?) {
                if (p0 == BluetoothProfile.HEADSET) {
                    val btHeadset = p1 as BluetoothHeadset
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            BLUETOOTH
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        requestPermissions()
                        return
                    }
                    if (btHeadset.connectedDevices.size > 0) {
                        Toast.makeText(
                            this@MainActivity,
                            "Ket noi thanh cong ${btHeadset.connectedDevices.first().name}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "khong co thiet bi ket noi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onServiceDisconnected(p0: Int) {
                if (p0 == BluetoothProfile.HEADSET) {
                    Toast.makeText(
                        this@MainActivity, "disconnect bluetooth", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        btAdapter.getProfileProxy(this, headsetBluetooth, BluetoothProfile.HEADSET)
    }

    private fun requestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO),
            REQUEST_AUDIO_PERMISSION_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        // this method is used to check permission
//        val result = ContextCompat.checkSelfPermission(applicationContext, BLUETOOTH)
//        val result2 = ContextCompat.checkSelfPermission(applicationContext, BLUETOOTH_ADMIN)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        val result3 = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)


        return result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
        //&& result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
        //&& result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> {
                if (grantResults.size > 0) {
//                    val permissionToRecord = grantResults[0] === PackageManager.PERMISSION_GRANTED
//                    val permissionToStore = grantResults[1] === PackageManager.PERMISSION_GRANTED
                    val permissionToStore = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val permissionToStore2 = grantResults[1] == PackageManager.PERMISSION_GRANTED
//                    val permissionToStore3 = grantResults[2] == PackageManager.PERMISSION_GRANTED
//                    val permissionToStore4 = grantResults[3] == PackageManager.PERMISSION_GRANTED
                    if (permissionToStore && permissionToStore2) {
                        Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private var mPath = ""
    private var mFilename = ""
    private lateinit var mediaRecord: MediaRecorder
    private lateinit var mPlay: MediaPlayer

    private fun startRecord() {
        if (checkPermissions()) {
            mFilename = Environment.getExternalStorageDirectory().absolutePath
            mFilename += "/mAudioRecord.3gp"
            //set dau vao
            mediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC)
            //set dau ra
            mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            //set endcoder
            mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            //set filepath
            mediaRecord.setOutputFile(mFilename)
            try {
                mediaRecord.prepare()
            } catch (e: Exception) {
                Log.e("TAG", "startRecord: $e")
            }
            mediaRecord.start()
            Log.e("TAG", "startRecord: $mFilename")

        } else {
            requestPermissions()
        }
    }

    private fun play() {
        mPlay.setDataSource(mFilename)
        mPlay.prepare()
        mPlay.start()
        try {

        } catch (e: Exception) {
            Log.e("TAG", "play: $e")
        }
    }

    private fun pauseRecording() {
        mediaRecord.stop()
        mediaRecord.release()
//        mediaRecord = null as MediaRecorder
    }

    private fun pause() {
        mPlay.release()
//        mPlay = null as MediaPlayer
    }

    lateinit var bluetoothAdapter: BluetoothAdapter


//        private fun isBluetoothHeadsetConnected(): Boolean {
//            BluetoothAdapter.getDefaultAdapter()
//            return if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            } else {
//               return bluetoothAdapter != null && bluetoothAdapter.isEnabled && bluetoothAdapter.getProfileConnectionState(
//                    BluetoothHeadset.HEADSET
//               ) == BluetoothAdapter.STATE_CONNECTED
//            }
//
//        }


    //    private lateinit var bluetoothHeadset : BluetoothHeadset
//    var mProfileListener = object : BluetoothProfile.ServiceListener{
//        override fun onServiceConnected(p0: Int, p1: BluetoothProfile?) {
//            if (p0 == BluetoothProfile.HEADSET){
//                bluetoothHeadset = p1 as BluetoothHeadset
//                if (ActivityCompat.checkSelfPermission(
//                        this@MainActivity,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return
//                }
//                if (bluetoothHeadset.connectedDevices.size >0){
//                    IS_B
//                }
//            }
//        }
//
//        override fun onServiceDisconnected(p0: Int) {
//
//        }
//
//    }
    var isConnectedBluetooth = false
    lateinit var btA2dp: BluetoothA2dp
    lateinit var a2dpConnectedDevice: List<BluetoothDevice>
//    private fun onReceiveBluetooth(context: Context) {
//        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        var mProfileListener = object : BluetoothProfile.ServiceListener {
//            override fun onServiceConnected(p0: Int, p1: BluetoothProfile?) {
//                if (p0 == BluetoothProfile.A2DP) {
//                    isConnectedBluetooth = false
//                    btA2dp = p1 as BluetoothA2dp
//                    if (ActivityCompat.checkSelfPermission(
//                            context,
//                            BLUETOOTH
//                        ) != PackageManager.PERMISSION_GRANTED
//                    ) {
//                        requestPermissions()
//                    }
//                    a2dpConnectedDevice = btA2dp.connectedDevices
//                    if (a2dpConnectedDevice.isNotEmpty()) {
//                        isConnectedBluetooth = true
//                        Toast.makeText(
//                            context,
//                            "Thiet bi ket noi bluetooth",
//                            Toast.LENGTH_SHORT
//                        ).show()                    }
//                    if (!isConnectedBluetooth) {
//                        Toast.makeText(
//                            context,
//                            "Thiet bi khong ket noi bluetooth",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, btA2dp)
//                }
//            }
//
//            override fun onServiceDisconnected(p0: Int) {
//                Toast.makeText(context, "Thiet bi ngat ket noi bluetooth", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//        }
//        mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP)
//    }


}