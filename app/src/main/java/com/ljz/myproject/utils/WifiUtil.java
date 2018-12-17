package com.ljz.myproject.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

import static com.ljz.myproject.utils.WifiUtil.TYPE_PASSWORD.TYPE_PASSWORD_NONE;
import static com.ljz.myproject.utils.WifiUtil.TYPE_PASSWORD.TYPE_PASSWORD_WPA;

/**
 * Created by Welive on 2018/11/30.
 * 开启或关闭wifi工具类
 */

public class WifiUtil {
    private final WifiManager mWifiManager;

    public WifiUtil(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    //判断WiFi是否打开
    public boolean isOpenWifi() {
        return mWifiManager.isWifiEnabled();
    }

    //开启WiFi
    public boolean openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            boolean b = mWifiManager.setWifiEnabled(true);
            return b;
        }
        return true;
    }

    //关闭WiFi
    public boolean closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            boolean b = mWifiManager.setWifiEnabled(false);
            return b;
        }
        return true;
    }

    //搜索附近WiFi
    public void searchWifi() {
        mWifiManager.startScan();
    }

    //搜索附近WiFi的结果
    public List<ScanResult> getSearchWifiData() {
        return mWifiManager.getScanResults();
    }

    //根据信号轻度划分等级level信号强度，i等级，自己决定
    public int getImagelevel(int level, int i) {
        return WifiManager.calculateSignalLevel(level, i);
    }

    //判断wifi是否有加密
    public static boolean checkAuth(ScanResult scanResult) {
        boolean needPassword = false;
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA") || capabilities.contains("WEP")) {
            needPassword = true;
        }
        return needPassword;
    }

    //判断WiFi是否连接过保存过密码
    public boolean connectSavedWifi(String ssid) {
        boolean saved = false;
        //去获得ssid对应的所保存的这个wifi的信息呢(wifi的networkId)
        WifiConfiguration wifiConfiguration = getNetWorkConfig(ssid);
        if (wifiConfiguration != null) {
            //以前保存过的
            saved = true;
            //还得去连接这个wifi了
            mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
        }
        return saved;
    }

    //根据这个ssid,来获得以前保存过的连接过的wifi的信息
    private WifiConfiguration getNetWorkConfig(String ssid) {
        WifiConfiguration wifiConfiguration = null;
        //1 先把以前所有连接过的wifi统统找出来
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        //考虑一下保存的格式,在参数的左右两边分别加两个双引号
        ssid = "\"" + ssid + "\"";
        //2 遍历,如果这个wifi的ssid和参数传进来的ssid相等,说明以前还真连接过
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            String configSSID = configuredNetwork.SSID;
            if (ssid.equals(configSSID)) {
                wifiConfiguration = configuredNetwork;
            }
        }
        return wifiConfiguration;
    }

    public enum TYPE_PASSWORD {TYPE_PASSWORD_NONE, TYPE_PASSWORD_WPA}

    //连接WiFi
    public boolean connetWifi(String id, String pwd, TYPE_PASSWORD typePassword) {
        WifiConfiguration wifiConfiguration = getWifiConfiguration(id, pwd, typePassword);
        int netWorkId = mWifiManager.addNetwork(wifiConfiguration);
        return mWifiManager.enableNetwork(netWorkId, true);
    }

    //关闭WiFi
    public String disconnetWifi() {
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
        int networkId = connectionInfo.getNetworkId();
        if (networkId == -1) {
            return "当前没有连接WiFi";
        } else {
            boolean b = mWifiManager.disableNetwork(networkId);
            return b ? "关闭连接成功" : "关闭连接失败";
        }
    }

    private WifiConfiguration getWifiConfiguration(String id, String pwd, TYPE_PASSWORD typePassword) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = id;
        if (typePassword == TYPE_PASSWORD_NONE) {
            //配置成无需加密的wifi
            //设置密码为空，没有加密
            wifiConfiguration.wepKeys[0] = "\"\"";
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.wepTxKeyIndex = 0;
        } else if (typePassword == TYPE_PASSWORD_WPA) {
            //配置成WPA加密的wifi
            //Log.e("m520iii",id+"///"+pwd);
            wifiConfiguration.preSharedKey = "\"" + pwd + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
        }
        return wifiConfiguration;
    }
}
