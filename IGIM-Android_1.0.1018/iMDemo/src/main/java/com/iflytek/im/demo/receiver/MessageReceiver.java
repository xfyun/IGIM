package com.iflytek.im.demo.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.common.NotificationUtil;

import java.util.List;

public class MessageReceiver extends BroadcastReceiver {
    public static final String TAG = "MessageReceiver";

    private OnMessageArrivedListener mListener;
    private long lastMsgArrivedTime = System.currentTimeMillis();

    public MessageReceiver() {
    }

    public MessageReceiver(OnMessageArrivedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e(TAG, "MainActivity广播接收");
//        if ((System.currentTimeMillis() - lastMsgArrivedTime) / 1000 > 2) {
//            Bundle bundle = intent.getExtras();
//            Object obj = bundle.getSerializable(IMConstants.Parameter.KEY_MSG);
//            if (obj != null) {
//                CommonMsgContent msg = (CommonMsgContent) obj;
//                ImApplication app = ImApplication.getApp();
//
//                if (isBackground()) {
//                    // 后台
//                    NotificationUtil.notifyPerson(msg,false);
//                } else {
//                    // 前台
//                    if (app.getCurrentState() == ImApplication.CurrentState.PERSONAL_TALK) {
//                        String sender = msg.getSender();
//                        if (!sender.equals(app.getPersonalTalkName())) {
//                            NotificationUtil.notifyPerson(msg,true);
//                        }
//                    } else if (app.getCurrentState() == ImApplication.CurrentState.GROUP_TALK) {
//                        String receiver = msg.getReceiver();
//                        if (!receiver.equals(app.getGroupTalkId())) {
//                            NotificationUtil.notifyPerson(msg,true);
//                        }
//                    } else {
//                        NotificationUtil.notifyPerson(msg,false);
//                    }
//                }
//                if (mListener != null) {
//                    mListener.onMessageOnMessageArrived();
//                }
//            }
//        }
    }

    public static boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) ImApplication.getApp().getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(ImApplication.getApp().getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public interface OnMessageArrivedListener {
        void onMessageOnMessageArrived();
    }

}
