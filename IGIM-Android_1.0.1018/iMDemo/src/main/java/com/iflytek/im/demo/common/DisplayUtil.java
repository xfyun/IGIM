package com.iflytek.im.demo.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ywwynm on 2016/5/17.
 * Modified by IMXQD on 2016/8/29.
 * 关于显示的类
 */
public class DisplayUtil {
    private static final String TAG = "DisplayUtil";

    private static float density = ImApplication.getApp().getResources().getDisplayMetrics().density;

    /**
     * 将dp值转换为对应的px值
     * @param dp 待转换的dp值
     * @return 转换后的px值
     */
    public static int dp2px(float dp) {
        return (int) (dp * density);
    }

    /**
     * 将px值转换为对应的dp值
     * @param px 待转换的px值
     * @return 转换后的dp值
     */
    public static float px2dp(int px) {
        return (px * density);
    }

    /**
     * @return 设备屏幕的真实宽高
     */
    public static Point getScreenSize() {
        Point screen = new Point();
        Display display = ((WindowManager) ImApplication.getApp().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                screen.x = (Integer) mGetRawW.invoke(display);
                screen.y = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                display.getSize(screen);
                Log.e(TAG, "Cannot use reflection to get real screen size. " +
                        "Returned size may be wrong.");
            }
        } else {
            display.getRealSize(screen);
        }
        return screen;
    }

    public static int getScreenWidth() {
        return getScreenSize().x;
    }

    public static  int getScreenHeight(){
        return getScreenSize().y;
    }

    /**
     * @return 状态栏高度
     */
    public static int getStatusbarHeight() {
        Resources resources = ImApplication.getApp().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else return 0;
    }

    /**
     * @return 如果设备有虚拟导航栏，返回{@code true}；否则，返回{@code false}
     */
    public static boolean hasNavigationBar() {
        boolean hasMenuKey = ViewConfiguration.get(ImApplication.getApp()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    /**
     * @return 虚拟导航栏的高度
     */
    public static int getNavigationBarHeight() {
        Resources resources = ImApplication.getApp().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else return 0;
    }

    /**
     * 为{@param editText}设置文字选择时开始、结束标志的颜色
     */
    public static void setSelectionHandlersColor(EditText editText, int color) {
        try {
            final Class<?> cTextView = TextView.class;
            final Field fhlRes = cTextView.getDeclaredField("mTextSelectHandleLeftRes");
            final Field fhrRes = cTextView.getDeclaredField("mTextSelectHandleRightRes");
            final Field fhcRes = cTextView.getDeclaredField("mTextSelectHandleRes");
            fhlRes.setAccessible(true);
            fhrRes.setAccessible(true);
            fhcRes.setAccessible(true);

            int hlRes = fhlRes.getInt(editText);
            int hrRes = fhrRes.getInt(editText);
            int hcRes = fhcRes.getInt(editText);

            final Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            final Object editor = fEditor.get(editText);

            final Class<?> cEditor = editor.getClass();
            final Field fSelectHandleL = cEditor.getDeclaredField("mSelectHandleLeft");
            final Field fSelectHandleR = cEditor.getDeclaredField("mSelectHandleRight");
            final Field fSelectHandleC = cEditor.getDeclaredField("mSelectHandleCenter");
            fSelectHandleL.setAccessible(true);
            fSelectHandleR.setAccessible(true);
            fSelectHandleC.setAccessible(true);

            Drawable selectHandleL = ContextCompat.getDrawable(editText.getContext(), hlRes);
            Drawable selectHandleR = ContextCompat.getDrawable(editText.getContext(), hrRes);
            Drawable selectHandleC = ContextCompat.getDrawable(editText.getContext(), hcRes);

            selectHandleL.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            selectHandleR.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            selectHandleC.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);

            fSelectHandleL.set(editor, selectHandleL);
            fSelectHandleR.set(editor, selectHandleR);
            fSelectHandleC.set(editor, selectHandleC);
        } catch (Exception ignored) { }
    }

    /**
     * 获得 Material Design 规定的色值为 100 的颜色作为相应的浅色
     * @param mdColor500 色值为 500 的 Material 颜色
     */
    public static int getLightColor(int mdColor500) {
        return getSamePosColor(R.array.material_500, mdColor500, R.array.material_100);
    }


    /**
     * 获得 Material Design 规定的色值为 700 的颜色作为相应的深色
     * @param mdColor500 色值为 500 的 Material 颜色
     */
    public static int getDarkColor(int mdColor500) {
        return getSamePosColor(R.array.material_500, mdColor500, R.array.material_700);
    }

    private static int getSamePosColor(
            @ArrayRes int oColorArrRes, int oColor, @ArrayRes int dColorArrRes) {
        int[] oColorArr = ImApplication.getApp().getResources().getIntArray(oColorArrRes);
        int[] dColorArr = ImApplication.getApp().getResources().getIntArray(dColorArrRes);
        for (int i = 0; i < oColorArr.length; i++) {
            if (oColorArr[i] == oColor) {
                return dColorArr[i];
            }
        }
        return dColorArr[0];
    }

}
