package com.ljz.myproject.demo.broadcastReceive;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import com.ljz.myproject.R;
import com.ljz.myproject.common.BaseActivity;
import com.ljz.myproject.utils.WifiUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Welive on 2018/11/30.
 * 测试发送广播
 */

public class MyBroadcast extends BaseActivity {

    private MyReceive myReceive;
    private WifiUtil wifiUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_change_broadcast);
        ButterKnife.bind(this);
        wifiUtil=new WifiUtil(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在onResume()注册、onPause()注销是因为onPause()在App死亡前一定会被执行，从而保证广播在App死亡前一定会被注销，从而防止内存泄露。
        unregisterReceiver(myReceive);
    }

    private void registerBroadcast() {
        //实例化BroadcastReceiver子类
        myReceive = new MyReceive();
        //实例化IntentFilter
        IntentFilter intentFilter = new IntentFilter();
        //监听网络状态变化
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(myReceive, intentFilter);
    }

    @OnClick({R.id.tv_wifi_open, R.id.tv_wifi_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_wifi_open:
                if(!wifiUtil.isOpenWifi()){
                    wifiUtil.openWifi();
                }
                break;
            case R.id.tv_wifi_close:
                if(wifiUtil.isOpenWifi()){
                    wifiUtil.closeWifi();
                }
                break;
            default:
                break;
        }
    }
}
