package com.iflytek.im.demo.ui.activity;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.listener.ResultListener;
import com.iflytek.im.demo.processor.Processor;

public class RegisterActivity extends BaseActivity implements TextWatcher {
	private static final String TAG = "RegisterActivity";
	private TextInputLayout mUidInput;
	private TextInputLayout mPasswordInput;
	private Button mRegisterBtn;
	private String mUid, mPassword;

    @Override
	protected void findViews() {
		mUidInput = f(R.id.register_username);
		mPasswordInput = f(R.id.register_password);
		mRegisterBtn = f(R.id.register_btn);
	}

    @Override
    protected void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.title_activity_register);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void setupEvents() {
        mUidInput.getEditText().addTextChangedListener(this);
        mPasswordInput.getEditText().addTextChangedListener(this);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mUid = mUidInput.getEditText().getText().toString();
                if(mUid.length()<3 || mUid.length() >16){
                    mUidInput.setError("请输入3-16位用户名");
                    return;
                }
                mPassword = mPasswordInput.getEditText().getText().toString();
                if(mPassword.length()<6||mPassword.length()>18){
                    mPasswordInput.setError("请输入6-16位密码");
                    return;
                }
                if (TextUtils.isEmpty(mUid)) {
                    mUidInput.setError(getString(R.string.error_input_username));
                    mUidInput.requestFocus();
                } else if (TextUtils.isEmpty(mPassword)){
                    mPasswordInput.setError(getString(R.string.error_input_password));
                    mPasswordInput.requestFocus();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            RegisterActivity.this);
                    builder.setCancelable(false)
                            .setPositiveButton(R.string.dialog_confirm,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            finish();
                                        }
                                    })
                            .setNegativeButton(R.string.dialog_cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                        }
                                    });

                    Processor.register(Constants.Id.IM_APPID, mUid, mPassword, new ResultListener() {
                        Handler mMainHandler = new Handler(Looper.getMainLooper());

                        public void onSuccess() {
                            Log.i(TAG, "注册成功");
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.setTitle(R.string.dialog_title_register_success)
                                            .setMessage(R.string.dialog_content_register_success);
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        }

                        public void onFailed() {



                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.setTitle(R.string.dialog_title_register_fail)
                                            .setMessage(R.string.dialog_content_register_fail);
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                            Log.i(TAG, "注册失败,如已注册请直接登录");
                        }
                    });
                }
            }
        });
    }

	@Override
	protected int getLayoutRes() {
		return R.layout.register;
	}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mUidInput.setError(null);
        mPasswordInput.setError(null);
    }
}
