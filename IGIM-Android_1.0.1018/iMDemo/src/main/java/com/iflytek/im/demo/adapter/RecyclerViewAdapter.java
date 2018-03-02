package com.iflytek.im.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.im.demo.R;

import java.util.List;

/**
 * Created by admin on 2017/1/9.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private List<String> mList;
    private ClickCallBack mClickCallBack;


    public RecyclerViewAdapter(List<String> mList) {
        this.mList = mList;
    }
    public RecyclerViewAdapter() {
    }

    public void setList(List<String> mList) {
        this.mList = mList;
    }
    public void setClickCallBack(ClickCallBack mClickCallBack) {
        this.mClickCallBack = mClickCallBack;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final String name = mList.get(position);
        holder.nameTV.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickCallBack.clickCallBack(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setmList(List<String> list) {
        this.mList = list;
    }

    class RecyclerViewHolder extends BaseViewHolder {
        TextView nameTV;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            nameTV = f(R.id.name);
        }
    }

    public interface ClickCallBack {
        void clickCallBack(String name);
    }
}
