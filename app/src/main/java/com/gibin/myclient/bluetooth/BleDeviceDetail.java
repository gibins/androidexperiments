package com.gibin.myclient.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gibin.myclient.R;

import java.util.List;

public class BleDeviceDetail extends AppCompatActivity {

    public static BluetoothDevice device = null;
    TextView devcieAddr = null;
    TextView serviceDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BLE Device Details");
        setContentView(R.layout.activity_ble_device_detail);

        devcieAddr = (TextView) findViewById(R.id.deviceAddr);
        serviceDetails = (TextView) findViewById(R.id.serviceDetails);


        if (null != device) {

            Log.d("myclient", "Devcie Details " + device.getAddress());
            devcieAddr.setText(devcieAddr.getText().toString() + device.getAddress());

            device.connectGatt(BleDeviceDetail.this, true, gattCallback);


        }
    }

    protected BluetoothGattCallback gattCallback = new
            BluetoothGattCallback() {
                @Override
                public void
                onConnectionStateChange(BluetoothGatt
                                                gatt, int status,
                                        int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        Log.i("myclient", "onConnectionStateChange() - STATE_CONNECTED");
                        boolean discoverServicesOk = gatt.discoverServices();
                        Log.d("myclient",""+gatt.readRemoteRssi());
                    } else if (newState ==
                            BluetoothGatt.STATE_DISCONNECTED) {
                        Log.i("myclient", "onConnectionStateChange() - STATE_DISCONNECTED");
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    final List<BluetoothGattService> services = gatt.getServices();
                    Log.d("myclient", "Service count " + services.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder deviceDetails = new StringBuilder();

                            for (int i = 0; i < services.size(); i++) {
                                Log.d("myclient", "Service " + i + " -> " + services.get(i).getUuid().toString());
                                BluetoothGattService service = services.get(i);
                                deviceDetails.append("\n Service: " + i + " " + services.get(i).getUuid().toString() + " \n");
                                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                                Log.d("myclient", "Chracteristics found -> " + characteristics.size());
                                for (int j = 0; j < characteristics.size(); j++) {
                                    Log.d("myclient", "Chracteristics " + j + " " + characteristics.get(j).getUuid().toString());
                                    deviceDetails.append("\n Charcteristic: " + j + " " + characteristics.get(j).getUuid().toString() + " \n");

                                    // Log.d("myclient",""+characteristics.get(j).getValue().toString());
                                   // readProperties(characteristics.get(j));
                                }
                            }
                            serviceDetails.setText(deviceDetails.toString());
                        }
                    });
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);


                    readProperties(characteristic);

                }
            };


    public void readProperties(BluetoothGattCharacteristic characteristic) {
        if (null != characteristic) {
            if(null != characteristic.getValue()){
                Log.d("myclient", "byte to string "+characteristic.getValue().toString());
            }

            Log.d("myclient", "Read string value from characteristics " + characteristic.getStringValue(1));

            if (null != characteristic.getStringValue(1) ) {
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                }
                if (null != characteristic.getIntValue(format, 1)) {
                    final int heartRate = characteristic.getIntValue(format, 1);
                    Log.d("myclient", String.format("Received : %d", heartRate));
                }
            } else {
                Log.d("myclient", "Null reading characteristic");
            }
        }
    }
}