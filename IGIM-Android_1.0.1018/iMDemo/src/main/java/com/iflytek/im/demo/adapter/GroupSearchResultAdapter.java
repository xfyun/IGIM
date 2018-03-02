package com.iflytek.im.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.im.demo.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/11/17.
 */

public class GroupSearchResultAdapter extends RecyclerView.Adapter<GroupSearchResultAdapter.GroupSearchResultHolder> {
    private List<Group> mGroupList;
    private GroupSearchCallBack callBack;





    @Override
    public GroupSearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupSearchResultHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(GroupSearchResultHolder holder, int position) {
        final Group group = mGroupList.get(position);
        holder.title.setText(group.getgName());
        String describe = group.getDescribe();
        if(describe == null){
            describe = "无";
        }
        holder.subtitle.setText("群介绍："+describe);

        holder.joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, String> joinGroupParams = new HashMap<>();
                joinGroupParams.put(Group.GID, group.getGid());
                callBack.joinGroup(joinGroupParams);

            }
        });


    }


    @Override
    public int getItemCount() {
        return mGroupList.size();
    }



    public interface GroupSearchCallBack {
        void joinGroup(Map<String, String> joinGroupParams);
    }


    class GroupSearchResultHolder extends BaseViewHolder {
        ImageView icon;
        TextView title;
        TextView subtitle;
        Button joinGroup;


        public GroupSearchResultHolder(View itemView) {
            super(itemView);
            icon = f(R.id.group_search_item_icon);
            title = f(R.id.group_search_item_title);
            subtitle = f(R.id.group_search_item_subtitle);
            joinGroup = f(R.id.join_search_result);
        }
    }


    public void setmGroupList(List<Group> mGroupList) {
        this.mGroupList = mGroupList;
    }

    public void setCallBack(GroupSearchCallBack callBack) {
        this.callBack = callBack;
    }
}
