package com.ljz.myproject.demo.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface BlueToothInterface {
    void getBluetoothDevices(BluetoothDevice device, int rssi);

    void searchFinish();
}
