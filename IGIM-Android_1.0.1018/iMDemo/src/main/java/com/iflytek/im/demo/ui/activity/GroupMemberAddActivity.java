package com.iflytek.im.demo.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.MemberAddLvAdapter;
import com.iflytek.im.demo.adapter.MemberAddLvAdapter.ViewHolder;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.dao.Db;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMemberAddActivity extends BaseActivity {
	private static final String TAG = "AddMemberActivity";
	private static String GROUP_ID;

	private static final String KEY_MSG = "msg";

	private EditText mMsgEdt;
	private ListView mFriendLv;
	private List<String> mSelectedFriends;
	private MemberAddLvAdapter mMemberAddAdapter;
	private List<String> mGroupMemberList;// 好友中已经在这个群里的人

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GROUP_ID = this.getIntent().getStringExtra(Constants.Parameter.KEY_RECEIVER_ID);
		setContentView(R.layout.memberadd);
		initView();
		initData();
		initListener();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.memberadd;
	}

	@Override
	protected void onResume() {
		super.onResume();
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
				addMembers();
				break;
		}
		return true;
	}

	private void initView() {
		mMsgEdt = (EditText) findViewById(R.id.msg);
		mFriendLv = (ListView) findViewById(R.id.members);
		setTitle(IMClient.getInstance().getGroupNameByGid(GROUP_ID));
	}

	private void initData() {
		mSelectedFriends = new ArrayList<>();
		List<String> friends = Db.getInstance().getFriends();
		mGroupMemberList = new ArrayList<>();
		try{
			// 获取已经在群里的用户
			Group group = IMClient.getInstance().getGroupByGid(GROUP_ID);
			if( group != null ){
                mGroupMemberList = group.getMemeber();
			}
            mMemberAddAdapter = new MemberAddLvAdapter(friends,
					GroupMemberAddActivity.this, mGroupMemberList);
			mFriendLv.setAdapter(mMemberAddAdapter);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public void addMembers(){
        String msgContent = mMsgEdt.getText().toString();
		Map<String, String> GroupAddParams = new HashMap<>();
		// 这个group应该是通过点击事件传进来的 暂时是写死的
		GroupAddParams.put(Group.GID, GROUP_ID);

		GroupAddParams.put("uid", IMClient.getInstance().getCurrentUser());
 		Group group = IMClient.getInstance().getGroupByGid(GROUP_ID);
		if(group == null) {
			ToastUtil.showText("您已被移除该群");
			return;
		}
		String type = group.getType() + "";
		GroupAddParams.put(Group.TYPE,type);
		GroupAddParams.put(KEY_MSG, msgContent);
		JSONArray groupMemberArray = new JSONArray();
		// 取出选择的联系人
		for (String friendName : mSelectedFriends) {
			groupMemberArray.put(friendName);
		}
		GroupAddParams.put(Group.MEMBERS, groupMemberArray.toString());
		IMClient.getInstance().addMemToGroup(GroupAddParams,
				new ResultCallback<String>() {

					@Override
					public void onSuccess(String data) {
						Log.i(TAG, "添加群成员成功");
						finish();
                        ToastUtil.showText("添加群成员成功");
					}

					@Override
					public void onError(int errorCode) {
						Log.i(TAG, "添加群成员失败");
                        ToastUtil.showText("添加群成员失败");
					}

				});

		dataChanged();
	}


	private void initListener() {

		mFriendLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String selectedName = (String) arg0.getItemAtPosition(arg2);
				if (mGroupMemberList == null || mGroupMemberList.contains(selectedName)) {
					return;
				}
				ViewHolder holder = (ViewHolder) arg1.getTag();
				holder.selectionChk.toggle();

				if(mSelectedFriends.contains(selectedName)){
					mSelectedFriends.remove(selectedName);
                    mMemberAddAdapter.isSelected().put(arg2,false);
				}else{
					mSelectedFriends.add(selectedName);
                    mMemberAddAdapter.isSelected().put(arg2,true);
				}
			}
		});
	}

	private void dataChanged() {
		mMemberAddAdapter.notifyDataSetChanged();
	};
}
