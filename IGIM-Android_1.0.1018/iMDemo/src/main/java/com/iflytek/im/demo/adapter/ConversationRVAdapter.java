package com.iflytek.im.demo.adapter;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.conversation.Conversation;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupMsgContent;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.entity.msg.TipMsg;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ExpressionController;
import com.iflytek.im.demo.common.MsgParseUtil;
import com.iflytek.im.demo.common.TimeStringUtil;

import java.util.ArrayList;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_RECEIVE_TYPE_GROUP;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_RECEIVE_TYPE_GROUP_NOTIFY;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_RECEIVE_TYPE_PERSONAL;

/**
 * 会话列表的adapter
 * Created by imxqd on 2016/8/30.
 */

public class ConversationRVAdapter extends RecyclerView.Adapter<ConversationRVAdapter.ConversationHolder> {

    public static final String TAG = "ConversationRVAdapter";

    private List<Conversation> mList = new ArrayList<>();
    private ItemEventCallback mCallback;

    @Override
    public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversationHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false));
    }

    public void updateData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: start to request conversation");
                List<Conversation> conversations = IMClient.getInstance().getAllConv();
                if (conversations != null) {
                    mList.clear();
                    mList.addAll(sortConversations(conversations));
                    RxBus.get().post(Constants.Event.GET_CONVERSATIONS_SUCCESS, new Object());
                }
            }
        });
    }

    public List<Conversation> sortConversations(List<Conversation> conversations) {
        Conversation temp; // 记录临时中间值
        int size = conversations.size(); // 数组大小
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (conversations.get(i).getLastSendTime() < conversations.get(j).getLastSendTime()) { // 交换两数的位置
                    temp = conversations.get(i);
                    conversations.set(i, conversations.get(j));
                    conversations.set(j, temp);
                }
            }
        }

        return conversations;

    }


    public void remove(int pos) {
        mList.remove(pos);
    }

    public void setCallback(ItemEventCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onBindViewHolder(final ConversationHolder holder, int position) {
        if (mList == null || mList.size() <= position) {
            return;
        }
        Conversation item = mList.get(position);
        if (item.getConversationType() == MESSAGE_CONTENT_RECEIVE_TYPE_GROUP_NOTIFY) {
            holder.icon.setImageResource(R.drawable.group_notification);
            GroupMsgContent lastmsg = (GroupMsgContent) item.getLastMsg();

            holder.message.setText(MsgParseUtil.getGroupMsgContent(lastmsg));

        } else {
            if (item.getConversationType() == MESSAGE_CONTENT_RECEIVE_TYPE_PERSONAL) {
                holder.icon.setImageResource(R.drawable.iv_chat_from);
            } else {
                holder.icon.setImageResource(R.drawable.ic_user_group_48dp);
            }
            MessageContent msg = item.getLastMsg();
            SpannableStringBuilder sb = null;
            if (msg instanceof CommonMsgContent) {
                sb = ExpressionController.expressionBuilder(MsgParseUtil.getCommonMsgContent((CommonMsgContent) msg),
                        holder.itemView.getContext());
            } else {
                sb = ExpressionController.expressionBuilder(MsgParseUtil.getTipMsgContent((TipMsg) msg), holder.itemView.getContext());
            }

            holder.message.setText(sb);
        }
        if (item.getConversationType() == MESSAGE_CONTENT_RECEIVE_TYPE_GROUP) {
            String gid = item.getConversationTitle();
            String groupName = IMClient.getInstance().getGroupNameByGid(gid);
            holder.name.setText(groupName);
        } else {
            holder.name.setText(item.getConversationTitle());
        }


        holder.time.setText(TimeStringUtil.getShortDateTimeString(item.getLastSendTime() * 1000));

        int count = item.getNumOfNotReadMsg();
        if (count == 0) {
            holder.count.setVisibility(View.INVISIBLE);
        } else if (count < 100) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(String.valueOf(count));
        } else {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(ImApplication.getApp().getString(R.string.conversation_item_text_count_more));
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ConversationHolder extends BaseViewHolder {
        ImageView icon;
        TextView name;
        TextView time;
        EmojiconTextView message;
        TextView count;

        public ConversationHolder(final View itemView) {
            super(itemView);
            icon = f(R.id.conversation_item_icon);
            name = f(R.id.conversation_item_name);
            time = f(R.id.conversation_item_time);
            message = f(R.id.conversation_item_message);
            count = f(R.id.conversation_item_not_read_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == -1) {
                        return;
                    }
                    Conversation item = mList.get(position);
                    mCallback.onConversationClick(item, position, name.getText().toString());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mCallback != null) {
                        int position = getAdapterPosition();
                        if (position == -1) {
                            return true;
                        }
                        Conversation item = mList.get(position);
                        mCallback.onConversationLongClick(item, position);
                    }
                    return true;
                }
            });
        }
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        if (position != mList.size() - 1) {
            notifyItemRangeChanged(position, mList.size() - position);
        }
    }


    public interface ItemEventCallback {
        void onConversationClick(Conversation item, int pos, String name);

        void onConversationLongClick(Conversation item, int pos);
    }
}
