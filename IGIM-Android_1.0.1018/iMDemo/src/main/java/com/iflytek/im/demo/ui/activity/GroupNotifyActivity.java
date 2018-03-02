package com.iflytek.im.demo.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupMsgContent;
import com.iflytek.cloud.im.entity.msg.MessageConstant;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.GroupNotifyRVAdapter;
import com.iflytek.im.demo.common.MsgParseUtil;
import com.iflytek.im.demo.common.NotificationUtil;

public class GroupNotifyActivity extends BaseActivity  {

    private final static String TAG = "GroupNotifyActivity";

    private RecyclerView mGroupNotifyRV;
    private GroupNotifyRVAdapter mGroupNotifyRVAdapter;
    private UpdateListCallBack mUpdateListCallBack = new UpdateListCallBack();
    private static final String mNotifyGroupMsgID = "AllGroupMsgNotify";



    class UpdateListCallBack implements GroupNotifyRVAdapter.UpdateCallBack {
        @Override
        public void notifyDatachanged() {
            mGroupNotifyRVAdapter.notifyDataSetChanged();
        }

        @Override
        public void popWindow(GroupMsgContent groupMsgContent, GroupNotifyRVAdapter.GroupNotifyHolder holder) {
            popAgreeWindow(groupMsgContent, holder);
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGroupNotifyRVAdapter.updateData();
        NotificationUtil.cancelNotification(mNotifyGroupMsgID);
        mGroupNotifyRVAdapter.sendReadReceipt();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initMembers() {
        mGroupNotifyRVAdapter = new GroupNotifyRVAdapter();
        mGroupNotifyRVAdapter.setUpdateCallBack(mUpdateListCallBack);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.group_notification;
    }

    @Override
    protected void findViews() {
        mGroupNotifyRV = (RecyclerView) findViewById(R.id.group_notify_RV);
    }

    @Override
    protected void initViews() {
        super.initViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("群通知");
        mGroupNotifyRV.setAdapter(mGroupNotifyRVAdapter);
        mGroupNotifyRV.setLayoutManager(new LinearLayoutManager(this));
    }


    public  void   popAgreeWindow(final GroupMsgContent groupMsgContent, final GroupNotifyRVAdapter.GroupNotifyHolder holder){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("群通知");
        builder.setMessage(MsgParseUtil.getGroupMsgContent(groupMsgContent));
        int cmd = groupMsgContent.getgMsgType();
        if((cmd == Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERINVITE
                || cmd == Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERJOIN
                || cmd == Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERVERIFY)
                && groupMsgContent.getHandleStatus() == MessageConstant.MESSAGE_CONTENT_UNHANDLED){

            builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mGroupNotifyRVAdapter.handleApplicant(groupMsgContent,holder,true);
                }
            });
            builder.setNegativeButton("拒绝",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mGroupNotifyRVAdapter.handleApplicant(groupMsgContent,holder,false);
                }
            });
        }
        builder.show();

    }


    private void removeNotReadNum() {
        IMClient.getInstance().updateGroupMsgAsReaded();
    }

    @Override
    protected void onPause() {
        removeNotReadNum();
        mGroupNotifyRVAdapter.sendReadReceipt();
        super.onPause();
    }

    @Subscribe(tags = {@Tag(Constants.Event.NEW_MESSAGE_IN)})
    public void onNewMessageIn(MessageContent msgContent) {
        if(msgContent instanceof GroupMsgContent){
            mGroupNotifyRVAdapter.updateData();
        }
    }




}
