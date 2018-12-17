package com.ljz.myproject.demo.launchMode;

import android.content.Intent;
import android.os.Bundle;

import com.ljz.myproject.R;
import com.ljz.myproject.common.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Welive on 2018/11/26.
 * 测试singleTop辅助页
 */

public class OtherTopActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_top);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_jump_to_single_top_page)
    public void onViewClicked() {
        Intent intent = new Intent(OtherTopActivity.this, ActivitySingleTop.class);
        startActivity(intent);
    }
}
