package com.iflytek.im.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.im.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 群组列表界面的RecyclerView的适配器
 * Created by imxqd on 2016/9/1.
 */

public class KickGrouperAdapter extends RecyclerView.Adapter<KickGrouperAdapter.KickGrouperHolder> {
    private static final String TAG = "GroupRVAdapter";

    public KickGrouperAdapter(List<String> list) {
        if(list != null){
            mMemberList = list;
        }else {
            mMemberList = new ArrayList<>();
        }
        mSelectedMemeber = new ArrayList<>();

    }

    public KickGrouperAdapter() {
    }

    private List<String> mMemberList;
    private List<String> mSelectedMemeber;

    @Override
    public KickGrouperHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new KickGrouperHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kickgrouper_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final KickGrouperHolder holder, int position) {
        holder.grouperNameTV.setText(mMemberList.get(position));
        holder.isSelectedCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {
                if(isSelected) {
                    mSelectedMemeber.add(holder.grouperNameTV.getText().toString());
                }else {
                    mSelectedMemeber.remove(holder.grouperNameTV.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMemberList.size();
    }

    public List<String> getmSelectedMemeber() {
        return mSelectedMemeber;
    }

    class KickGrouperHolder extends BaseViewHolder {
        RelativeLayout kickGrouperRL;
        TextView grouperNameTV;
        CheckBox isSelectedCB;


        public KickGrouperHolder(View itemView) {
            super(itemView);
            kickGrouperRL = f(R.id.kick_grouper_item);
            grouperNameTV = f(R.id.grouper_name);
            isSelectedCB = f(R.id.is_selected);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelectedCB.isChecked())
                        isSelectedCB.setChecked(false);
                    else
                        isSelectedCB.setChecked(true);
                }
            });
        }
    }
}
