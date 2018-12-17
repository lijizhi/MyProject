package com.ljz.myproject.utils;


import android.content.Context;

/**
 * Created by Welive on 2018/12/11.
 * dp与px相互转换工具类
 */

public class SizeUtil {

    public static int Dp2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
