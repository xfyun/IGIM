package im.iflytek.com.imrecorddemo;

import android.app.Application;

import com.iflytek.cloud.im.IMClient;

/**
 * Created by Administrator on 2017/8/29.
 */

public class IMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IMClient.createInstance(this);
    }
}
