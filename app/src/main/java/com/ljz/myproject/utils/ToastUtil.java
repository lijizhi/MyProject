package com.ljz.myproject.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Welive on 2018/11/27.
 * 吐司工具类
 */

public class ToastUtil {

    private static Toast toast;

    public static void showText(Context context, String s) {
        if (toast == null)
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        else {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(s);
        }
        toast.show();
    }
}

