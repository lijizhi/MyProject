package com.ljz.myproject.demo.launchMode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ljz.myproject.R;
import com.ljz.myproject.common.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Welive on 2018/11/26.
 * SingleTop（栈顶复用模式）
 */

public class ActivitySingleTop extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletop);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_jump_to_self, R.id.tv_jump_to_other_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_jump_to_self:
                Intent intent_one = new Intent(ActivitySingleTop.this, ActivitySingleTop.class);
                startActivity(intent_one);
                break;
            case R.id.tv_jump_to_other_page:
                Intent intent_two = new Intent(ActivitySingleTop.this, OtherTopActivity.class);
                startActivity(intent_two);
                break;
        }
    }
}
