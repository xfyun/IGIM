package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.RecyclerViewAdapter;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/1/9.
 */

public class TransferOwnerActivity extends BaseActivity {
    private TextView mGroupMemberHeader;
    private RecyclerView mGroupMember;
    private RecyclerViewAdapter mTransferOwnerAdapter;

    private String mGID;
    private List<String> mMembers;




    @Override
    protected int getLayoutRes() {
        return R.layout.seach_result;
    }


    @Override
    protected void initViews() {
        mGroupMemberHeader = f(R.id.recyclerview_header);
        mGroupMemberHeader.setText("请选择群主的下一任接班人：");
        mGroupMember = f(R.id.search_result_rv);
        mGroupMember.setAdapter(mTransferOwnerAdapter);
        mGroupMember.setLayoutManager(new LinearLayoutManager(this));
        setTitle(IMClient.getInstance().getGroupNameByGid(mGID));

    }


    @Override
    protected void initMembers() {
        super.initMembers();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mGID = bundle.getString(Group.GID);
        mMembers = bundle.getStringArrayList(Group.MEMBERS);
        if(mMembers != null){
            mMembers.remove(IMClient.getInstance().getCurrentUser());
        }
        mTransferOwnerAdapter = new RecyclerViewAdapter(mMembers);
        mTransferOwnerAdapter.setClickCallBack(new RecyclerViewAdapter.ClickCallBack() {
            @Override
            public void clickCallBack(String name) {
                transferOwner(name);
            }
        });
    }


    private void transferOwner(String name) {
        Map<String, String> params = new HashMap<>();
        params.put(Group.GID, mGID);
        params.put(Group.OWNER, name);
        IMClient.getInstance().transferOwner(params, new ResultCallback<String>() {
            @Override
            public void onError(int errorCode) {
                ToastUtil.showText("移交群主失败");
            }

            @Override
            public void onSuccess(String datas) {
                ToastUtil.showText("移交群主成功");
                finish();
            }
        });
    }

}
