package br.com.fabricasinapse.locktech

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class ConectarActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private val pairedDevices = mutableListOf<BluetoothDevice>()
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                if (context != null && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                Toast.makeText(context, "Dispositivo encontrado: ${device.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val connectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                val device: BluetoothDevice = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                if (context != null && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                Log.d("Bluetooth", "Conectado: ${device.name}")
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                val device: BluetoothDevice = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                Log.d("Bluetooth", "Desconectado: ${device.name}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        listView = findViewById(R.id.listView)

        val btnEnableBluetooth: Button = findViewById(R.id.btnEnableBluetooth)
        btnEnableBluetooth.setOnClickListener {
            enableBluetooth()
        }

        val btnListDevices: Button = findViewById(R.id.btnListDevices)
        btnListDevices.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                listPairedDevices()
                checkLocationPermissionAndDiscover()  // Verifica a permissão e inicia a descoberta
            } else {
                Toast.makeText(this, "Bluetooth não está ativado", Toast.LENGTH_SHORT).show()
            }
        }

        // Registrar o BroadcastReceiver para detectar dispositivos
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        // Registrar o BroadcastReceiver para conexão
        val connectionFilter = IntentFilter()
        connectionFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        connectionFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(connectionReceiver, connectionFilter)
    }

    private fun enableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            Toast.makeText(this, "Bluetooth já está ativado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listPairedDevices() {
        pairedDevices.clear()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        pairedDevices.addAll(bluetoothAdapter.bondedDevices)

        val deviceList = pairedDevices.map { "${it.name} (${it.address})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView.adapter = adapter

        Log.d("Bluetooth", "Dispositivos emparelhados: $deviceList")

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = pairedDevices[position]
            connectToDevice(device)
        }
    }

    private fun checkLocationPermissionAndDiscover() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Solicita a permissão de localização
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permissão já foi concedida, então iniciamos a descoberta de dispositivos
            startDiscovery()
        }
    }

    private fun startDiscovery() {
        if (bluetoothAdapter.isEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter.startDiscovery()
            Toast.makeText(this, "Iniciando descoberta de dispositivos...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bluetooth não está ativado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Toast.makeText(this, "Conectando a ${device.name}", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val socket = device.createRfcommSocketToServiceRecord(MY_UUID)
                socket.connect()
                runOnUiThread {
                    Toast.makeText(this, "Conectado a ${device.name}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                runOnUiThread {
                    Toast.makeText(this, "Falha na conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se a permissão for concedida, inicia a descoberta
                startDiscovery()
            } else {
                // Se a permissão for negada, informa o usuário
                Toast.makeText(this, "Permissão de localização necessária para descobrir dispositivos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(connectionReceiver) // Desregistrar o receiver de conexão
    }
}




