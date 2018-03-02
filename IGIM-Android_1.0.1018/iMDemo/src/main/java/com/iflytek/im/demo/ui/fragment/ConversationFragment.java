package com.iflytek.im.demo.ui.fragment;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.conversation.Conversation;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupMsgContent;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupNotifyKickMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperJoinRespMsg;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.entity.msg.SystemMsgContent;
import com.iflytek.cloud.im.entity.msg.TipMsg;
import com.iflytek.cloud.im.listener.SyncListener;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.ConversationRVAdapter;
import com.iflytek.im.demo.common.NotificationUtil;
import com.iflytek.im.demo.ui.activity.GroupNotifyActivity;
import com.iflytek.im.demo.ui.activity.TalkActivity;
import com.iflytek.im.demo.ui.dialogs.AlertDialog;

import java.util.List;

import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_RECEIVE_TYPE_GROUP;
import static com.iflytek.im.demo.Constants.Event.CREATE_DIS_GROUP_SUCCESS;
import static com.iflytek.im.demo.Constants.Event.CREATE_GROUP_SUCCESS;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_GRP_KICK;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_REMOVE_GROUP;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERJOIN_RESP;
import static com.iflytek.im.demo.Constants.Oper.AGREE;

/**
 * 一个用来显示对话列表的Fragment
 * Created by imxqd on 2016/8/30.
 */
public class ConversationFragment extends BaseFragment implements ConversationRVAdapter.ItemEventCallback {

    private final static String TAG = "ConversationFragment";

    private ConversationRVAdapter mAdapter;

    private NestedScrollView mLlEmpty;

    private RecyclerView mRvChat;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    NotificationUtil.handleSomeoneOnline();
            }
        }
    };


    public ConversationFragment() {
        // Required empty public constructor
    }

    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initMember() {
        mAdapter = new ConversationRVAdapter();
        /*IMClient.getInstance().syncAllMsgs(new SyncListener() {
            @Override
            public void onFailed(String TAG, int errorCode) {
                Log.d("MainActivity", "获取离线消息失败");
            }

            @Override
            public void onMessages(MessageContent... msgs) {
                if (msgs.length != 0) {
                    RxBus.get().post(IMConstants.Event.SYNC_MESSAGE_SUCCESS, msgs);
                    mAdapter.updateData();
                }
            }
        });*/
    }

    @Override
    protected void findViews() {
        mLlEmpty = f(R.id.ll_empty_state_chat_list);
        mRvChat = f(R.id.rv_chats_normal);
    }

    @Override
    protected void initUI() {
        mRvChat.setAdapter(mAdapter);
        mRvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        setVisibilities();
    }

    @Override
    protected void setupEvents() {
        mAdapter.setCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.updateData();
    }

    private void setVisibilities() {
        if (mAdapter.getItemCount() == 0) { // 没有任何聊天
            mLlEmpty.setVisibility(View.VISIBLE);
            mRvChat.setVisibility(View.GONE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mRvChat.setVisibility(View.VISIBLE);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ImApplication.getApp().startActivity(intent);
            }
        } else if (conversationType == 2) {
            Intent intent = new Intent(ImApplication.getApp(), GroupNotifyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ImApplication.getApp().startActivity(intent);
        }

    }

    @Override
    public void onConversationLongClick(final Conversation item, final int pos) {
        Resources res = ImApplication.getApp().getResources();
        String conversationName = item.getConversationTitle();
        if (item.getConversationType() == MESSAGE_CONTENT_RECEIVE_TYPE_GROUP)
            conversationName = IMClient.getInstance().getGroupNameByGid(item.getConversationTitle());
        AlertDialog dialog = new AlertDialog.Builder(res.getColor(R.color.red_700))
                .cancelText(res.getString(R.string.dialog_cancel))
                .confirmText(res.getString(R.string.dialog_confirm))
                .title(res.getString(R.string.dialog_title_delete))
                .content(res.getString(R.string.dialog_delete_content, conversationName))
                .confirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (IMClient.getInstance().delConvSynch(item)) {
                            mAdapter.removeItem(pos);
                            setVisibilities();
                        }

                    }
                })
                .build();
        dialog.show(getFragmentManager(), "delete");
    }


    @Subscribe(tags = {@Tag(Constants.Event.GET_CONVERSATIONS_SUCCESS)})
    public void onGetConversationsSuccess(Object obj) {
        setVisibilities();
        if (mRvChat.getScrollState() == RecyclerView.SCROLL_STATE_IDLE ||
                !mRvChat.isComputingLayout()) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(tags = {@Tag(Constants.Event.NEW_MESSAGE_IN)})
    public void onNewMessageIn(MessageContent msg) {
        Log.d(TAG, "notifyPerson: 消息进来");
        if (msg instanceof CommonMsgContent || msg instanceof GroupMsgContent
                || msg instanceof SystemMsgContent) {
            if (msg != null && !msg.getSender().equals(IMClient.getInstance().getCurrentUser())) {
                notifyPerson(msg);
            }
        }
        if (msg instanceof GroupMsgContent) {
            GroupMsgContent groupMsgContent = (GroupMsgContent) msg;
            handleGroupMsgContent(groupMsgContent);
        }
        if (msg instanceof TipMsg) {
            Log.d(TAG, "onNewMessageIn: tipMsg");
            RxBus.get().post(CREATE_GROUP_SUCCESS, new Object());
            RxBus.get().post(CREATE_DIS_GROUP_SUCCESS, new Object());
        }
        mAdapter.updateData();
    }


    @Subscribe(tags = {@Tag(Constants.Event.ON_NETWORK_STATE_CHANGED)})
    public void onNetWorkStateChanged(NetworkInfo.State state) {
        IMClient.getInstance().syncAllMsgs(new SyncListener() {
            @Override
            public void onFailed(String TAG, int errorCode) {
            }

            @Override
            public void onMessages(MessageContent... msgs) {
                RxBus.get().post(Constants.Event.SYNC_MESSAGE_SUCCESS, msgs);
            }
        });
    }


    public void notifyPerson(MessageContent msg) {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> activityList = am.getRunningTasks(1);
        if (activityList != null && activityList.size() > 0) {
            if (msg instanceof SystemMsgContent) {
                handler.sendEmptyMessage(1);
                return;
            }
            ComponentName cpn = activityList.get(0).topActivity;

            if (TalkActivity.class.getName().equals(cpn.getClassName())) {
                return;

            } else {
                if (msg instanceof CommonMsgContent) {
                    NotificationUtil.notifyPerson((CommonMsgContent) msg, false);
                } else if (msg instanceof GroupMsgContent && !GroupNotifyActivity.class.getName().equals(cpn.getClassName())) {
                    NotificationUtil.notifyGroupMsg((GroupMsgContent) msg);
                }
            }
        }
    }


    @Subscribe(tags = {@Tag(Constants.Event.SYNC_MESSAGE_SUCCESS)})
    public void onSyncMsgSuccess(MessageContent... msg) {
        mAdapter.updateData();
    }

    private void handleGroupMsgContent(GroupMsgContent groupMsgContent) {
        Log.d(TAG, "handleGroupMsgContent: type=" + groupMsgContent.getgMsgType());
        switch (groupMsgContent.getgMsgType()) {
            case MESSAGE_CMD_GRP_NOTYFY_GRP_KICK:
                GroupNotifyKickMsg kickMsg = (GroupNotifyKickMsg) groupMsgContent;
                if (kickMsg.getKicked().equals(IMClient.getInstance().getCurrentUser())) {
                    delConversation(kickMsg.getGid());
                    RxBus.get().post(CREATE_GROUP_SUCCESS, new Object());
                    RxBus.get().post(CREATE_DIS_GROUP_SUCCESS, new Object());
                }
                break;
            case MESSAGE_CMD_GRP_NOTYFY_REMOVE_GROUP:
                delConversation(groupMsgContent.getGid());
                RxBus.get().post(CREATE_GROUP_SUCCESS, new Object());
                RxBus.get().post(CREATE_DIS_GROUP_SUCCESS, new Object());
                break;
            case MESSAGE_CMD_GRP_OPERJOIN_RESP:
                GroupOperJoinRespMsg groupOperJoinRespMsg = (GroupOperJoinRespMsg) groupMsgContent;
                if (groupOperJoinRespMsg.getOper() == AGREE) {
                    RxBus.get().post(CREATE_GROUP_SUCCESS, new Object());
                }
        }
    }


    public void delConversation(String chatID) {
        IMClient.getInstance().delConvById(chatID);
        mAdapter.updateData();
    }

}
