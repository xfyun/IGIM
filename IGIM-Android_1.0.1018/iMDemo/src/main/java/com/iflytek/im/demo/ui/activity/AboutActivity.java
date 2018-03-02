package com.iflytek.im.demo.ui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
import com.iflytek.im.demo.Config;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ToastUtil;

public class AboutActivity extends BaseActivity {

    private IFlytekUpdate  updater;

    private TextView mTvCodeName;

    private String mVersionName;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void findViews() {
        mTvCodeName = f(R.id.about_code_name);
    }

    @Override
    protected void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try {
            PackageInfo info = getPackageManager()
                    .getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            mVersionName = info.versionName;
            mTvCodeName.setText(mVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            if (Config.isDebug) {
                e.printStackTrace();
            }
        }
        checkForUpdate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    private void checkForUpdate() {
        updater = IFlytekUpdate.getInstance(getApplicationContext());
        updater.setDebugMode(true);
        updater.setParameter(UpdateConstants.EXTRA_WIFIONLY, "true");
        updater.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false");
        updater.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
        updater.forceUpdate(getApplicationContext(), updateListener);
    }

    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorCode, UpdateInfo result) {
            switch (errorCode) {
                case UpdateErrorCode.OK:
                    if (result != null && result.getUpdateType() == UpdateType.NoNeed) {
                        Looper.prepare();
                        ToastUtil.showText("已经是最新版本");
                        Looper.loop();
                        return;
                    }
                    updater.showUpdateInfo(AboutActivity.this, result);
                    break;
                case 20002:
                    Looper.prepare();
                    ToastUtil.showText("网络不给力哦");
                    Looper.loop();
                    break;
            }
        }
    };



}
