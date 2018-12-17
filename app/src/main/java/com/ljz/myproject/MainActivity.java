package com.ljz.myproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ljz.myproject.common.BaseActivity;
import com.ljz.myproject.demo.bluetooth.BluetoothActivity;
import com.ljz.myproject.demo.contentProvider.ContactActivity;
import com.ljz.myproject.demo.customView.CustomViewActivity;
import com.ljz.myproject.demo.launchMode.ActivitySingleTask;
import com.ljz.myproject.demo.launchMode.ActivitySingleTop;
import com.ljz.myproject.demo.launchMode.ActivityStandard;
import com.ljz.myproject.demo.broadcastReceive.MyBroadcast;
import com.ljz.myproject.demo.service.ServiceActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder= ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_jump_to_standard_page, R.id.tv_jump_to_single_top_page, R.id.tv_jump_single_task_page, R.id.tv_jump_to_single_instance_page,
             R.id.tv_jump_to_blue_tooth_chat,R.id.tv_jump_to_service_page,R.id.tv_jump_to_broadcast_receive,R.id.tv_jump_to_content_provider,
             R.id.tv_custom_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_jump_to_standard_page:
                Intent standard = new Intent(MainActivity.this, ActivityStandard.class);
                startActivity(standard);
                break;
            case R.id.tv_jump_to_single_top_page:
                Intent singleTop = new Intent(MainActivity.this, ActivitySingleTop.class);
                startActivity(singleTop);
                break;
            case R.id.tv_jump_single_task_page:
                Intent singleTask = new Intent(MainActivity.this, ActivitySingleTask.class);
                startActivity(singleTask);
                break;
            case R.id.tv_jump_to_single_instance_page:
                Intent singleInstance = new Intent();
                singleInstance.setAction("com.ljz.myproject.launchmode.singleinstance");
                startActivity(singleInstance);
                break;
            case R.id.tv_jump_to_blue_tooth_chat:
                Intent bluetooth = new Intent(MainActivity.this,BluetoothActivity.class);
                startActivity(bluetooth);
                break;
            case R.id.tv_jump_to_service_page:
                Intent service = new Intent(MainActivity.this, ServiceActivity.class);
                startActivity(service);
                break;
            case R.id.tv_jump_to_broadcast_receive:
                Intent broadcast = new Intent(MainActivity.this, MyBroadcast.class);
                startActivity(broadcast);
                break;
            case R.id.tv_jump_to_content_provider:
                Intent contact = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contact);
                break;
            case R.id.tv_custom_view:
                Intent custom = new Intent(MainActivity.this, CustomViewActivity.class);
                startActivity(custom);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
