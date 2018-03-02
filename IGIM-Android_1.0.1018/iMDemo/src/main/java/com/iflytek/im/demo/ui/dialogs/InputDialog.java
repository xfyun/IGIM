package com.iflytek.im.demo.ui.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.SoftInputUtil;


/**
 * Created by ywwynm on 2016/6/8.
 * 用于输入的对话框
 */
public class InputDialog extends BaseDialog {

    private int mAccentColor;

    private TextInputLayout mTil;
    private EditText        mEt;

    private String mTitle;
    private String mHint;
    private String mConfirmText;
    private String mCancelText;

    private String mInitialText;

    private int mMaxLength = 160;

    private Callback mCallback;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_input;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mTil = f(R.id.til_input);
        mEt  = f(R.id.et_input);

        initTitle();
        initInput();
        initCancel();
        initConfirm();

        mEt.postDelayed(new Runnable() {
            @Override
            public void run() {
                SoftInputUtil.showSoftInputView(getActivity(), mEt);
            }
        }, 200);

        return mContentView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        SoftInputUtil.hideSoftInputView(getActivity());
        super.onDismiss(dialog);
    }

    private void initTitle() {
        TextView tvTitle = f(R.id.tv_title_input);
        if (mTitle != null) {
            tvTitle.setText(mTitle);
            tvTitle.setTextColor(mAccentColor);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    private void initInput() {
        if (mHint != null) {
            if (mInitialText != null) {
                mEt.setText(mInitialText);
            }
            mTil.setHint(mHint);
            mEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mMaxLength) });
        } else {
            mTil.setVisibility(View.GONE);
        }
    }

    private void initConfirm() {
        TextView tvConfirm = f(R.id.tv_confirm_panel);
        if (mConfirmText != null) {
            tvConfirm.setText(mConfirmText);
            tvConfirm.setTextColor(mAccentColor);
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onConfirm(mEt.getText().toString());
                    }
                }
            });
        } else {
            tvConfirm.setVisibility(View.GONE);
        }
    }

    private void initCancel() {
        TextView tvCancel = f(R.id.tv_cancel_panel);
        if (mCancelText != null) {
            tvCancel.setText(mCancelText);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onCancel();
                    }
                    dismiss();
                }
            });
        } else {
            tvCancel.setVisibility(View.GONE);
        }
    }

    public void setError(String error) {
        mTil.setError(error);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onConfirm(String content);
        void onCancel();
    }

    public static class Builder {

        private InputDialog mDialog;

        public Builder(int accentColor) {
            mDialog = new InputDialog();
            mDialog.mAccentColor = accentColor;
        }

        public Builder title(String title) {
            mDialog.mTitle = title;
            return this;
        }

        public Builder initialText(String initialText) {
            mDialog.mInitialText = initialText;
            return this;
        }

        public Builder hint(String hint) {
            mDialog.mHint = hint;
            return this;
        }

        public Builder maxLength(int maxLength) {
            mDialog.mMaxLength = maxLength;
            return this;
        }

        public Builder confirmText(String confirmText) {
            mDialog.mConfirmText = confirmText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            mDialog.mCancelText = cancelText;
            return this;
        }

        public InputDialog build() {
            return mDialog;
        }

    }

}
