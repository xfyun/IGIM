package com.iflytek.im.demo.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.IMClientError;
import com.iflytek.cloud.im.core.util.FileUtil;
import com.iflytek.cloud.im.entity.User;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.NetworkControl;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.common.http.HttpException;
import com.iflytek.im.demo.common.http.HttpPacket;
import com.iflytek.im.demo.common.http.HttpUtil;
import com.iflytek.im.demo.common.http.RequestBuilder;
import com.iflytek.im.demo.dao.SharePreferenceHelper;
import com.iflytek.im.demo.listener.ResultListener;
import com.iflytek.im.demo.processor.Processor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.iflytek.cloud.im.IMClientError.ERROR_ACCOUNT_ONLINE;
import static com.iflytek.im.demo.Constants.Id.IM_APPID;
import static com.iflytek.im.demo.Constants.LoginErrorCode.KEY_LOGIN_ERRORCODE;
import static com.iflytek.im.demo.Constants.LoginErrorCode.LOGIN_INFO_ERROR;
import static com.iflytek.im.demo.Constants.LoginErrorCode.LOGIN_SOMENE_ONLINE;
import static com.iflytek.im.demo.Constants.LoginErrorCode.NETWORK_ERROR;
import static com.iflytek.im.demo.Constants.LoginErrorCode.OTHER_ERROR;
import static com.iflytek.im.demo.Constants.Preference.KEY_UID;
import static com.iflytek.im.demo.Constants.TokenParams.RET;
import static com.iflytek.im.demo.Constants.TokenParams.TOKEN;

public class LoginActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "LoginActivity";

    private Button mLoginBtn, mRegisterBtn;
    private View mMoreHideBottomView, mMoreLoginOptionView;
    private ImageView mMoreImgView, mLoginAnim;
    private boolean mIsShowBottom = false;
    private Intent mIntent;
    private String mUid, mPassword;
    private EditText mAppidEdt, mPasswordEdt;
    private CheckBox mIsRememberPassword;
    private AnimationDrawable mAnimationDrawable;

    private String mToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initView();
        handleFromSplashActivity();
    }

    private Handler changeViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //登录无用，动画开始
                    mLoginBtn.setClickable(false);
                    mAnimationDrawable.start();
                    mLoginAnim.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    //登录有用，动画结束
                    mLoginBtn.setClickable(true);
                    mAnimationDrawable.stop();
                    mLoginAnim.setVisibility(View.GONE);
                    break;
            }


        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.login;
    }

    private void initView() {
        mLoginBtn = (Button) findViewById(R.id.buton_login);
        mLoginBtn.setOnClickListener(this);
        mRegisterBtn = (Button) findViewById(R.id.button_regist);
        mRegisterBtn.setOnClickListener(this);

        mMoreHideBottomView = findViewById(R.id.morehidebottom);
        mMoreImgView = (ImageView) findViewById(R.id.more_image);
        mLoginAnim = (ImageView) findViewById(R.id.login_anim);

        mMoreLoginOptionView = findViewById(R.id.more);
        mMoreLoginOptionView.setOnClickListener(this);

        mAppidEdt = (EditText) findViewById(R.id.appid_edit);
        mPasswordEdt = (EditText) findViewById(R.id.password_edit);

        mIsRememberPassword = (CheckBox) findViewById(R.id.auto_save_password);

        if (!NetworkControl.getInstance().isNetworkConnected()) {
            fillInfo(getLoginInfo());
            Toast.makeText(LoginActivity.this, "当前网络不可用，请检查网络连接后重新登录", Toast.LENGTH_LONG).show();
            FileUtil.i(TAG, "当前网络不可用，请检查网络连接后重新登录");
        } else {
            fillInfo(getLoginInfo());
        }
        mAnimationDrawable = (AnimationDrawable) mLoginAnim.getBackground();
    }

    public void showBottom(boolean isShow) {
        if (isShow) {
            mMoreHideBottomView.setVisibility(View.GONE);
            mMoreImgView.setImageResource(R.drawable.login_more_up);
            mIsShowBottom = true;
        } else {
            mMoreHideBottomView.setVisibility(View.VISIBLE);
            mMoreImgView.setImageResource(R.drawable.login_more);
            mIsShowBottom = false;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more:
                showBottom(!mIsShowBottom);
                break;
            case R.id.buton_login:
                changeViewHandler.sendEmptyMessage(0);
                mUid = mAppidEdt.getText().toString();
                mPassword = mPasswordEdt.getText().toString();
                if (TextUtils.isEmpty(mUid) || TextUtils.isEmpty(mPassword)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_LONG).show();
                    changeViewHandler.sendEmptyMessage(1);
                    break;
                }
                if (!NetworkControl.getInstance().isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "当前网络不可用，请检查网络", Toast.LENGTH_LONG).show();
                    changeViewHandler.sendEmptyMessage(1);
                    return;
                }

                Processor.loginVerification(IM_APPID, mUid, mPassword, new ResultListener() {
                    public void onSuccess() {
                        loginVerifySuccess();
                    }
                    public void onFailed() {
                        loginVerifyFail();
                    }
                });

                break;
            case R.id.button_regist:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    private void login() {
        User user = new User();
        user.setUid(mUid);
        user.setName(mUid);
//        mToken = IM_TOKEN;
        Log.d(TAG, "login: token:" + mToken);
        IMClient.getInstance().login(user, true, mToken, new ResultCallback<String>() {
            @Override
            public void onSuccess(String data) {
                loginSuccess();
                changeViewHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(int errorCode) {
               if (errorCode == IMClientError.ERROR_NETWORK) {
                    parseLoginErrorCode(NETWORK_ERROR);
                } else if (errorCode == ERROR_ACCOUNT_ONLINE) {
                    parseLoginErrorCode(LOGIN_SOMENE_ONLINE);
                }
                changeViewHandler.sendEmptyMessage(1);
            }

        });
    }

    private void loginSuccess() {
        Log.i(TAG, mUid + "上线成功");
        if (mIsRememberPassword.isChecked()) {
            Map<String, Object> accountMap = new HashMap<>();
            accountMap.put(KEY_UID, mUid);
            accountMap.put(Constants.Preference.KEY_PASSWORD, mPassword);
            saveAccount(accountMap);
        }
        changeViewHandler.sendEmptyMessage(1);
        mIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mIntent);
        LoginActivity.this.finish();
    }

    private void loginVerifySuccess() {
        Log.i(TAG, "验证成功");
        //获取用户级token
        String token = getUserToken();
//        String token = IM_TOKEN;
        if (!TextUtils.isEmpty(token)) {
            saveUserToken(token);
            initIMClient(token);
            mToken = token;
        } else {
            parseLoginErrorCode(OTHER_ERROR);
            return;
        }
        login();
    }

    private void loginVerifyFail() {
        Handler mMainHandler = new Handler(Looper.getMainLooper());
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        LoginActivity.this);
                builder.setMessage("登录失败，请确定用户名和密码")
                        .setCancelable(false)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mPasswordEdt.setText("");
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        changeViewHandler.sendEmptyMessage(1);
    }

    private void popWindowToFoceLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("该用户已在线，是否强制上线")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(mUid)) {
                                    mUid = mAppidEdt.getText().toString();
                                }
                                changeViewHandler.sendEmptyMessage(0);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginVerifySuccess();
                                    }
                                }).start();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void fillInfo(Map<String, String> loginInfo) {
        if (loginInfo != null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                String uid = intent.getExtras().getString(KEY_UID);
                if (!TextUtils.isEmpty(uid)) {
                    mAppidEdt.setText(uid);
                }
            }

            if (loginInfo.containsKey(KEY_UID)) {
                mAppidEdt.setText(loginInfo.get(KEY_UID));
            }
            if (loginInfo.containsKey(Constants.Preference.KEY_PASSWORD)) {
                mPasswordEdt.setText(loginInfo.get(Constants.Preference.KEY_PASSWORD));
            }
        }

    }

    private void parseLoginErrorCode(int errorCode) {

        switch (errorCode) {
            case LOGIN_INFO_ERROR:
                ToastUtil.showText("用户名或密码错误，请确认");
                break;
            case LOGIN_SOMENE_ONLINE:
                popWindowToFoceLogin();
                break;
            case NETWORK_ERROR:
                ToastUtil.showText("无网络连接");
                break;
            case OTHER_ERROR:
                ToastUtil.showText("登陆异常，请稍后再试");
                break;
        }
    }

    private void handleFromSplashActivity() {
        Bundle bundle = getIntent().getExtras();
        int errorCode;

        if (bundle == null) {
            return;
        } else {
            errorCode = bundle.getInt(KEY_LOGIN_ERRORCODE);
        }
        parseLoginErrorCode(errorCode);
    }

    private boolean isLogin(Map<String, String> loginInfo) {
        if (loginInfo != null) {
            return loginInfo.containsKey(KEY_UID) &&
                    loginInfo.containsKey(Constants.Preference.KEY_PASSWORD);
        } else {
            return false;
        }
    }

    public Map<String, String> getLoginInfo() {
        Map<String, String> loginInfo = (Map<String, String>) SharePreferenceHelper.getInstance().getAll();
        return loginInfo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_qq, menu);
        return true;
    }

    public void saveAccount(Map<String, Object> accountInfo) {
        SharePreferenceHelper.getInstance().saveSharePreference(accountInfo);
    }

    private String getUserToken() {
        try {
            HttpPacket httpPacket = RequestBuilder.buildUserTokenRequest(mUid, IM_APPID);
            String response = HttpUtil.processRequest(httpPacket);
            if (TextUtils.isEmpty(response)) {
                return null;
            } else {
                JSONObject responseJson = new JSONObject(response);
                int ret = responseJson.getInt(RET);
                if (ret != 0) {
                    return null;
                }
                String token = responseJson.getString(TOKEN);
                Log.d(TAG, "getUserToken: token:" + token);
                return token;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (HttpException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void initIMClient(String token) {
        ImApplication.getApp().initIMClient(token);
    }

    private void saveUserToken(String token) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put(TOKEN, token);
        SharePreferenceHelper.getInstance().saveSharePreference(tokenMap);
    }

}
