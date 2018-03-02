package com.iflytek.im.demo.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.NetworkControl;
import com.iflytek.im.demo.common.PinyinUtil;
import com.iflytek.im.demo.dao.Db;
import com.iflytek.im.demo.listener.UserListener;
import com.iflytek.im.demo.processor.Processor;
import com.iflytek.im.demo.ui.activity.TalkActivity;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 朋友列表界面的RecyclerView的适配器
 * Created by imxqd on 2016/9/1.
 */

public class FriendRVAdapter extends RecyclerView.Adapter<FriendRVAdapter.FriendHolder> implements
        StickyRecyclerHeadersAdapter<FriendRVAdapter.FriendHolder> {

    private static final String TAG = "FriendRVAdapter";

    private Map<String, Integer> mLetters = new HashMap<>();
    private List<String> mList;
    private RemoveCallBack removeCallBack;
    private FriendOnClickListener onClickListener;



    public interface FriendOnClickListener{
        void onFriendClick(int position);
    }


    public void setRemoveCallBack(RemoveCallBack removeCallBack) {
        this.removeCallBack = removeCallBack;
    }

    private boolean mDelIconVisible = false;

    public interface UpdateCallback {
        void onComplete();
        void updateLetter(List letter);
    }

    public interface RemoveCallBack {
        void remove(String name);
    }

    public void setmList(List<String> mList) {
        this.mList = mList;
    }

    public FriendRVAdapter() {
        mList = new ArrayList<>();
    }

    public void setOnItemClickListener(FriendOnClickListener listener) {
        onClickListener = listener;
    }

    public void update(final UpdateCallback callback) {
        if(NetworkControl.getInstance().isNetworkConnected()) {
            Processor.getUser(Constants.Id.IM_APPID, new UserListener() {

                public void onUser(List<String> users) {

                    mList.clear();
                    users.remove(IMClient.getInstance().getCurrentUser());
                    Db.getInstance().setFriends(users);
                    users = sort(users);
                    callback.updateLetter(addLetters(users));
                    mList.addAll(users);
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: wrong adapter");
                        e.printStackTrace();
                    }
                    callback.onComplete();
                }

                public void onFailed() {
                    Log.i(TAG, "获取用户失败");
                    callback.onComplete();
                }
            });
        }
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FriendHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(final FriendHolder holder, int pos) {
        if( !mDelIconVisible ) {
            holder.delIcon.setVisibility(View.GONE);
        }else {
            holder.delIcon.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(false);
            holder.delIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mList.indexOf(holder.text.getText().toString());
                    removeCallBack.remove(mList.get(position));
                }
            });
        }
        holder.icon.setImageResource(R.drawable.iv_chat_from);
        holder.text.setText(mList.get(pos));
    }



    @Override
    public long getHeaderId(int position) {
        return getItem(position).toUpperCase().charAt(0);
    }

    @Override
    public FriendHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new FriendHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(FriendHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(String.valueOf(getItem(position).toUpperCase().charAt(0)));
//        holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }


    public void setDelIconVisiable() {
        mDelIconVisible = true;
        notifyItemRangeChanged(0, mList.size());
    }



    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FriendHolder extends BaseViewHolder {
        ImageView icon, delIcon;
        TextView text;

        public FriendHolder(final View itemView) {
            super(itemView);
            icon = f(R.id.friend_item_icon);
            text = f(R.id.friend_item_text);
            delIcon = f(R.id.del_icon);
            if(onClickListener == null ) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String user = mList.get(getAdapterPosition());
                        Intent intent = new Intent(itemView.getContext(), TalkActivity.class);
                        intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, user);
                        intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, 0);
                        intent.putExtra(Constants.Parameter.KEY_RECEIVER_NAME,user);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onFriendClick(getAdapterPosition());
                    }
                });
            }



        }

    }


    public List<String> sort(List<String> users){
        List<String> usersPinYin = hanzi2Pinyin(users);
        for(int i = 0; i < usersPinYin.size(); i++){
            int min = i;
            for(int j=i+1; j< usersPinYin.size();j++){
                String strj = usersPinYin.get(j);
                String strMin = usersPinYin.get(min);

                if(strj.toLowerCase().compareTo(strMin.toLowerCase()) < 0){
                    min = j;
                }
            }
            String temp = users.get(i);
            users.set(i,users.get(min));
            users.set(min,temp);
            temp = usersPinYin.get(i);
            usersPinYin.set(i,usersPinYin.get(min));
            usersPinYin.set(min,temp);
        }
        return users;
    }


    private String hanzi2Pinyin(String str){
        String pinyin = PinyinUtil.getPinyin(str).toString();
        str = pinyin.substring(pinyin.indexOf("[")+1,pinyin.lastIndexOf("]"));
        str = str.replaceAll(",","");
        return str;
    }
    private List<String> hanzi2Pinyin(List<String> list){
        List<String> result = new ArrayList<>();
        for(String str : list){
            if(PinyinUtil.isHanzi(str)){
                result.add(hanzi2Pinyin(str));
            }else{
                result.add(str);
            }
        }
        return result;
    }


    private List<String> addLetters(List<String > users){
        int position = 0;
        ArrayList<String> customLetters = new ArrayList<>();
        mLetters.clear();
        for(String user : users){
            if(PinyinUtil.isHanzi(user)){
               user = hanzi2Pinyin(user);
            }
            user = user.toUpperCase();
            if(!mLetters.containsKey(user.charAt(0)+"")){
                mLetters.put(user.charAt(0) + "",position);
                customLetters.add(user.charAt(0) + "");
            }
            position ++;
        }
        return customLetters;
    }

    public List<String> getmList() {
        return mList;
    }


    public Map<String, Integer> getmLetters() {
        return mLetters;
    }

    public String getItem(int position) {
        String item = mList.get(position);
        if(PinyinUtil.isHanzi(item)){
            item = hanzi2Pinyin(item);
        }

        return item;
    }

}
