package com.iflytek.im.demo.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/28.
 */

public class ProcessUtil {
    public static boolean isBackground(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return !cn.getPackageName().equals(packageName);
    }
}
