package go.egg.mobilenijneepodcherkivanieecg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseDeviceActivity extends AppCompatActivity {

    private final int SCAN_TIME =1000;
    private final int ENABLE_BLUETOOTH = 1;
    private final int ENABLE_PERMISSION = 2;


    private RecyclerView mRecyclerView;
    private DeviceMap mDeviceMap;
    private DeviceAdapter mDeviceAdapter;


    private Button start_scan;
    private Button stop_scan;



    private Button temp_b;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    private BleScanCallback mBleScanCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mGatt;
    private Map<String,BluetoothDevice> mBluetoothDeviceMap;//??
    private boolean scan_lable;
    private boolean connected;
    private Handler mHandler;
    private Handler mHandler2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        mRecyclerView = findViewById(R.id.recycler_view_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        start_scan = findViewById(R.id.start_scan_button);
        stop_scan = findViewById(R.id.stop_scan_button);
        start_scan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                StartScan();
                //
            }
        });
        stop_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopScan();
                //
            }
        });

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        UpdateDeviceList();




        temp_b = findViewById(R.id.temp_b);
        temp_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(connected);
            }
        });

    }


    //BLE
    private class BleScanCallback extends ScanCallback{
        public BleScanCallback(int i){}

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
           // super.onScanResult(callbackType, result);
            add(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
           // super.onBatchScanResults(results);
            for(ScanResult scanResult:results){
                add(scanResult);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
        public void add(ScanResult scanResult){
            mDeviceMap.add(scanResult);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void StartScan(){
        if(!hasPermissions()||scan_lable){
            return;
        }
        //filter ggat server?1
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanSettings scanSettings = new ScanSettings
                .Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        mBleScanCallback = new BleScanCallback(1);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(scanFilters,scanSettings,mBleScanCallback);
        scan_lable=true;

        //scansetting






    }
    private void StopScan(){
        if(scan_lable&&mBluetoothAdapter!=null&&mBluetoothAdapter.isEnabled()&&mBluetoothLeScanner!=null){
            mBluetoothLeScanner.stopScan(mBleScanCallback);
        }
        scan_lable= false;


        mBleScanCallback=null;
        mBluetoothLeScanner=null;
        mHandler=null;

        UpdateDeviceList();

    }





    //permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermissions() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            requestBluetoothEnable();
            return false;
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
            return false;
        }
        return true;
    }
    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
        //Log.d(TAG, "Requested user enables Bluetooth. Try starting the scan again.");
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ENABLE_PERMISSION);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void connect_device(BluetoothDevice bluetoothDevice){

        GgatClientCallBack ggatClientCallBack = new GgatClientCallBack();
        mGatt = mBluetoothDevice.connectGatt(this,false,ggatClientCallBack, 2);

    }

    private class GgatClientCallBack extends BluetoothGattCallback{
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println("QWekqwejqwhejqwhejqwhejqwhjehjqwhej");


          //  super.onConnectionStateChange(gatt, status, newState);

            if(status==BluetoothGatt.GATT_FAILURE){
                System.out.println("WHAT1");
                System.out.println(status);
                disconnectGgatServer();
                return;
            }else if(status!=BluetoothGatt.GATT_SUCCESS){
                System.out.println("WHAT2");
                System.out.println(status);

                disconnectGgatServer();
                return;
            }
            if(newState== BluetoothProfile.STATE_CONNECTED){
                connected=true;

                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                gatt.discoverServices();
            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                System.out.println("WHAT3");

                disconnectGgatServer();
            }

        }
    }
    private void disconnectGgatServer(){
        if(mGatt!=null){
            mGatt.disconnect();
            mGatt.close();
        }
        System.out.println("No");
        mHandler2=null;
        connected=false;
    }




    //recycler
    private void UpdateDeviceList(){
        if(mDeviceMap==null)
            mDeviceMap = DeviceMap.get();//
        if(mDeviceAdapter==null){
            mDeviceAdapter= new DeviceAdapter();
            mRecyclerView.setAdapter(mDeviceAdapter);
        }
        mDeviceAdapter.notifyDataSetChanged();
        //
    }
    private class DeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        String mString;


        public DeviceHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView= itemView.findViewById(R.id.item_list_text_name);
        }
        public void setter(String string){
            mString = string;
            textView.setText(mString);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            BluetoothDevice bluetoothDevice = mDeviceMap.get_map().get(mString);
            mBluetoothDevice = bluetoothDevice;
            connect_device(mBluetoothDevice);
        }
    }
    private class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder>{

        @Override
        public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.item_list,parent,false);
            return new DeviceHolder(view);
        }

        @Override
        public void onBindViewHolder(DeviceHolder holder, int position) {
            holder.setter((String) mDeviceMap.get_map().keySet().toArray()[position]);

        }

        @Override
        public int getItemCount() {
            return mDeviceMap.get_map().size();
        }
    }
}
