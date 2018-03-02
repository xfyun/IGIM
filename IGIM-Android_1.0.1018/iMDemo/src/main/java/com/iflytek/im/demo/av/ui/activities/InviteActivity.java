/*
package com.iflytek.im.demo.av.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.BaseViewHolder;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InviteActivity extends BaseActivity {

    private static final String TAG = "InviteActivity";

    public static final String PRARM_MAX_COUNT = "max_count";
    public static final String PRARM_EXCEPT_USERS = "except_users";
    public static final String PRARM_GROUP_ID = "group_id";

    public static final String RESULT_USERS = "result_users";

    private RecyclerView mRvUserList;
    private FriendRVAdapter mAdapter;

    private ArrayList<String> mExceptUserList;
    private int mMaxCount = 0;
    private String mGroupId = "10000";

    public static void startForResult(Activity activity, int requestCode, int maxCount,
                                      String groupId,
                                      @Nullable ArrayList<String> exceptUserList) {
        Intent intent = new Intent(activity, InviteActivity.class);
        intent.putExtra(PRARM_MAX_COUNT, maxCount);
        intent.putExtra(PRARM_GROUP_ID, groupId);
        intent.putExtra(PRARM_EXCEPT_USERS, exceptUserList);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_invite;
    }

    @Override
    protected void findViews() {
        mRvUserList = f(R.id.invite_user_list);
    }

    @Override
    protected void initMembers() {
        mAdapter = new FriendRVAdapter();
        mExceptUserList = getIntent().getStringArrayListExtra(PRARM_EXCEPT_USERS);
        mMaxCount = getIntent().getIntExtra(PRARM_MAX_COUNT, 0);
        mGroupId = getIntent().getStringExtra(PRARM_GROUP_ID);

        if (mExceptUserList == null) {
            mExceptUserList = new ArrayList<>(0);
        }
    }

    @Override
    protected void initViews() {
        mRvUserList.setAdapter(mAdapter);
        mRvUserList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.update(mGroupId);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("0/" + mMaxCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            Intent data = new Intent();
            data.putExtra(RESULT_USERS, mAdapter.getSelected());
            setResult(RESULT_OK, data);
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return true;
    }


    public class FriendRVAdapter extends RecyclerView.Adapter<FriendRVAdapter.FriendHolder> {

        private List<String> mList;
        private ArrayMap<String, Boolean> mSelectedMap;
        private int mSelectedCount = 0;

        public FriendRVAdapter() {
            mList = new ArrayList<>();
            mSelectedMap = new ArrayMap<>();
        }


        public void update(String groupId) {
            Map<String, String> getGroupInfoParams = new HashMap<>();
            getGroupInfoParams.put(Group.GID, groupId);
            IMClient.getInstance().getGroupInfo(getGroupInfoParams,
                    new ResultCallback<Group>() {

                        @Override
                        public void onError(int errorCode) {
                            Log.e(TAG, "onError: " + errorCode);
                            Toast.makeText(InviteActivity.this, "Error:" + errorCode, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Group group) {
                            List<String> users = group.getMemeber();
                            mList.clear();
                            mSelectedMap.clear();
                            users.removeAll(mExceptUserList);
                            users.remove(IMClient.getInstance().getCurrentUser());
                            mList.addAll(users);
                            for (String s : mList) {
                                mSelectedMap.put(s, false);
                            }
                            notifyDataSetChanged();
                        }
                    });
        }


        public ArrayList<String> getSelected() {
            ArrayList<String> list = new ArrayList<>();
            Iterator<Map.Entry<String, Boolean>> iterator = mSelectedMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Boolean> item = iterator.next();
                if (item.getValue()) {
                    list.add(item.getKey());
                }
            }
            return list;
        }

        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FriendHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend, parent, false));
        }

        @Override
        public void onBindViewHolder(FriendRVAdapter.FriendHolder holder, int position) {

            String user = mList.get(position);
            if (!mSelectedMap.get(user)) {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.invite_item_bg));
            } else {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.invite_item_bg_selected));
            }
            holder.icon.setImageResource(R.drawable.iv_chat_from);
            holder.text.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class FriendHolder extends BaseViewHolder {
            ImageView icon;
            TextView text;

            public FriendHolder(final View itemView) {
                super(itemView);
                icon = f(R.id.friend_item_icon);
                text = f(R.id.friend_item_text);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        String user = mList.get(pos);
                        if (!mSelectedMap.get(user)) {
                            if (mSelectedCount < mMaxCount) {
                                mSelectedMap.put(user, true);
                                mSelectedCount++;
                            } else {
                                ToastUtil.showText("最多只能选择" + mMaxCount + "个");
                            }
                        } else {
                            mSelectedMap.put(user, false);
                            mSelectedCount--;
                        }
                        setTitle(mSelectedCount + "/" + mMaxCount);
                        notifyItemChanged(pos);
                    }
                });
            }
        }
    }

}
*/
