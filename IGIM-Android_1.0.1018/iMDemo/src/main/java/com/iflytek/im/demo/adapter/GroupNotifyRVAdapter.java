package com.iflytek.im.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupMsgContent;
import com.iflytek.cloud.im.entity.msg.MessageConstant;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.DisplayUtil;
import com.iflytek.im.demo.common.MsgParseUtil;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 会话列表的adapter
 * Created by imxqd on 2016/8/30.
 */

public class GroupNotifyRVAdapter extends RecyclerView.Adapter<GroupNotifyRVAdapter.GroupNotifyHolder> {

    public static final String TAG = "GroupNotifyRVAdapter";

    private List<GroupMsgContent> mList = new ArrayList<>();

    private UpdateCallBack mUpdateCallBack;
    private MessageContent mNewestMsg = new MessageContent();


    public interface UpdateCallBack {
        void notifyDatachanged();

        void popWindow(GroupMsgContent groupMsgContent, GroupNotifyHolder holder);
    }


    public void setUpdateCallBack(UpdateCallBack mUpdateCallBack) {
        this.mUpdateCallBack = mUpdateCallBack;
    }

    @Override
    public GroupNotifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupNotifyHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GroupNotifyHolder holder, int position) {
        final GroupMsgContent groupMsgContent = mList.get(position);
        String groupName = IMClient.getInstance().getGroupNameByGid(groupMsgContent.getGid());
        if (groupName == null) {
            groupName = groupMsgContent.getGid();
        }

        holder.mApplicantName.setText(groupName);
        holder.mApplicantContent.setText(MsgParseUtil.getGroupMsgContent(groupMsgContent));
        int cmd = groupMsgContent.getgMsgType();
        holder.mApplicantContent.setMaxWidth(DisplayUtil.getScreenWidth() / 2);
        if (groupMsgContent.getHandleStatus() == MessageConstant.MESSAGE_CONTENT_HANDLED) {
            holder.mAgreeApplicant.setVisibility(View.GONE);
            holder.mStatus.setVisibility(View.VISIBLE);
            holder.mStatus.setText("已同意");
        } else if (groupMsgContent.getHandleStatus() == MessageConstant.MESSAGE_CONTENT_INVALID) {
            holder.mAgreeApplicant.setVisibility(View.GONE);
            holder.mStatus.setText("已失效");
            holder.mStatus.setVisibility(View.VISIBLE);
        } else if (groupMsgContent.getHandleStatus() == MessageConstant.MESSAGE_CONTENT_UNHANDLED) {
            holder.mAgreeApplicant.setVisibility(View.VISIBLE);
            holder.mStatus.setVisibility(View.GONE);
        } else if (groupMsgContent.getHandleStatus() == MessageConstant.MESSAGE_CONTENT_REFUSED) {
            holder.mAgreeApplicant.setVisibility(View.GONE);
            holder.mStatus.setText("已拒绝");
            holder.mStatus.setVisibility(View.VISIBLE);
        }
        if (cmd != Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERINVITE
                && cmd != Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERJOIN
                && cmd != Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERVERIFY) {
            holder.mAgreeApplicant.setVisibility(View.GONE);
            holder.mStatus.setVisibility(View.GONE);
            holder.mApplicantContent.setMaxWidth(DisplayUtil.getScreenWidth());
        }


        holder.mApplicantMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdateCallBack.popWindow(groupMsgContent, holder);
            }
        });


        holder.mAgreeApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApplicant(groupMsgContent, holder, true);
            }
        });


    }

    public void updateData() {
        mList.clear();
        mList.addAll(IMClient.getInstance().queryAllGroupNotifyMsg());
        if (mList.size() > 0 && mList.get(0).getSeqID() > mNewestMsg.getSeqID()) {
            mNewestMsg = mList.get(0);
        }
        mUpdateCallBack.notifyDatachanged();
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class GroupNotifyHolder extends BaseViewHolder {
        TextView mApplicantName, mApplicantContent, mStatus;
        ImageView mApplicantIcon;
        Button mAgreeApplicant;
        LinearLayout mApplicantMessageLL;


        public GroupNotifyHolder(final View itemView) {
            super(itemView);
            mApplicantMessageLL = f(R.id.applicant_message);
            mAgreeApplicant = f(R.id.agree_applicant);
            mApplicantContent = f(R.id.applicant_content);
            mApplicantName = f(R.id.applicant_name);
            mApplicantIcon = f(R.id.applicant_icon);
            mStatus = f(R.id.agreed_join);
        }


    }

    public void handleApplicant(final GroupMsgContent groupMsgContent, final GroupNotifyHolder holder, final boolean oper) {

        IMClient.getInstance().handleRequest(groupMsgContent, oper, new ResultCallback<String>() {
            @Override
            public void onError(int errorCode) {
                ToastUtil.showText("该请求已失效");
                holder.mAgreeApplicant.setVisibility(View.GONE);
                holder.mStatus.setText("已失效");
                holder.mStatus.setVisibility(View.VISIBLE);
                updateData();
                mUpdateCallBack.notifyDatachanged();
            }

            @Override
            public void onSuccess(String datas) {
                //同意成功或拒绝成功
                if (!oper) {
                    holder.mAgreeApplicant.setVisibility(View.GONE);
                    holder.mStatus.setText("已拒绝");
                    holder.mStatus.setVisibility(View.VISIBLE);
                    updateData();
                    mUpdateCallBack.notifyDatachanged();
                } else {
                    holder.mAgreeApplicant.setVisibility(View.GONE);
                    holder.mStatus.setText("已同意");
                    holder.mStatus.setVisibility(View.VISIBLE);
                    updateData();
                    mUpdateCallBack.notifyDatachanged();
                }
            }
        });


    }

    public void sendReadReceipt() {
        if (mNewestMsg == null) {
            return;
        }
//        long seqID = mNewestMsg.getSeqID();
//        boolean isGroup = false;
//        IMClient.getInstance().sendReadReceipt(null, true, isGroup, seqID);
        IMClient.getInstance().sendReadReceipt(mNewestMsg, 10 * 1000);

    }
}
