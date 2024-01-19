package com.gibin.myclient.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gibin.myclient.R;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class BluetoothMainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    private boolean scanning;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;

    private BluetoothLeScanner bluetoothLeScanner = null;

    private Handler handler = null;


    TextView textView = null;

    ListView deviceListview = null;

    private final static int REQUEST_ENABLE_BT = 1;

    ArrayAdapter<String> listAdapter = null;
    ArrayList<BluetoothDevice> deviceList = null;

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (null != result) {
                Log.d("myclient", "Device found " + result.getDevice().getAddress());
                String deviceName = (null != result.getDevice().getName()) ? (result.getDevice().getAddress() + " ( " + result.getDevice().getName() + " ) " + callbackType) : (deviceName = result.getDevice().getAddress() + " " + callbackType);
                //super.onScanResult(callbackType, result);
                textView.setText(textView.getText() + "\n" + deviceName);
                Log.d("myclient", deviceName);

                if (result.getDevice() != null) {
                    if (!isDuplicate(result.getDevice())) {
                        synchronized (result.getDevice()) {
                            String itemDetail = result.getDevice().getName() == null ? result.getDevice().getAddress() : result.getDevice().getName();
                            listAdapter.add(itemDetail);
                            deviceList.add(result.getDevice());
                        }
                    }


                } else {
                    Log.d("myclient", "null result, callbacktype " + callbackType);
                }
            }

        }
    };

    protected BluetoothGattCallback gattCallback = new
            BluetoothGattCallback() {
                @Override public void
                onConnectionStateChange(BluetoothGatt
                                                gatt, int status,
                                        int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        Log.i("myclient", "onConnectionStateChange() - STATE_CONNECTED");
                        boolean discoverServicesOk = gatt.discoverServices();
                    } else if (newState ==
                            BluetoothGatt.STATE_DISCONNECTED) {
                        Log.i("myclient", "onConnectionStateChange() - STATE_DISCONNECTED");}}

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    final List<BluetoothGattService> services = gatt.getServices();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < services.size(); i++) {
                                BluetoothGattService service = services.get(i);
                                StringBuffer buffer = new
                                        StringBuffer
                                        (services.get(i).getUuid().toString());
                                List <BluetoothGattCharacteristic> characteristics =
                                        service.getCharacteristics();
                                for (int j = 0; j < characteristics.size(); j++) {
                                    buffer.append("\n");
                                    buffer.append("Characteristic:" + characteristics.get(j).getUuid().toString());
                                   // Log.d("myclient",""+characteristics.get(j).getValue().toString());
                                }
                                listAdapter.add(buffer.toString());}}});
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    int flag = characteristic.getProperties();
                    int format = -1;
                    if ((flag & 0x01) != 0) {
                        format =
                                BluetoothGattCharacteristic.FORMAT_UINT16;
                    } else {
                        format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    }
                    final int heartRate =
                            characteristic.getIntValue(format,
                                    1);
                    Log.d("myclient", String.format("Received : %d", heartRate));
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Bluetooth Low Energy");
        setContentView(R.layout.activity_bluetooth_main);
        textView = (TextView) findViewById(R.id.TEXT_STATUS_ID);
        textView.setText("Data Logger");

        deviceListview = (ListView) findViewById(R.id.deviceListView);

        Log.d("Activity Started", "ActivityStarted");

        checkPermission();

        checkBtAvailable();

        // Crashes when Bluetooth Adapter is initialized
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        Log.d("myclient", "BTA " + bluetoothAdapter.getName());
        Log.d("myclient", "BLE SC " + bluetoothLeScanner);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myclient", "clicked");

                scanLeDevice();

                // startScanning();
            }
        });

        deviceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("myclient","On item click listener pos"+i);
                //stopScanning();
                listAdapter.clear();
                bluetoothLeScanner.stopScan(leScanCallback);
                BluetoothDevice device = deviceList.get(i);
               /* device.connectGatt(BluetoothMainActivity.this, true,
                        gattCallback);*/
                BleDeviceDetail.device = null;
                        BleDeviceDetail.device = device;

                Intent toNleDeviceDetails = new Intent(BluetoothMainActivity.this,BleDeviceDetail.class);

                startActivity(toNleDeviceDetails);

            }
        });

        handler = new Handler();

        //LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceList = new ArrayList<>();
        deviceListview.setAdapter(listAdapter);


    }

    private void checkBtAvailable() {

        if (bluetoothAdapter != null) {
            Log.d("myclient", "Bluetooth not available");
        }

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            //doSomeOperations();
                            Log.d("myclient", "Intenet launcher" + data.toString());
                        }
                    }
                });

        if (bluetoothAdapter != null &&
                !bluetoothAdapter.isEnabled()) {
            Log.d("myclient", "Bluetooth disabled");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityResultLauncher.launch(enableIntent);

        }
    }

    private void scanLeDevice() {
        Log.d("myclient", "inside scanning decision");
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setText(textView.getText() + "\n" + "Scan Stopped");
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    bluetoothLeScanner.flushPendingScanResults(leScanCallback);
                    Log.d("myclient", "flushed " + bluetoothLeScanner.toString());
                }
            }, SCAN_PERIOD);
            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            textView.setText(textView.getText() + "\n" + "Scan Started");
            Log.d("myclient", "scanning started " + bluetoothLeScanner.toString());

        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            textView.setText(textView.getText() + "\n" + "Scan Stopped");
        }
    }

    public void checkPermission() {
        Log.d("myclient", "Permission Request started");

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                "android.permission.BLUETOOTH_SCAN",
                "android.permission.BLUETOOTH_CONNECT"

        }, 100);

        Log.d("myclient", "Permission Request stopped");


    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        Log.d("myclient", "Grant Results" + grantResults.length);
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        switch (permsRequestCode) {

            case 100:

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                break;

        }

    }


    public void startScanning() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("myclient", "Scanning started");
                bluetoothLeScanner.startScan(leScanCallback);
            }
        });
    }

    private boolean isDuplicate(BluetoothDevice device) {
        for (int i = 0; i < listAdapter.getCount(); i++) {
            String addedDeviceDetail =
                    listAdapter.getItem(i);
            if (addedDeviceDetail.equals(device.getAddress()) || addedDeviceDetail.equals(device.getName()))
            { return true; }
        } return false;
    }


}