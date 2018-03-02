package com.iflytek.im.demo.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.iflytek.im.demo.R;

import java.util.ArrayList;

/**
 * Created by sun on 2017-05-23.
 */

public class PopWindowUtil {
    private Context mContext;

    public PopWindowUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void popWindow(ArrayList<String> text, final ArrayList<View.OnClickListener> listener, View view) {


        if (text.size() < 1 || listener.size() < 1) {
            return;
        }
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.pop_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置按钮的点击事件
        Button first = (Button) contentView.findViewById(R.id.button1);
        first.setText(text.get(0));
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.get(0).onClick(view);
                popupWindow.dismiss();
            }
        });
        Button second = (Button) contentView.findViewById(R.id.button2);
        Button third = (Button) contentView.findViewById(R.id.button3);
        View h1 = contentView.findViewById(R.id.hr1);
        View h2 = contentView.findViewById(R.id.hr2);


        if (text.size() >= 2) {
            // 设置按钮的点击事件
            second.setText(text.get(1));
            second.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.get(1).onClick(view);
                    popupWindow.dismiss();
                }
            });
        }else {
            second.setVisibility(View.INVISIBLE);
            h1.setVisibility(View.INVISIBLE);
        }

        if(text.size() >=3 ) {
            third.setText(text.get(2));
            third.setOnClickListener(listener.get(2));
            third.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.get(2).onClick(view);
                    popupWindow.dismiss();
                }
            });
        }else {
            third.setVisibility(View.INVISIBLE);
            h2.setVisibility(View.INVISIBLE);
        }




        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("mengdd", "onTouch : ");
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0,0 );

    }
}
