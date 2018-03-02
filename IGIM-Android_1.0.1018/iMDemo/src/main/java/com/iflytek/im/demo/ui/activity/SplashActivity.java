package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.User;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.Logging;
import com.iflytek.im.demo.common.NetworkControl;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.dao.SharePreferenceHelper;
import com.iflytek.im.demo.listener.ResultListener;
import com.iflytek.im.demo.processor.Processor;

import static com.iflytek.cloud.im.IMClientError.ERROR_ACCOUNT_ONLINE;
import static com.iflytek.im.demo.Constants.LoginErrorCode.KEY_LOGIN_ERRORCODE;
import static com.iflytek.im.demo.Constants.LoginErrorCode.LOGIN_INFO_ERROR;
import static com.iflytek.im.demo.Constants.LoginErrorCode.LOGIN_SOMENE_ONLINE;
import static com.iflytek.im.demo.Constants.LoginErrorCode.NETWORK_ERROR;
import static com.iflytek.im.demo.Constants.LoginErrorCode.OTHER_ERROR;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    private static final String EMPTY_UID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        login();
    }

    private void login() {
        final String uid = SharePreferenceHelper.getInstance().getString(Constants.Preference.KEY_UID);
        String password = SharePreferenceHelper.getInstance().getString(Constants.Preference.KEY_PASSWORD);
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(password)) {
            startLoginActivity(0);
            return;
        }
        login(uid);
    }

    private void login(String uid) {
        String token = Constants.Id.IM_TOKEN;
        if (TextUtils.isEmpty(token)) {
            startLoginActivity(OTHER_ERROR);
            return;
        }
        initIMClient(token);
        User user = new User();
        user.setName(uid);
        user.setUid(uid);
        IMClient.getInstance().login(user, false, token, new ResultCallback<String>() {
            @Override
            public void onSuccess(String data) {
                loginSuccess();
            }

            @Override
            public void onError(int errorCode) {
                loginError(errorCode);
            }
        });
    }

    private void initIMClient(String token) {
        Log.e(TAG,"start initIMClient");
        ImApplication.getApp().initIMClient(token);
    }


    private void loginSuccess() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    private void loginError(int errorCode) {
        Logging.d("SplashActivity", "login failed:" + errorCode);
        if (errorCode == ERROR_ACCOUNT_ONLINE) {
            startLoginActivity(LOGIN_SOMENE_ONLINE);
        } else {
//            ToastUtil.showText("登陆异常，请稍后再试：可能服务端出错");
            startLoginActivity(OTHER_ERROR);
        }
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.splash;
    }

    private void startLoginActivity(final int errorCode) {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.putExtra(KEY_LOGIN_ERRORCODE, errorCode);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}