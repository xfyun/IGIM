package com.iflytek.im.demo.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by imxqd on 2016/8/29.
 * 软键盘操作类
 */

public class SoftInputUtil {

    /**
     * 隐藏软键盘
     * @param activity 当前窗口的Activity
     */
    public static void hideSoftInputView(Activity activity) {
        InputMethodManager manager = ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (activity.getCurrentFocus() != null) {
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    /**
     * 显示软键盘
     * @param activity 当前窗口的Activity
     * @param view 需要获取焦点的View
     */
    public static void showSoftInputView(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    public static void addKeyboardCallback(final Window window, final KeyboardCallback callback) {
        final View decorView = window.getDecorView();
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private Rect r = new Rect();
            private int initialDiff = -1;
            private float possibleKeyboardHeight =
                    96 * decorView.getContext().getResources().getDisplayMetrics().density;
            private boolean mKeyboardOpened = false;

            @Override
            public void onGlobalLayout() {
                // decor.getRoot.getHeight is always full height
                int fullHeight = decorView.getRootView().getHeight();
                //Logger.d("fullHeight: " + fullHeight);

                // r will be populated with the coordinates of your view that area still visible.
                decorView.getWindowVisibleDisplayFrame(r);
                //Logger.d("r.bottom - r.top: " + (r.bottom - r.top));

                // get the height diff as px
                int heightDiff = fullHeight - (r.bottom - r.top);
                //Logger.d("height diff: " + heightDiff);

                // set the initialDiff at the beginning.
                if (initialDiff == -1) {
                    initialDiff = heightDiff;
                }

                int diff = heightDiff - initialDiff;
                //Logger.d("heightDiff - initialDiff: " + diff);

                // if it could be a keyboard add the padding to the view
                if (diff > possibleKeyboardHeight) {
                    if (!mKeyboardOpened) {
                        mKeyboardOpened = true;
                        if (callback != null) {
                            callback.onKeyboardShow(diff);
                        }
                    }
                } else if (diff == 0) {
                    if (mKeyboardOpened) {
                        if (callback != null) {
                            callback.onKeyboardHide();
                        }
                        mKeyboardOpened = false;
                    }
                }
            }
        };
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        callback.setOnGlobalLayoutListener(listener);
    }

    @SuppressWarnings("deprecation")
    public static void removeKeyboardCallback(final Window window, final KeyboardCallback callback) {
        ViewTreeObserver observer = window.getDecorView().getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeOnGlobalLayoutListener(callback.getOnGlobalLayoutListener());
        } else {
            observer.removeGlobalOnLayoutListener(callback.getOnGlobalLayoutListener());
        }
    }

    public static abstract class KeyboardCallback {

        private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

        public void setOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
            mOnGlobalLayoutListener = onGlobalLayoutListener;
        }

        public ViewTreeObserver.OnGlobalLayoutListener getOnGlobalLayoutListener() {
            return mOnGlobalLayoutListener;
        }

        public abstract void onKeyboardShow(int keyboardHeight);
        public abstract void onKeyboardHide();
    }
}
