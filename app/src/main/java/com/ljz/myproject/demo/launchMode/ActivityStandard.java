package com.ljz.myproject.demo.launchMode;
import android.content.Intent;
import android.os.Bundle;
import com.ljz.myproject.R;
import com.ljz.myproject.common.BaseActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Welive on 2018/11/26.
 * standard模式的activity
 */

public class ActivityStandard extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_launch)
    public void onViewClicked() {
        Intent intent = new Intent(ActivityStandard.this, ActivityStandard.class);
        startActivity(intent);
    }
}
