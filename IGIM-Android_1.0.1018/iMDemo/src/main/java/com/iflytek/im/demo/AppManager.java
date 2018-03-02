package com.iflytek.im.demo;


import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;


/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 *
 * @author HZC
 */
public class AppManager {

    private static Stack<AppCompatActivity> activityStack;

    private AppManager() {
    }


    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(AppCompatActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 去除指定 Activity
     *
     * @param activity
     */
    public static void removeActivity(AppCompatActivity activity) {

        if (activityStack != null && activityStack.size() > 0) {
            if (activityStack.contains(activity)) {
                activityStack.remove(activity);
            }
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishLastActivity() {
        AppCompatActivity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(AppCompatActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (AppCompatActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 释放栈顶指定说明的Activity
     *
     * @param topCount
     */
    public static void finishActivity(int topCount) {

        if (topCount > 0 && topCount <= activityStack.size()) {

            while (topCount > 0) {
                finishLastActivity();
                topCount--;
            }
        }

    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public static void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

}

