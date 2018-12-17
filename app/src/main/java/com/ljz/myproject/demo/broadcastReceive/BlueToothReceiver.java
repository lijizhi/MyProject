package com.ljz.myproject.demo.broadcastReceive;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ljz.myproject.demo.bluetooth.BlueToothInterface;
import com.orhanobut.logger.Logger;

/**
 * Created by Welive on 2018/11/27.
 * 广播接收者
 */

public class BlueToothReceiver extends BroadcastReceiver {

    private BlueToothInterface mBlueToothInterface;

    public BlueToothReceiver(BlueToothInterface mBlueToothInterface) {
        this.mBlueToothInterface = mBlueToothInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //当扫描到设备的时候
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // 获取设备对象
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //提取强度信息
            if(null!=intent.getExtras()) {
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                Logger.d(device.getName() + "\n" + device.getAddress() + "\n强度：" + rssi);
                if (mBlueToothInterface != null) {
                    mBlueToothInterface.getBluetoothDevices(device, rssi);
                }
            }
        }

        //搜索完成
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if (mBlueToothInterface != null) {
                mBlueToothInterface.searchFinish();
            }
            Logger.d("onReceive: 搜索完成");
        }
    }
}