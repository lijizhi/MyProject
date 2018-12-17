package com.ljz.myproject.demo.service;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;

/**
 * Created by Welive on 2018/11/28.
 * 自定义一个服务
 */

public class MyService extends Service{

    private MyBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("onCreate执行");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.d("onStart执行");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("onStartCommand执行");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("onBind执行");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d("onUnbind执行");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Logger.d("onDestroy执行");
        super.onDestroy();
    }

    class MyBinder extends Binder {
        public void startDownload() {
            Logger.d("开始下载");
            // 执行具体的下载任务
        }

    }

}
