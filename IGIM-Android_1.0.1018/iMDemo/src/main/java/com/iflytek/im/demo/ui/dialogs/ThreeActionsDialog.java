package com.iflytek.im.demo.ui.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.im.demo.R;


/**
 * Created by ywwynm on 2016/6/4.
 * 带有三个操作的对话框
 */
public class ThreeActionsDialog extends BaseDialog {

    private int mAccentColor;

    private String mTitle;
    private String mContent;

    private String mFirstActionText;
    private String mSecondActionText;
    private String mCancelText;

    private View.OnClickListener mFirstActionListener;
    private View.OnClickListener mSecondActionListener;
    private View.OnClickListener mCancelListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_three_actions;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initTitle();
        initContent();
        initFirstAction();
        initSecondAction();
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

    private void initFirstAction() {
        TextView tvFirstAction = f(R.id.tv_first_action_alert);
        if (mFirstActionText != null) {
            tvFirstAction.setText(mFirstActionText);
            tvFirstAction.setTextColor(mAccentColor);
            tvFirstAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFirstActionListener != null) {
                        mFirstActionListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvFirstAction.setVisibility(View.GONE);
        }
    }

    private void initSecondAction() {
        TextView tvSecondAction = f(R.id.tv_second_action_alert);
        if (mSecondActionText != null) {
            tvSecondAction.setText(mSecondActionText);
            tvSecondAction.setTextColor(mAccentColor);
            tvSecondAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSecondActionListener != null) {
                        mSecondActionListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvSecondAction.setVisibility(View.GONE);
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

        private ThreeActionsDialog mDialog;

        public Builder(int accentColor) {
            mDialog = new ThreeActionsDialog();
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

        public Builder firstActionText(String firstActionText) {
            mDialog.mFirstActionText = firstActionText;
            return this;
        }

        public Builder secondActionText(String secondActionText) {
            mDialog.mSecondActionText = secondActionText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            mDialog.mCancelText = cancelText;
            return this;
        }

        public Builder firstActionListener(View.OnClickListener listener) {
            mDialog.mFirstActionListener = listener;
            return this;
        }

        public Builder secondActionListener(View.OnClickListener listener) {
            mDialog.mSecondActionListener = listener;
            return this;
        }

        public Builder cacelListener(View.OnClickListener listener) {
            mDialog.mCancelListener = listener;
            return this;
        }

        public ThreeActionsDialog build() {
            return mDialog;
        }

    }


}
