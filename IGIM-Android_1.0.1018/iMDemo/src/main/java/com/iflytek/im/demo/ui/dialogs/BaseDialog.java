package com.iflytek.im.demo.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by ywwynm on 2016/6/2.
 * 所有{@link DialogFragment}的基类.
 */
public abstract class BaseDialog extends DialogFragment {

    protected View mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutResource(), container, false);
        return mContentView;
    }

    protected abstract @LayoutRes int getLayoutResource();

    protected final <T extends View> T f(View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    protected final <T extends View> T f(@IdRes int id) {
        return f(mContentView, id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
    }
}
