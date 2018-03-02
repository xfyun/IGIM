package com.iflytek.im.demo;

import android.app.Application;
import android.util.Log;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.listener.MsgListener;
import com.iflytek.im.demo.common.NetworkControl;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.dao.Db;
import com.iflytek.im.demo.dao.SharePreferenceHelper;
import com.iflytek.sunflower.FlowerCollector;


public class ImApplication extends Application {

    private static final String TAG = "ImApplication";

    private static ImApplication mApp;


    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Db.init(this);
        NetworkControl.createInstance(this);
        SharePreferenceHelper.createInstance(this);
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(mApp);
        //添加网络判断
        if (!NetworkControl.getInstance().isNetworkConnected()) {
            ToastUtil.showText("当前网络不可用，请检查网络");
        }
    }


    public static ImApplication getApp() {
        return mApp;
    }

    public void initIMClient(String token) {
        IMClient.createInstance(this);
        IMClient.getInstance().setDebugAble(true);
        FlowerCollector.setDebugMode(false);
        IMClient.getInstance().regMsgListener(new MsgListener() {
            @Override
            public void onMsg(MessageContent msg) {
                Log.d(TAG, "notifyPerson:  新消息过来了");
                RxBus.get().post(Constants.Event.NEW_MESSAGE_IN, msg);
            }
        });

    }
}
