package com.gibin.myclient.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gibin.myclient.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothMainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    private boolean scanning;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;

    private BluetoothLeScanner bluetoothLeScanner = null;

    private Handler handler = null;

    private ScanCallback leScanCallback = null;

    TextView textView  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Bluetooth Activity");
        setContentView(R.layout.activity_bluetooth_main);

        checkPermission();

        textView = findViewById(R.id.TEXT_STATUS_ID);
        textView.setText("Data Logger");
        Log.d("Activity Started","ActivityStarted");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice();
            }
        });


        // Crashes when Bluetooth Adapter is initialized
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler();

      //LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();

        // Device scan callback.
        leScanCallback =
                new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        textView.setText(textView.getText() + "\n" + result.getDevice());
                        /*leDeviceListAdapter.addDevice(result.getDevice());
                        leDeviceListAdapter.notifyDataSetChanged();*/
                    }
                };


    }

    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setText(textView.getText() + "\n" + "Scan Stopped");
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            textView.setText(textView.getText() + "\n" + "Scan Started");
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            textView.setText(textView.getText() + "\n" + "Scan Stopped");
        }
    }

    public void checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED){

            // permission is already granted


        }else{

            //persmission is not granted yet
            //Asking for permission
            ActivityCompat.requestPermissions(this,new String[]   {
                    Manifest.permission.READ_CONTACTS},100);

        }
    }

}