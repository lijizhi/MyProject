package com.ljz.myproject.demo.bluetooth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ljz.myproject.R;
import com.ljz.myproject.adapter.BlueToothAdapter;
import com.ljz.myproject.common.BaseActivity;
import com.ljz.myproject.entity.BlueTooth;
import com.ljz.myproject.demo.broadcastReceive.BlueToothReceiver;
import com.ljz.myproject.utils.BluetoothUtil;
import com.ljz.myproject.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Welive on 2018/11/27.
 * 蓝牙功能练手
 */

public class BluetoothActivity extends BaseActivity implements
        CompoundButton.OnCheckedChangeListener,
        BlueToothInterface,
        BlueToothAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.bluetooth_switch)
    Switch bluetoothSwitch;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private BlueToothAdapter recyclerAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BlueTooth> list = new ArrayList<>();

    public static final int BLUE_TOOTH_DIALOG = 0x111;
    public static final int BLUE_TOOTH_TOAST = 0x123;
    public static final int BLUE_TOOTH_WRAITE = 0X222;
    public static final int BLUE_TOOTH_READ = 0X333;
    public static final int BLUE_TOOTH_SUCCESS = 0x444;

    private BluetoothUtil mBluetoothUtil;
    private BlueToothReceiver mReceiver;

    private Timer timer;
    private WifiTask task;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);

        bluetoothSwitch.setOnCheckedChangeListener(this);

        //初始化刷新
        initRefresh();

        //初始化Recyclerview
        initRecyclerView();

        //初始化蓝牙
        initBluetooth();
    }

    private void initRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_yellow,R.color.color_light_black,R.color.color_pink);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRecyclerView() {
        recyclerAdapter = new BlueToothAdapter(this);
        recyclerAdapter.setWifiData(list);
        recyclerAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initBluetooth() {
        //获取本地蓝牙实例
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断蓝牙是否开启来设置状态
        if (mBluetoothAdapter.isEnabled()) {
            //已经开启
            bluetoothSwitch.setChecked(true);
            bluetoothSwitch.setText("蓝牙已开启");
        } else {
            bluetoothSwitch.setChecked(false);
            bluetoothSwitch.setText("蓝牙已关闭");
        }

        mReceiver = new BlueToothReceiver(this);

        //注册扫描设备广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter.isEnabled()) {
            Logger.d("onResume: resumeStart");
            mBluetoothUtil = BluetoothUtil.getInstance(handler);
            mBluetoothUtil.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                //打开蓝牙
                mBluetoothAdapter.enable();
                bluetoothSwitch.setText(R.string.open_bluetooth_ing);
                ToastUtil.showText(this, "正在开启蓝牙");
                Logger.d("执行");
            }
        } else {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_OFF) {
                //打开蓝牙
                mBluetoothAdapter.disable();
                bluetoothSwitch.setText(R.string.close_wifi_ing);
                ToastUtil.showText(this, "正在关闭蓝牙");
                Logger.d("执行");
            }
        }

        bluetoothSwitch.setClickable(false);

        if (timer == null || task == null) {
            timer = new Timer();
            task = new WifiTask();

            task.setChecked(isChecked);
            timer.schedule(task, 0, 1000);
            Logger.d("执行");
        }
    }

    @Override
    public void getBluetoothDevices(BluetoothDevice device, int rssi) {

    }

    @Override
    public void searchFinish() {
        swipeRefreshLayout.setRefreshing(false);
        ToastUtil.showText(BluetoothActivity.this, "扫描完成");
    }

    @Override
    public void onItemClick(int position) {
        showProgressDialog("正在进行连接");
        BlueTooth blueTooth = list.get(position);
        connectDevice(blueTooth.getMac());
    }

    private void close() {
        if (timer != null)
            timer.cancel();
        //取消扫描
        mBluetoothAdapter.cancelDiscovery();
        swipeRefreshLayout.setRefreshing(false);
        //注销广播
        unregisterReceiver(mReceiver);
    }

    private void connectDevice(String mac) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);

        if (null != mBluetoothUtil)
            mBluetoothUtil.connectDevice(device);
    }

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRefresh() {
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            list.clear();

            //扫描的是已配对的
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                list.add(new BlueTooth("已配对的设备", BlueTooth.TAG_TOAST));

                for (BluetoothDevice device : pairedDevices) {
                    Logger.d(device.getName() + "\n" + device.getAddress());
                    list.add(new BlueTooth(device.getName(), device.getAddress(), ""));
                }

                list.add(new BlueTooth("已扫描的设备", BlueTooth.TAG_TOAST));
            } else {
                ToastUtil.showText(getApplicationContext(), "没有找到已匹对的设备！");
                list.add(new BlueTooth("已扫描的设备", BlueTooth.TAG_TOAST));
            }

            recyclerAdapter.notifyDataSetChanged();

            //开始扫描设备
            mBluetoothAdapter.startDiscovery();
            ToastUtil.showText(BluetoothActivity.this, "开始扫描设备");
        } else {
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.showText(BluetoothActivity.this, "请开启蓝牙");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBluetoothUtil) {
            mBluetoothUtil.stop();
        }
        close();
    }

    private class WifiTask extends TimerTask {
        private boolean isChecked;

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        @Override
        public void run() {
            if (isChecked) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
                    handler.sendEmptyMessage(BluetoothAdapter.STATE_ON);
            } else {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
                    handler.sendEmptyMessage(BluetoothAdapter.STATE_OFF);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothAdapter.STATE_ON:
                    bluetoothSwitch.setClickable(true);
                    break;
                case BluetoothAdapter.STATE_OFF: {
                    if (msg.what == BluetoothAdapter.STATE_ON) {
                        bluetoothSwitch.setText("蓝牙已开启");
                        //自动刷新
                        swipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(true);
                                onRefresh();
                            }
                        }, 1000);

                        //开启socket监听
                        mBluetoothUtil = BluetoothUtil.getInstance(handler);
                        mBluetoothUtil.start();
                    } else if (msg.what == BluetoothAdapter.STATE_OFF) {
                        bluetoothSwitch.setText("蓝牙已关闭");
                        recyclerAdapter.setWifiData(null);
                        recyclerAdapter.notifyDataSetChanged();
                        mBluetoothUtil.stop();
                    }

                    timer.cancel();
                    timer = null;
                    task = null;
                    bluetoothSwitch.setClickable(true);
                }
                break;
                case BLUE_TOOTH_DIALOG: {
                    showProgressDialog((String) msg.obj);
                }
                break;
                case BLUE_TOOTH_TOAST: {
                    dismissProgressDialog();
                    ToastUtil.showText(BluetoothActivity.this, (String) msg.obj);
                }
                break;
                case BLUE_TOOTH_SUCCESS: {
                    dismissProgressDialog();
                    ToastUtil.showText(BluetoothActivity.this, "连接设备" +  msg.obj + "成功");
                    Intent intent = new Intent(BluetoothActivity.this, ChatActivity.class);
                    intent.putExtra(ChatActivity.DEVICE_NAME_INTENT, (String) msg.obj);
                    startActivity(intent);
                    //关闭其他资源
                    close();

                }
                break;
            }
        }
    };
}
