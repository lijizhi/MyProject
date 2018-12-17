package com.ljz.myproject.demo.launchMode;

import android.os.Bundle;

import com.ljz.myproject.R;
import com.ljz.myproject.common.BaseActivity;

/**
 * Created by Welive on 2018/11/26.
 * singleInstance（单任务栈启动模式）
 */

public class ActivitySingleInstance extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleinstance);
    }
}
