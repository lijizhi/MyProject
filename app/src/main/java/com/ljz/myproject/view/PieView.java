package com.ljz.myproject.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ljz.myproject.entity.PieData;

import java.util.ArrayList;

/**
 * Created by Welive on 2018/12/10.
 * 饼状图
 */

public class PieView extends View{

    //定义饼状图各个扇形的颜色
    private int[] mColors={0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080, 0xFFE6B800, 0xFF7CFC00};

    //饼状图初始绘制角度
    private float mStartAngle = 0;

    //数据
    private ArrayList<PieData> mData;

    //宽高
    private int mWidth, mHeight;

    //画笔
    private Paint mPaint=new Paint();

    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        mHeight=h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mData) {
            return;
        }

        //当前起始角度
        float currentStartAngle = mStartAngle;

        //将画布坐标原点移动到中心位置
        canvas.translate(mWidth / 2, mHeight / 2);

        //饼状图半径
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);

        //饼状图绘制区域
        @SuppressLint("DrawAllocation")
        RectF rect = new RectF(-r, -r, r, r);

        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);
            mPaint.setColor(pie.getColor());
            canvas.drawArc(rect, currentStartAngle, pie.getAngle(), true, mPaint);
            currentStartAngle += pie.getAngle();
        }
    }

    // 设置起始角度
    public void setStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
        invalidate();
    }

    // 设置数据
    public void setData(ArrayList<PieData> mData) {
        this.mData = mData;
        initData(mData);
        invalidate();
    }

    // 初始化数据
    private void initData(ArrayList<PieData> mData) {
        if (null == mData || mData.size() == 0) {
            return;
        }
        float sumValue = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);
            //计算数值和
            sumValue += pie.getValue();
            //设置颜色
            int j = i % mColors.length;
            pie.setColor(mColors[j]);
        }

        float sumAngle = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);
            //百分比
            float percentage = pie.getValue() / sumValue;
            //对应的角度
            float angle = percentage * 360;
            //记录百分比
            pie.setPercentage(percentage);
            //记录角度大小
            pie.setAngle(angle);
            sumAngle += angle;
            Log.i("angle", "" + pie.getAngle());
        }
    }
}
