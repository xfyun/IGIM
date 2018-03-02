package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.GroupInfoLvAdapter;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MoreGroupInfoActivity extends BaseActivity {
    private static final String TAG = "MoreGroupInfoActivity";
    private static String mGroupID;
    private ListView mGroupMemberLv;
    private TextView mGidTextVeiw, mOwnerTextView;
    private ArrayList<String> mGroupManagers,mGroupMembers;
    private boolean firstLoading;
    private Group mGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firstLoading = true;
        mGroupID = this.getIntent().getStringExtra(Constants.Parameter.KEY_RECEIVER_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.moregroupinfo;
    }

    @Override
    protected void findViews() {
        mGroupMemberLv = (ListView) findViewById(R.id.groupDetail);
        mGidTextVeiw = (TextView) findViewById(R.id.gid_textview);
        mOwnerTextView = (TextView) findViewById(R.id.group_holder_textview);
    }

    @Override
    protected void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(IMClient.getInstance().getGroupNameByGid(mGroupID));
    }

    @Override
    protected void initMembers() {
        super.initMembers();
        mGroupManagers = new ArrayList<>();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void setupEvents() {

    }

    @Override
    protected void onResume() {
        getGroupInfo();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: id=" + id);
        if (id == R.id.exitGroup) {
            exitGroup();
        } else if (id == R.id.addGroup) {
            addGroup();
        } else if (id == R.id.removeGroup) {
            removeGroup();
        } else if (id == R.id.kick_grouper) {
            kickGrouper();
        } else if (id == R.id.appoint_mgr) {
            appointMgr();
        }else if( id == android.R.id.home){
            finish();
        }else if ( id == R.id.transfer_owner) {
            transferOwner();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: firstloading:" + firstLoading);
        if(!firstLoading) {
            if (!isGroupManager() && !isGroupOwner()) {
                menu.findItem(R.id.kick_grouper).setEnabled(false);
                menu.findItem(R.id.removeGroup).setEnabled(false);
                menu.findItem(R.id.appoint_mgr).setEnabled(false);
                menu.findItem(R.id.transfer_owner).setEnabled(false);
            } else if (!isGroupOwner()) {
                menu.findItem(R.id.removeGroup).setEnabled(false);
                menu.findItem(R.id.appoint_mgr).setEnabled(false);
                menu.findItem(R.id.transfer_owner).setEnabled(false);
            }
        }else {
            firstLoading = false;
        }
        return true;
    }

    private void getGroupInfo() {
        Map<String, String> getGroupInfoParams = new HashMap<>();
        getGroupInfoParams.put(Group.GID, mGroupID);
        IMClient.getInstance().getGroupInfo(getGroupInfoParams,
                new ResultCallback<Group>() {

                    @Override
                    public void onError(int errorCode) {
                        ToastUtil.showText("获取群组成员失败");
                        Log.i(TAG, "获取群组成员失败,errorcode = " + errorCode);
                    }

                    @Override
                    public void onSuccess(Group group) {
                        Log.i(TAG, "获取群组成员成功");
                        mGroup = group;
                        List<String> groupMembers;
                        groupMembers = group.getMemeber();
                        if (groupMembers == null) {
                            return;
                        }
                        mGroupMembers = (ArrayList<String>) groupMembers;
                        mGroupManagers = (ArrayList<String>) group.getManagers();
                        GroupInfoLvAdapter adapter = new GroupInfoLvAdapter(
                                MoreGroupInfoActivity.this, groupMembers, mGroupManagers, mGroupID);
                        mGroupMemberLv.setAdapter(adapter);
                        mGidTextVeiw.setText(group.getGid());
                        mOwnerTextView.setText(group.getOwner());
                    }
                });
    }

    private boolean isGroupOwner() {
        String owner = mGroup.getOwner();
        String uid = IMClient.getInstance().getCurrentUser();
        return owner.equals(uid);
    }

    private boolean isGroupManager() {
        String uid = IMClient.getInstance().getCurrentUser();
        if (mGroup == null) {
            Log.d(TAG, "isGroupManager: type null");
            return false;
        }
        List<String> managers;
        managers = mGroup.getManagers();

        if (managers != null) {
            return managers.contains(uid);
        } else {
            return false;
        }

    }


    private void exitGroup() {
        Map<String, String> exitGroupParams = new HashMap<String, String>();
        exitGroupParams.put(Group.GID, mGroupID);
        IMClient.getInstance().exitFromGroup(exitGroupParams,
                new ResultCallback<String>() {

                    @Override
                    public void onSuccess(String data) {
                        ToastUtil.showText("退出群组成功");
                        setResult(Constants.RequestCodeAndResultCode.EXIT_GROUP_RESULTCODE);
                        MoreGroupInfoActivity.this.finish();
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.e(TAG, "退出群组失败");
                    }

                });


    }

    private void addGroup() {
        Intent intent = new Intent(MoreGroupInfoActivity.this, GroupMemberAddActivity.class);
        intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, mGroupID);
        startActivity(intent);
    }

    private void removeGroup() {
        Map<String, String> params = new HashMap<>();
        params.put(Group.GID, mGroupID);
        IMClient.getInstance().removeGroup(params,
                new ResultCallback<String>() {
                    @Override
                    public void onError(int errorCode) {
                        ToastUtil.showText("解散群组失败");
                    }

                    @Override
                    public void onSuccess(String datas) {
                        ToastUtil.showText("解散群组成功");
                        setResult(Constants.RequestCodeAndResultCode.EXIT_GROUP_RESULTCODE);
                        MoreGroupInfoActivity.this.finish();
                    }
                });
    }

    private void kickGrouper() {
        Intent intent = new Intent(MoreGroupInfoActivity.this, KickGrouperActivity.class);
        intent.putExtra(Group.GID, mGroupID);
        startActivity(intent);
    }

    private void appointMgr() {
        Intent intent = new Intent(MoreGroupInfoActivity.this, SetGroupManagerActivity.class);
        intent.putStringArrayListExtra(Group.MANAGERS, mGroupManagers);
        intent.putExtra(Group.GID, mGroupID);
        startActivity(intent);
    }

    private void transferOwner() {
        Intent intent = new Intent(MoreGroupInfoActivity.this, TransferOwnerActivity.class);
        Log.d(TAG, "transferOwner: mGroupMembers:" + mGroupMembers);
        intent.putStringArrayListExtra(Group.MEMBERS, mGroupMembers);
        intent.putExtra(Group.GID, mGroupID);
        startActivity(intent);
    }


}
