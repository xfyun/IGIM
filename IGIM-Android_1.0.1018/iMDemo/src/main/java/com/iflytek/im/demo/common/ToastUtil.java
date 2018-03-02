package com.iflytek.im.demo.common;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.iflytek.im.demo.ImApplication;

/**
 * Created by imxqd on 2016/8/29.
 * Toast显示类，这样显示可以保证同时只显示一个Toast
 */
public class ToastUtil {

    private static Toast toast = Toast.makeText(ImApplication.getApp(), "", Toast.LENGTH_SHORT);

    /**
     * Toast显示，这样显示可以保证同时只显示一个Toast
     * @param text 要显示的文本
     */
    public static void showText(String text) {
        toast.setText(text);
        toast.show();
    }

    /**
     * Toast显示，这样显示可以保证同时只显示一个Toast
     * @param string 要显示的文本的id
     */
    public static void showText(@StringRes int string) {
        toast.setText(string);
        toast.show();
    }

    /**
     * 立即取消显示
     */
    public static void cancel() {
        toast.cancel();
    }
}
