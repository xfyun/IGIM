package com.iflytek.im.demo.ui.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.SimpleListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ywwynm on 2016/6/2.
 * 内容只有列表的对话框.
 */
public class SimpleListDialog extends BaseDialog {

    private List<String> mItems;
    private List<View.OnClickListener> mOnItemClickListeners;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView rv = f(R.id.rv_dialog_simple_list);
        SimpleListAdapter adapter = new SimpleListAdapter(
                getActivity(), mItems, mOnItemClickListeners);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mContentView;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_simple_list;
    }

    public void setItems(List<String> items) {
        mItems = items;
    }

    public void setItems(String[] items) {
        mItems = new ArrayList<>(items.length);
        Collections.addAll(mItems, items);
    }

    public void setOnItemClickListeners(List<View.OnClickListener> onItemClickListeners) {
        mOnItemClickListeners = onItemClickListeners;
    }
}
