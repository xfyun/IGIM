package com.iflytek.im.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.im.demo.Constants;


/**
 * 网络状态监听
 * Created by imxqd on 2016/8/30.
 */

public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "NetWorkChange";


    public NetWorkChangeBroadcastReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info == null){
                return;
            }else if (NetworkInfo.State.CONNECTED == info.getState()) {
                RxBus.get().post(Constants.Event.ON_NETWORK_STATE_CHANGED, info.getState());
            }


        }

    }

}
