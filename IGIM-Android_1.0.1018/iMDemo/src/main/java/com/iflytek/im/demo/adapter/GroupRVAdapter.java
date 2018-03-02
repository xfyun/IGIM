package com.iflytek.im.demo.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.cloud.im.listener.SyncListener;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.ui.activity.TalkActivity;

import java.util.ArrayList;
import java.util.List;

import static com.iflytek.im.demo.Constants.Event.NEW_MESSAGE_IN;

/**
 * 群组列表界面的RecyclerView的适配器
 * Created by imxqd on 2016/9/1.
 */

public class GroupRVAdapter extends RecyclerView.Adapter<GroupRVAdapter.GroupHolder> {
    private static final String TAG = "GroupRVAdapter";


    private List<Group> mList;
    private List<Group> mGroupList;
    private List<Group> mSmallGroupList;
    private int mGroupType;

    private GroupOnClickListener onClickListener;



    public interface GroupOnClickListener{
        void onGroupClick(int position);
    }



    public interface UpdateCallback {
        void onComplete(List<Group> groups);
    }

    public void setOnClickListener(GroupOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public GroupRVAdapter(int type) {
        // 0表示讨论组 1表示群组
        // TODO: 2016/9/1  改为常量名（目前SDK还没有）
        mGroupType = type;
        mList = new ArrayList<>();
        mGroupList = new ArrayList<>();
        mSmallGroupList = new ArrayList<>();
    }

    public void update(final boolean needToSync, final UpdateCallback callback) {
        IMClient.getInstance().getGroupList(new ResultCallback<List<Group>>() {
            @Override
            public void onError(int errorCode) {
                Log.e(TAG, "getGroupList error, errorCode:" + errorCode);
                callback.onComplete(null);
            }

            @Override
            public void onSuccess(List<Group> datas) {
                mGroupList.clear();
                mSmallGroupList.clear();
                for (Group data : datas) {
                    if (data.getType() == 1) {
                        mGroupList.add(data);
                    } else {
                        mSmallGroupList.add(data);
                    }
                }
                if(mGroupType == 0) {
                    //讨论组
                    callback.onComplete(mSmallGroupList);
                }else {
                    //群组
                    callback.onComplete(mGroupList);
                }

                Log.d(TAG, "notifyPerson  以外的消息过来了");
                RxBus.get().post(NEW_MESSAGE_IN, new MessageContent());

                if (needToSync) {
                    IMClient.getInstance().syncAllMsgs(new SyncListener() {
                        @Override
                        public void onFailed(String TAG, int errorCode) {
                        }

                        @Override
                        public void onMessages(MessageContent... msgs) {
                            if (msgs.length != 0) {
                                RxBus.get().post(Constants.Event.SYNC_MESSAGE_SUCCESS, msgs);
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        Group group = mList.get(position);
        holder.icon.setImageResource(R.drawable.ic_user_group_48dp);
        holder.title.setText(group.getgName());
        holder.subtitle.setText(group.getDescribe());
    }

    @Override
    public int getItemCount() {
        if(mList == null) {
            mList = new ArrayList<>();
        }
        return mList.size();
    }

    class GroupHolder extends BaseViewHolder {
        ImageView icon;
        TextView title;
        TextView subtitle;

        public GroupHolder(final View itemView) {
            super(itemView);
            icon = f(R.id.group_item_icon);
            title = f(R.id.group_item_title);
            subtitle = f(R.id.group_item_subtitle);
            if (onClickListener == null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Group group = mList.get(getAdapterPosition());
                        Intent intent = new Intent(itemView.getContext(), TalkActivity.class);
                        intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, group.getGid());
                        intent.putExtra(Constants.Parameter.KEY_RECEIVER_NAME, group.getgName());
                        intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, 1);
                        itemView.getContext().startActivity(intent);
                    }
                });

            } else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onGroupClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    public List<Group> getmGroupList() {
        return mGroupList;
    }

    public List<Group> getmSmallGroupList() {
        return mSmallGroupList;
    }

    public void setList(List<Group> mList) {
        this.mList = mList;
    }
}
