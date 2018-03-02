package com.iflytek.im.demo.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ywwynm on 2016/5/25.
 * 一个基本的ViewHolder类
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected <T extends View> T f(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }
}
