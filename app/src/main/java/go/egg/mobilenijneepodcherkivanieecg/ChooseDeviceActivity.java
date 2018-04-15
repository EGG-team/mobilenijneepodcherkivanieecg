package go.egg.mobilenijneepodcherkivanieecg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
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


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.bluetooth.BluetoothGattService.*;

public class ChooseDeviceActivity extends AppCompatActivity {

    private final int SCAN_TIME =1000;
    private final int ENABLE_BLUETOOTH = 1;
    private final int ENABLE_PERMISSION = 2;


    private RecyclerView mRecyclerView;
    private DeviceMap mDeviceMap;
    private DeviceAdapter mDeviceAdapter;


    private Button start_scan;
    private Button stop_scan;
    private boolean     mInitialized;

    private Button temp;



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

        temp =findViewById(R.id.temp_b);
        temp.setOnClickListener(v->send_msg());


    }


    private void send_msg(){
        if(!mInitialized&&!connected)
            return;

         String s = "HER";
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        BluetoothGattService service = mGatt.getService(UUID.fromString("0x1101"));
        BluetoothGattCharacteristic characteristic = service.getCharacteristics().get(0);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic.setValue(data);



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
            System.out.println(status);



            super.onConnectionStateChange(gatt, status, newState);

            if(status==BluetoothGatt.GATT_FAILURE){
                System.out.println(status);
                disconnectGgatServer();
                return;
            }else if(status!=BluetoothGatt.GATT_SUCCESS){
                System.out.println(status);

                disconnectGgatServer();
                return;
            }
            if(newState== BluetoothProfile.STATE_CONNECTED){
                connected=true;

                Toast.makeText(ChooseDeviceActivity.this,"Подключение успешно!",Toast.LENGTH_SHORT).show();
                gatt.discoverServices();
            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){

                disconnectGgatServer();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status!=BluetoothGatt.GATT_SUCCESS){
                //disconnectGgatServer();

                return;
            }



            String string_uuid1 = "6e400001‑b5a3‑f393‑e0a9‑e50e24dcca9e";
            String string_uuid2 = "0000ffe0‑0000‑1000‑8000‑00805f9b34fb";
            byte[] data2 = {6, 101, 4, 0, 0, 0, 0, 1, 98, 5, 97, 3, 102, 3, 9, 3, 101, 0, 97, 9, 101, 5, 0, 101, 2, 4, 100, 99, 99, 97, 9, 101};
            byte[] data3 = {0, 0, 0, 0, 102, 102, 101, 0, 0, 0, 0, 0, 1, 0, 0, 0, 8, 0, 0, 0, 0, 0, 8, 0, 5, 102, 9, 98, 3, 4, 102, 98};



            //UUID uuid1 = UUID.fromString(string_uuid1);
            UUID uuid1= UUID.nameUUIDFromBytes(data2);

//
            UUID uuid2 = UUID.nameUUIDFromBytes(data3);
            System.out.println(uuid1.toString()+"              "+uuid2.toString());

            for(BluetoothGattService service:gatt.getServices()){
                System.out.println(service.getUuid());
            }

            BluetoothGattService service = gatt.getService(uuid1);

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid2);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mInitialized = gatt.setCharacteristicNotification(characteristic, true);
            String s = "e04fd020ea3a6910a2d808002b30309d";
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i+1), 16));
            }
            characteristic.setValue(data);
            System.out.println("WTF");

        }



    }
    private void disconnectGgatServer(){
        if(mGatt!=null){
            mGatt.disconnect();
            mGatt.close();
        }
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
