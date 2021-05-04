package com.example.honorsproj_covid19;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class status extends AppCompatActivity {

    private Button advertiseButton;
    private Button enterCode;
    private Button statButton;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE_BT = 1;
    private static final int ACCESS_LOCATION_REQUEST = 2;
    private Button OnButton;
    private Button OffButton;
    private Button discoverButton;
    private TextView text;
    private BluetoothAdapter myBlueToothAdapter;
    private BluetoothLeScanner myBlueLeScanner;
    private Handler newHandler = new Handler();
    private boolean scanning;
    private static final long SCAN_PERIOD = 1000000000;
    private ArrayAdapter<String> BluetoothArray;

    private ScanCallback newScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result == null || result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName()) )
                return;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e( "BLE", "Discovery onScanFailed: " + errorCode );
            super.onScanFailed(errorCode);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        statButton = findViewById(R.id.statButton);
        OnButton = (Button) findViewById(R.id.turnOn);
        OffButton = (Button) findViewById(R.id.turnOff);
        text = (TextView) findViewById(R.id.statusText);
        discoverButton = (Button) findViewById(R.id.discoverButton);
        enterCode = (Button) findViewById(R.id.enterCode);
        advertiseButton = (Button) findViewById(R.id.advertiseButton);
        final BluetoothManager BTManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        myBlueToothAdapter = BTManager.getAdapter();

        registerReceiver(locationServiceStateReceiver, new IntentFilter((LocationManager.MODE_CHANGED_ACTION)));
        myBlueToothAdapter = myBlueToothAdapter.getDefaultAdapter();
        myBlueLeScanner = myBlueToothAdapter.getDefaultAdapter().getBluetoothLeScanner();

        checkPermissions();
        checkLocationServices();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(generateUUID, 0, 5, TimeUnit.MINUTES);

        if(!myBlueToothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(myBlueToothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }

        if(myBlueToothAdapter == null){
            OnButton.setEnabled(false);
            OffButton.setEnabled(false);
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(), "Your Deivce does not support Bluetooth", Toast.LENGTH_LONG).show();
        } else {
            text = (TextView) findViewById(R.id.statusText);
            OnButton = (Button) findViewById(R.id.turnOn);
            OnButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    on(v);
                }
            });

            OffButton = (Button) findViewById(R.id.turnOff);
            OffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    off(v);
                }
            });
        }

        if (!myBlueToothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ){
            Toast.makeText(this, "Multiple advertisement isn't supported", Toast.LENGTH_SHORT).show();
            advertiseButton.setEnabled(false);
            discoverButton.setEnabled(false);
        }


        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(status.this, Statistics.class);
                startActivity(i);
            }
        });

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myBlueToothAdapter.isDiscovering()) {

                    Toast.makeText(getApplicationContext(), "Making device discoverable", Toast.LENGTH_LONG);

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
                } else {
                    myBlueToothAdapter.startDiscovery();
                    registerReceiver(scanBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                    UUID userUUID = UUID.randomUUID();

                }
            }
        });

        enterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i4 = new Intent(status.this, EnterCode.class);
                startActivity(i4);
            }
        });

        advertiseButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               Toast.makeText(getApplicationContext(), "Advertising For Devices", Toast.LENGTH_LONG);
               advertise();
           }
        });
    }

    private final BroadcastReceiver scanBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(!BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
                String deviceRSSI = (intent.getExtras().get(BluetoothDevice.EXTRA_RSSI).toString());
                BluetoothArray.add(device.getUuids() + "\n" + deviceRSSI);
            }
        }
    };

    private final BroadcastReceiver locationServiceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(LocationManager.MODE_CHANGED_ACTION)) {
                boolean isEnabled = areLocationServicesEnabled();
                Log.e("Locationservice is now:", isEnabled ? "on" : "off");
                checkPermissions();
            }
        }
    };

    public void on(View v){
        if(!myBlueToothAdapter.isEnabled()){
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(), "Bluetooth Turned On", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
        }
    }


    public void off(View v){
        myBlueToothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(), "Bluetooth turned off", Toast.LENGTH_LONG).show();
    }

    private void scanBluetooth() {
        if(myBlueLeScanner != null) {
            if (!scanning) {

                newHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanning = false;
                        myBlueLeScanner.stopScan(newScanCallBack);
                    }
                }, SCAN_PERIOD);

                scanning = true;
                myBlueLeScanner.startScan(newScanCallBack);
            } else {
                scanning = false;
                myBlueLeScanner.stopScan(newScanCallBack);
            }
        }
    }

    private void advertise(){
        BluetoothLeAdvertiser advertiser = myBlueToothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build();

        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(getString(R.string.uniqueID)));

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceUuid( pUuid )
                .addServiceData( pUuid, "Data".getBytes(Charset.forName("UTF-8") ) )
                .build();


        AdvertiseCallback advertisingCallBack = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode){
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }

        };

        advertiser.startAdvertising(settings, data, advertisingCallBack);
    }

    Runnable generateUUID = new Runnable() {
        @Override
        public void run() {
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (myBlueToothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] missingPermissions = getMissingPermissions(getRequiredPermissions());
            if (missingPermissions.length > 0) {
                requestPermissions(missingPermissions, ACCESS_LOCATION_REQUEST);
            } else {
                permissionsGranted();
            }
        }
    }

    private String[] getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String requiredPermission : requiredPermissions) {
                if (getApplicationContext().checkSelfPermission(requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(requiredPermission);
                }
            }
        }
        return missingPermissions.toArray(new String[0]);
    }


    private String[] getRequiredPermissions() {
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetSdkVersion >= Build.VERSION_CODES.Q)
            return new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        else return new String[] {Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    private void permissionsGranted() {

        if (checkLocationServices()) {
            initBluetoothHandler();
        }
    }

    private void initBluetoothHandler() {
    }

    private boolean areLocationServicesEnabled() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {

            return false;
        }

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGpsEnabled || isNetworkEnabled;
    }

    private boolean checkLocationServices() {
        if (!areLocationServicesEnabled()) {
            new AlertDialog.Builder(status.this)
                    .setTitle("Location services are not enabled")
                    .setMessage("Scanning for Bluetooth peripherals requires locations services to be enabled.") // Want to enable?
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
            return false;
        } else {
            return true;
        }
    }


}