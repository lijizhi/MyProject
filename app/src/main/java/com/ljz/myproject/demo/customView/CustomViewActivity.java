package com.ljz.myproject.demo.customView;
import android.os.Bundle;
import com.ljz.myproject.R;
import com.ljz.myproject.common.BaseActivity;
import com.ljz.myproject.view.PieView;
import com.ljz.myproject.entity.PieData;
import java.util.ArrayList;

/**
 * Created by Welive on 2018/12/10.
 * 自定义View的合集
 */
public class CustomViewActivity extends BaseActivity {

    private PieView pieView;
    private ArrayList<PieData> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);
        initView();
        initData();
    }

    private void initView() {
       pieView=findViewById(R.id.pie_view);
    }

    private void initData() {
        setPieData();
    }

    private void setPieData() {
        PieData one = new PieData("语文", 20);
        list.add(one);
        PieData two = new PieData("数学", 30);
        list.add(two);
        PieData three = new PieData("英语", 50);
        list.add(three);
        pieView.setData(list);
    }
}
