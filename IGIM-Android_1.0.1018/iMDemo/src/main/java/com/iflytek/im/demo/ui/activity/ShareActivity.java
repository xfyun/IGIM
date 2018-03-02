package com.iflytek.im.demo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.conversation.Conversation;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.ConversationRVAdapter;
import com.iflytek.im.demo.adapter.FriendRVAdapter;
import com.iflytek.im.demo.adapter.GroupRVAdapter;
import com.iflytek.im.demo.dao.Db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/2/13.
 */

public class ShareActivity extends BaseActivity implements ConversationRVAdapter.ItemEventCallback {
    private final static String TAG = "shareActivity";


    private EditText searchET;
    private TextView content;
    private LinearLayout chooseFriend;
    private LinearLayout chooseGroup;
    private RecyclerView recentContacts;

    private ConversationRVAdapter conversationAdapter;
    private FriendRVAdapter friendRVAdapter;
    private GroupRVAdapter groupRVAdapter;
    private Context mContext;
    private FriendRVAdapter.UpdateCallback updateCallback;

    @Override
    protected int getLayoutRes() {
        return R.layout.share_activity;
    }


    private FriendRVAdapter.FriendOnClickListener onClickListener;
    private GroupRVAdapter.GroupOnClickListener onGroupClickListener;

    @Override
    protected void initViews() {
        super.initViews();
        searchET = (EditText) findViewById(R.id.share_search_et);
        chooseFriend = (LinearLayout) findViewById(R.id.choose_friend_ll);
        chooseGroup = (LinearLayout) findViewById(R.id.choose_group_ll);
        recentContacts = (RecyclerView) findViewById(R.id.recent_contacts);
        content = (TextView) findViewById(R.id.content);
    }

    @Override
    protected void initMembers() {
        super.initMembers();
        mContext = this;
        conversationAdapter = new ConversationRVAdapter();
        updateCallback = new FriendRVAdapter.UpdateCallback() {
            @Override
            public void onComplete() {
                friendRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void updateLetter(List letter) {

            }
        };
        /*groupUpdateCallback = new GroupRVAdapter.UpdateCallback() {
            @Override
            public void onComplete() {
//                mAllGroups.clear();
//                mAllGroups.addAll(groupRVAdapter.getmGroupList());
//                mAllGroups.addAll(groupRVAdapter.getmSmallGroupList());
                Log.d(TAG, "onComplete: " + mAllGroups);
                groupRVAdapter.setmList(IMClient.getInstance().getAllGroups());
                recentContacts.setAdapter(groupRVAdapter);
                recentContacts.setLayoutManager(new LinearLayoutManager(mContext));
                groupRVAdapter.notifyDataSetChanged();
            }
        } ;*/
    }

    @Override
    protected void setupEvents() {
        super.setupEvents();
        conversationAdapter.setCallback(this);
        conversationAdapter.updateData();
        recentContacts.setAdapter(conversationAdapter);
        recentContacts.setLayoutManager(new LinearLayoutManager(this));
        initListener();
    }


    @Subscribe(tags = {@Tag(Constants.Event.GET_CONVERSATIONS_SUCCESS)})
    public void onGetConversationsSuccess(Object obj) {
        Log.d(TAG, "onGetConversationsSuccess: ");
        conversationAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        chooseFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setText("好友");
                friendRVAdapter = new FriendRVAdapter();
                recentContacts.setAdapter(friendRVAdapter);
                recentContacts.setLayoutManager(new LinearLayoutManager(mContext));
                friendRVAdapter.setOnItemClickListener(onClickListener);
                friendRVAdapter.update(updateCallback);
            }
        });
        onClickListener = new FriendRVAdapter.FriendOnClickListener() {
            @Override
            public void onFriendClick(int position) {
                String user = friendRVAdapter.getmList().get(position);
                Intent intent = new Intent(mContext, TalkActivity.class);
                intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, user);
                intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, 0);
                intent.putExtra(Constants.Parameter.KEY_RECEIVER_NAME,user);

                intent.putExtra(Constants.Parameter.KEY_IS_SHARE, true);
                intent.putExtra(Constants.Parameter.KEY_TEXT, getShareText());

                mContext.startActivity(intent);
                finish();
            }
        };

        onGroupClickListener = new GroupRVAdapter.GroupOnClickListener() {

            @Override
            public void onGroupClick(int position) {
                Group group = IMClient.getInstance().getAllGroups().get(position);
                Intent intent = new Intent(mContext, TalkActivity.class);
                intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, group.getGid());
                intent.putExtra(Constants.Parameter.KEY_RECEIVER_NAME, group.getgName());
                intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, 1);

                intent.putExtra(Constants.Parameter.KEY_IS_SHARE, true);
                intent.putExtra(Constants.Parameter.KEY_TEXT, getShareText());
                mContext.startActivity(intent);
                finish();
            }
        } ;
        chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setText("群组");
                groupRVAdapter = new GroupRVAdapter(0);
//                groupRVAdapter.update(false, groupUpdateCallback);
                groupRVAdapter.setList(IMClient.getInstance().getAllGroups());
                groupRVAdapter.setOnClickListener(onGroupClickListener);
                recentContacts.setAdapter(groupRVAdapter);
                recentContacts.setLayoutManager(new LinearLayoutManager(mContext));
                groupRVAdapter.notifyDataSetChanged();

            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content.setText("查找");
                friendRVAdapter = new FriendRVAdapter();
                recentContacts.setAdapter(friendRVAdapter);
                recentContacts.setLayoutManager(new LinearLayoutManager(mContext));
                friendRVAdapter.setOnItemClickListener(onClickListener);
                friendRVAdapter.setmList(searchFriends(s + ""));
                friendRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private String getShareText() {
        Intent intent = getIntent();
        if (intent == null) {
            return "";
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return "";
        }
        Log.d("fengxiang", "initData: getType:" + intent.getType());

        String type = intent.getType();
        if (!TextUtils.isEmpty(type) && "text/plain".equals(type)) {
            return (String) bundle.get(Intent.EXTRA_TEXT);
        } else {
            return "";
        }

    }

    @Override
    public void onConversationClick(Conversation item, int pos, String name) {
        String chatID = item.getConversationTitle();
        int conversationType = item.getConversationType();
        if (conversationType == 0 || conversationType == 1) {
            if (!TextUtils.isEmpty(chatID)) {
                Intent intent = new Intent(ImApplication.getApp(), TalkActivity.class);
                intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, chatID);
                intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, item.getConversationType());
                intent.putExtra(Constants.Parameter.KEY_RECEIVER_NAME, name);
                intent.putExtra(Constants.Parameter.KEY_IS_NOTIFICATION, false);

                intent.putExtra(Constants.Parameter.KEY_IS_SHARE, true);
                intent.putExtra(Constants.Parameter.KEY_TEXT, getShareText());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onConversationLongClick(Conversation item, int pos) {

    }

    private List<String> searchFriends(String keyWord) {
        List<String> searchResult = new ArrayList<>();
        List<String> friends = new ArrayList<>();
        friends = Db.getInstance().getFriends();
        for(String name : friends) {
            if(name.contains(keyWord)){
                searchResult.add(name);
            }
        }
        return searchResult;
    }

}
