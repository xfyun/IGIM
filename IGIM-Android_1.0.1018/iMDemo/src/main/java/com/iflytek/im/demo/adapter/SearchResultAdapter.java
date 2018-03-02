package com.iflytek.im.demo.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.DisplayUtil;
import com.iflytek.im.demo.ui.view.InnerRecyclerView;

import java.util.List;
import java.util.Map;

import static android.R.attr.width;
import static android.content.ContentValues.TAG;
import static com.iflytek.im.demo.R.id.friendList;

/**
 * Created by Administrator on 2016/11/14.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultHolder> {
    private Map<Integer,List> result;
    private Context mContext;

    public void setResult(Map<Integer, List> result) {
        this.result = result;
    }

    public SearchResultAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seach_result, parent, false));
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
       if(position == 0){
           List<String > friendList = result.get(0);
           if(friendList.size() == 0){
               holder.header.setVisibility(View.GONE);
               holder.recyclerView.setVisibility(View.GONE);
               return;
           }else{
               holder.header.setVisibility(View.VISIBLE);
               holder.recyclerView.setVisibility(View.VISIBLE);
               holder.recyclerView.setLayoutParams(getParams(friendList.size(),holder));
           }
           FriendRVAdapter friendRVAdapter = new FriendRVAdapter();
           friendRVAdapter.setmList(friendList);
           holder.recyclerView.setAdapter(friendRVAdapter);
           holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
           try {
               friendRVAdapter.notifyDataSetChanged();
           } catch (Exception e) {
               Log.e(TAG, "onBindViewHolder: wrong adapter");
               e.printStackTrace();
           }
           holder.header.setText("联系人");
       }else if(position == 1){
           //group
           List<Group> groupList = result.get(1);
           if(groupList.size() == 0){
               holder.header.setVisibility(View.GONE);
               holder.recyclerView.setVisibility(View.GONE);
               return;
           }else{
               holder.header.setVisibility(View.VISIBLE);
               holder.recyclerView.setVisibility(View.VISIBLE);
               holder.recyclerView.setLayoutParams(getParams(groupList.size(),holder));
           }

           GroupRVAdapter groupRVAdapter = new GroupRVAdapter(1);
           groupRVAdapter.setList(groupList);
           holder.recyclerView.setAdapter(groupRVAdapter);
           holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
           try {
               groupRVAdapter.notifyDataSetChanged();
           } catch (Exception e) {
               Log.e(TAG, "onBindViewHolder: wrong adapter");
               e.printStackTrace();
           }
           holder.header.setText("群组");
       }else{
           //discussion
           List<Group> discussionGroupList = result.get(2);
           if(discussionGroupList.size() == 0){
               holder.header.setVisibility(View.GONE);
               holder.recyclerView.setVisibility(View.GONE);
               return;
           }else{
               holder.header.setVisibility(View.VISIBLE);
               holder.recyclerView.setVisibility(View.VISIBLE);
               holder.recyclerView.setLayoutParams(getParams(discussionGroupList.size(),holder));
           }

           GroupRVAdapter discussionGroupRVAdapter = new GroupRVAdapter(0);
           discussionGroupRVAdapter.setList(discussionGroupList);
           holder.recyclerView.setAdapter(discussionGroupRVAdapter);
           holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
           discussionGroupRVAdapter.notifyDataSetChanged();
           holder.header.setText("讨论组");
       }

    }

    public ViewGroup.LayoutParams getParams(int size, SearchResultHolder holder){
        ViewGroup.LayoutParams  params =  holder.recyclerView.getLayoutParams();
        params.height = DisplayUtil.dp2px((size+2) * 68);
        params.width = DisplayUtil.getScreenWidth();

        return params;
    }



    @Override
    public int getItemCount() {
        return result.size();
    }

    class SearchResultHolder extends  BaseViewHolder{
        private TextView header;
        private RecyclerView recyclerView;
        public SearchResultHolder(View itemView) {
            super(itemView);
            recyclerView = f(R.id.search_result_rv);
            header = f(R.id.recyclerview_header);
        }





    }

}
