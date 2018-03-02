package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.KickGrouperAdapter;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iflytek.cloud.im.entity.group.Group.GID;
import static com.iflytek.cloud.im.entity.group.Group.MANAGERS;
import static com.iflytek.im.demo.Constants.RequestCodeAndResultCode.ADD_GROUPMANAGER_RESULTCODE;
import static com.iflytek.im.demo.Constants.TokenParams.RET;

/**
 * Created by admin on 2017/1/5.
 */

public class AddGroupManagerActivity extends BaseActivity {
    private String mGroupID;
    private ArrayList<String> mManagers;
    private RecyclerView membersRV;
    private KickGrouperAdapter addGroupManagerAdapter;


    @Override
    protected int getLayoutRes() {
        return R.layout.kick_grouper;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_group_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish_adding:
                addGroupManager();
                break;
        }
        return true;
    }


    private void addGroupManager() {
        final ArrayList<String> managers = (ArrayList<String>) addGroupManagerAdapter.getmSelectedMemeber();
        Map<String, Object> params = new HashMap<>();
        params.put(Group.GID, mGroupID);
        params.put(Group.MANAGERS, managers);
        Log.d("addgroupmanageractivity", "addGroupManager: gid:" + mGroupID + " managers:" + managers);
        IMClient.getInstance().appointManager(params, new ResultCallback<String>() {
            @Override
            public void onError(int errorCode) {
                ToastUtil.showText("添加管理员失败");
            }

            @Override
            public void onSuccess(String datas) {
                ToastUtil.showText("添加管理员成功");
                Intent intent = new Intent();
                intent.putExtra(RET,0);
                intent.putStringArrayListExtra(Group.MANAGERS,managers);
                setResult(ADD_GROUPMANAGER_RESULTCODE, intent);
                finish();
            }
        });
    }


    @Override
    protected void initMembers() {
        super.initMembers();
        setGroup();
    }

    @Override
    protected void initViews() {
        super.initViews();
        membersRV = f(R.id.members);
        List<String> members = getGroupMembersToAdd();
        if (members != null)
            addGroupManagerAdapter = new KickGrouperAdapter(members);
        else
            addGroupManagerAdapter = new KickGrouperAdapter();

        membersRV.setAdapter(addGroupManagerAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));
    }


    private List<String> getGroupMembersToAdd() {
        Group group = IMClient.getInstance().getGroupByGid(mGroupID);
        if (group != null) {
            List<String> members = group.getMemeber();
            members.remove(IMClient.getInstance().getCurrentUser());
            for (String name : mManagers) {
                members.remove(name);
            }
            return members;
        } else {
            return null;
        }
    }

    private void setGroup() {
        Intent intent = getIntent();
        Bundle bundle = null;
        if (intent != null)
            bundle = intent.getExtras();
        if (bundle != null) {
            mGroupID = bundle.getString(GID);
            mManagers = bundle.getStringArrayList(MANAGERS);
        }
    }

}
