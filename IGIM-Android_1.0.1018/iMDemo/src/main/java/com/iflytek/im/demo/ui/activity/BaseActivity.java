package com.iflytek.im.demo.ui.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.im.demo.AppManager;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
        initMembers();
        setContentView(getLayoutRes());
        findViews();
        initViews();
        setupEvents();
        AppManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        AppManager.removeActivity(this);
    }

    protected abstract @LayoutRes int getLayoutRes();

    @SuppressWarnings("unchecked")
    final protected <T extends View> T f(@IdRes int id) {
        return (T) findViewById(id);
    }

    @SuppressWarnings("unchecked")
    final protected <T extends View> T f(View parent, @IdRes int id) {
        return (T) parent.findViewById(id);
    }

    protected void initMembers() {}

    protected void findViews() {}

    protected void initViews() {}

    protected void setupEvents() {}

}
