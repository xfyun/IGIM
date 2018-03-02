package com.iflytek.im.demo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwangjr.rxbus.RxBus;

/**
 * 本App中所有Fragment的基类
 * Created by imxqd on 2016/8/30.
 */

public abstract class BaseFragment extends Fragment {

    protected View mContentView;

    protected boolean mShouldBackFromFragment = true;

    public void setShouldBackFromFragment(boolean shouldBackFromFragment) {
        mShouldBackFromFragment = shouldBackFromFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        beforeInit();
        initMember();
    }

    @Override
    public void onResume() {
        super.onResume();
        RxBus.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxBus.get().unregister(this);
    }

    /**
     * 该方法必须被子类在onCreateView()中调用
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContentView = inflater.inflate(getLayoutRes(), container, false);
        findViews();
        initUI();
        setupEvents();
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    protected abstract @LayoutRes int getLayoutRes();

    protected void beforeInit() { }

    protected void initMember() { }

    protected void findViews() { }

    protected void initUI() { }

    protected void setupEvents() { }


    protected final <T extends View> T f(View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    protected final <T extends View> T f(@IdRes int id) {
        return f(mContentView, id);
    }
}
