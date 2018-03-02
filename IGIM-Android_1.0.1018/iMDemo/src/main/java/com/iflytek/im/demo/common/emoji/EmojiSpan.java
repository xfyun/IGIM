package com.iflytek.im.demo.common.emoji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.DynamicDrawableSpan;

import java.lang.ref.WeakReference;

/**
 * 表情
 * Created by imxqd on 2016/8/30.
 */

public class EmojiSpan extends DynamicDrawableSpan {

    private final Context mContext;

    private final int mResourceId;

    private final int mSize;

    private final int mTextSize;

    private int mHeight;

    private int mWidth;

    private int mTop;

    private Drawable mDrawable;

    private WeakReference<Drawable> mDrawableRef;

    public EmojiSpan(Context context, int resourceId, int size, int alignment, int textSize) {
        super(alignment);
        mContext = context;
        mResourceId = resourceId;
        mWidth = mHeight = mSize = size;
        mTextSize = textSize;
    }

    public Drawable getDrawable() {
        if (mDrawable == null) {
            mDrawable = ContextCompat.getDrawable(mContext, mResourceId);
            mHeight = mSize;
            mWidth = mHeight * mDrawable.getIntrinsicWidth() / mDrawable.getIntrinsicHeight();
            mTop = (mTextSize - mHeight) / 2;
            mDrawable.setBounds(0, mTop, mWidth, mTop + mHeight);
        }
        return mDrawable;
    }

    @Override
    public void draw(
            Canvas canvas, CharSequence text, int start, int end,
            float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY = top + ((bottom - top) / 2) - ((b.getBounds().bottom - b.getBounds().top) / 2) - mTop;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        if (mDrawableRef == null || mDrawableRef.get() == null) {
            mDrawableRef = new WeakReference<>(getDrawable());
        }
        return mDrawableRef.get();
    }
}
