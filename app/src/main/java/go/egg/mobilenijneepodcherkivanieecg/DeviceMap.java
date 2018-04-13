package go.egg.mobilenijneepodcherkivanieecg;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Василий on 14.04.2018.
 */

public class DeviceMap {
    private static DeviceMap sDeviceMap;
    private Map<String,BluetoothDevice> mBluetoothDeviceMap;
    private DeviceMap(){
        mBluetoothDeviceMap = new LinkedHashMap<>();
    }
    public static DeviceMap get(){
        if(sDeviceMap==null){
            sDeviceMap=new DeviceMap();
        }
        return sDeviceMap;
    }
    public void add(ScanResult scanResult){
        BluetoothDevice bluetoothDevice = scanResult.getDevice();
        String adres = bluetoothDevice.getAddress();
        mBluetoothDeviceMap.put(adres,bluetoothDevice);
    }
    public Map<String,BluetoothDevice> get_map(){
        return mBluetoothDeviceMap;
    }
}
