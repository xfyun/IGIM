package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.KickGrouperAdapter;
import com.iflytek.im.demo.common.ToastUtil;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iflytek.cloud.im.entity.group.Group.GID;

/**
 * Created by Administrator on 2016/12/22.
 */

public class KickGrouperActivity extends BaseActivity {
    private String mGroupID;
    private RecyclerView membersRV;
    private KickGrouperAdapter kickGrouperAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.kick_grouper;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_group_member,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.finish_adding:
                kickGroupers();
                break;
        }
        return true;
    }

    @Override
    protected void initMembers() {
        super.initMembers();
        setmGroupID();
    }

    @Override
    protected void initViews() {
        super.initViews();
        membersRV = f(R.id.members);
        List<String> members = getGroupMembers();
        if(members != null)
            kickGrouperAdapter = new KickGrouperAdapter(members);
        else
            kickGrouperAdapter = new KickGrouperAdapter();

        membersRV.setAdapter(kickGrouperAdapter);
        membersRV.setLayoutManager(new LinearLayoutManager(this));
        kickGrouperAdapter.notifyDataSetChanged();
        setTitle(IMClient.getInstance().getGroupNameByGid(mGroupID));
    }


    private void kickGroupers(){
        List<String> kickGroupers = kickGrouperAdapter.getmSelectedMemeber();
        if(kickGroupers == null || kickGroupers.size() == 0){
            ToastUtil.showText("请选择要被踢除的成员");
            return;
        }
        Map<String, String> params = new HashMap<>();
        JSONArray jsonArray = new JSONArray(kickGroupers);
        params.put("members", jsonArray.toString());
        params.put("gid", mGroupID);
        IMClient.getInstance().kickGroupers(params, new ResultCallback<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtil.showText("踢除成员成功");
                finish();
            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.showText("踢除成员失败");
            }

        });



    }


    public void setmGroupID() {
        Intent intent = getIntent();
        Bundle bundle = null;
        if(intent != null)
            bundle = intent.getExtras();
        if(bundle != null) {
            mGroupID  = bundle.getString(GID);
        }
    }

    public List<String> getGroupMembers(){
        Group group = IMClient.getInstance().getGroupByGid(mGroupID);
        if(group != null){
            List<String> members = group.getMemeber();
            members.remove(IMClient.getInstance().getCurrentUser());
            return members;
        }else {
            return null;
        }
    }
}
