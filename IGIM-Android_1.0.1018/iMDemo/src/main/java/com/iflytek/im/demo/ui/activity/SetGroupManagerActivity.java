package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.FriendRVAdapter;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iflytek.im.demo.Constants.RequestCodeAndResultCode.ADD_GROUPMANAGER_REQUESTCODE;
import static com.iflytek.im.demo.Constants.RequestCodeAndResultCode.ADD_GROUPMANAGER_RESULTCODE;
import static com.iflytek.im.demo.Constants.TokenParams.RET;

/**
 * Created by admin on 2017/1/5.
 */

public class SetGroupManagerActivity extends BaseActivity {
    private LinearLayout mManagerAddLL;
    private RecyclerView mManagerRV;
    private Button mManagerAddBtn;
    private FriendRVAdapter mManagerRvAdapter;
    private String mGroupID;

    private ArrayList<String> mManagers;

    @Override
    protected int getLayoutRes() {
        return R.layout.set_group_manager;
    }

    @Override
    protected void initMembers() {
        super.initMembers();
        mManagers = new ArrayList<>();
        mManagerRvAdapter = new FriendRVAdapter();
        getGroupManagers();
        mManagerRvAdapter.setmList(mManagers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_group_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                mManagerRvAdapter.setDelIconVisiable();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mManagerRV = f(R.id.managers);
        mManagerAddLL = f(R.id.add_manager);
        mManagerAddBtn = f(R.id.add_manager_btn);
        mManagerRV.setAdapter(mManagerRvAdapter);
        mManagerRV.setLayoutManager(new LinearLayoutManager(this));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(IMClient.getInstance().getGroupNameByGid(mGroupID));

    }

    @Override
    protected void setupEvents() {
        super.setupEvents();
        mManagerAddLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addManager();
            }
        });
        mManagerAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addManager();
            }
        });
        mManagerRvAdapter.setRemoveCallBack(new FriendRVAdapter.RemoveCallBack() {
            @Override
            public void remove(final String name) {
                Map<String, Object> params = new HashMap<>();
                params.put(Group.GID, mGroupID);
                final List<String> managers = new ArrayList<>();
                managers.add(name);
                params.put(Group.MANAGERS, managers);
                IMClient.getInstance().removeManager(params, new ResultCallback<String>() {
                    @Override
                    public void onError(int errorCode) {
                        ToastUtil.showText("移除失败");
                    }

                    @Override
                    public void onSuccess(String datas) {
                        int position = mManagers.indexOf(name);
                        mManagers.remove(position);
                        mManagerRvAdapter.setmList(mManagers);
                        mManagerRvAdapter.notifyItemRemoved(position);
                    }
                });
            }
        });
    }

    private List<String> getGroupManagers() {
        mManagers = new ArrayList<>();
        Intent intent = getIntent();
        mManagers = intent.getStringArrayListExtra(Group.MANAGERS);
        mGroupID = intent.getStringExtra(Group.GID);
        return mManagers;
    }

    private void addManager() {
        Intent intent = new Intent(SetGroupManagerActivity.this, AddGroupManagerActivity.class);
        intent.putExtra(Group.GID, mGroupID);
        intent.putStringArrayListExtra(Group.MANAGERS, mManagers);
        startActivityForResult(intent, ADD_GROUPMANAGER_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ADD_GROUPMANAGER_RESULTCODE) {
            Bundle bundle = data.getExtras();
            int ret = bundle.getInt(RET);
            if (ret == 0) {
                List<String> newManagers = data.getStringArrayListExtra(Group.MANAGERS);
                mManagers.addAll(newManagers);
                mManagerRvAdapter.setmList(mManagers);
                mManagerRvAdapter.notifyDataSetChanged();
            }
        }
    }
}
