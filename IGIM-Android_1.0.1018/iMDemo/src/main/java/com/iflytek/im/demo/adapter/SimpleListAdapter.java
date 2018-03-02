package com.iflytek.im.demo.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iflytek.im.demo.R;

import java.util.List;

/**
 * Created by ywwynm on 2016/6/2.
 * 在一个列表中显示一些操作
 */
public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.SimpleListHolder> {

    private LayoutInflater mInflater;
    private List<String> mItems;
    private List<View.OnClickListener> mOnItemClickListeners;

    public SimpleListAdapter(
            @NonNull Activity activity,
            @NonNull List<String> items,
            @NonNull List<View.OnClickListener> onItemClickListeners) {
        mInflater = LayoutInflater.from(activity);
        mItems = items;
        mOnItemClickListeners = onItemClickListeners;
    }

    @Override
    public SimpleListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleListHolder(mInflater.inflate(R.layout.rv_simple_list, parent, false));
    }

    @Override
    public void onBindViewHolder(SimpleListHolder holder, int position) {
        holder.tv.setText(mItems.get(position));
        if (position == getItemCount() - 1) {
            holder.separator.setVisibility(View.INVISIBLE);
        } else {
            holder.separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class SimpleListHolder extends BaseViewHolder {

        TextView tv;
        View separator;

        public SimpleListHolder(View itemView) {
            super(itemView);

            tv = f(R.id.tv_simple_list);
            separator = f(R.id.view_separator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos >= mOnItemClickListeners.size()) {
                        return;
                    }

                    View.OnClickListener listener = mOnItemClickListeners.get(pos);
                    if (listener != null) {
                        listener.onClick(v);
                    }
                }
            });
        }
    }

}
