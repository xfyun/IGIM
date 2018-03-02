package com.iflytek.im.demo.ui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;


/**
 * Created by ywwynm on 2016/6/4.
 * 警告对话框
 */
public class AlertDialog extends BaseDialog {

    private int mAccentColor;

    private String mTitle;
    private String mContent;
    private String mConfirmText;
    private String mCancelText;

    private View.OnClickListener mConfirmListener;
    private View.OnClickListener mCancelListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_alert;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initTitle();
        initContent();
        initConfirm();
        initCancel();

        return mContentView;
    }

    private void initTitle() {
        TextView tvTitle = f(R.id.tv_title_alert);
        if (mTitle != null) {
            tvTitle.setText(mTitle);
            tvTitle.setTextColor(mAccentColor);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    private void initContent() {
        TextView tvContent = f(R.id.tv_content_alert);
        if (mContent != null) {
            tvContent.setText(mContent);
        } else {
            tvContent.setVisibility(View.GONE);
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
                    if (mConfirmListener != null) {
                        mConfirmListener.onClick(v);
                    }
                    dismiss();
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
                    if (mCancelListener != null) {
                        mCancelListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvCancel.setVisibility(View.GONE);
        }
    }

    public static class Builder {

        private AlertDialog mDialog;

        public Builder(int accentColor) {
            mDialog = new AlertDialog();
            mDialog.mAccentColor = accentColor;
        }

        public Builder title(String title) {
            mDialog.mTitle = title;
            return this;
        }

        public Builder content(String content) {
            mDialog.mContent = content;
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

        public Builder confirmListener(View.OnClickListener listener) {
            mDialog.mConfirmListener = listener;
            return this;
        }

        public Builder canelListener(View.OnClickListener listener) {
            mDialog.mCancelListener = listener;
            return this;
        }

        public AlertDialog build() {
            return mDialog;
        }

    }
}
