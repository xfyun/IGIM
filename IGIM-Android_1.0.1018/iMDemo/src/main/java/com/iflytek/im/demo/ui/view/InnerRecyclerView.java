package com.iflytek.im.demo.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.iflytek.im.demo.common.DisplayUtil;

/**
 * Created by xiangsun4 on 2016/11/14.
 */

public class InnerRecyclerView extends RecyclerView {

    public static final String TAG = "InnerRecyclerView";


    public InnerRecyclerView(Context context) {
        super(context);
    }


    public InnerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int measuredHeight = DisplayUtil.dp2px(getAdapter().getItemCount()*100);
        int measuredWidth =  DisplayUtil.getScreenWidth();
//        setMeasuredDimension(measuredHeight, measuredWidth);
//        super.onMeasure(widthSpec, heightSpec);
        int width = measureDimension(measuredWidth, widthSpec);
        int height = measureDimension(measuredHeight, heightSpec);
        setMeasuredDimension(width, height);
    }

    public int measureDimension(int defaultSize, int measureSpec){
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = defaultSize;   //UNSPECIFIED
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public InnerRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
