package com.ljz.myproject;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by Welive on 2018/11/26.
 * 自定义全局的Application
 * 主要处理一些全局数据的初始化
 */

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Context getInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initLogger();
    }

    private void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}

