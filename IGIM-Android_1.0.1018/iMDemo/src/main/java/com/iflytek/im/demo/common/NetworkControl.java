package com.iflytek.im.demo.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkControl {
	private static Context mContext;
    private static NetworkControl instance;
    private NetworkControl(Context context) {
        this.mContext = context;
    }

    public static void createInstance(Context context) {
        instance = new NetworkControl(context);
        mContext = context;
    }

    public static NetworkControl getInstance() {
        return instance;
    }

    /**
     * 判断WIFI是否打开
     *
     * @return
     */

    public boolean isWifiEnabled() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((cm.getActiveNetworkInfo() != null && cm
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断是否是3G网络
     *
     * @return
     */
    public boolean is3rd() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是wifi还是3g网络
     *
     * @return
     */
    public boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        try {
            info = cm.getActiveNetworkInfo();
            return info != null && (info.isAvailable() && info.isConnected());
        } catch (Exception e) {
            return false;
        }
    }
	
	

}
