package com.iflytek.im.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.im.demo.R;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoLvAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<String> mGroupMembers = new ArrayList<>();
	private List<String> mManagers = new ArrayList<>();
	private String mGID;

	public GroupInfoLvAdapter(Context context, List<String> groups, List<String> managers, String gid) {
		super();
		this.mInflater = LayoutInflater.from(context);
		this.mGroupMembers = groups;
		this.mManagers = managers;
		this.mGID = gid;
	}

	@Override
	public int getCount() {
		return mGroupMembers.size();
	}

	@Override
	public Object getItem(int position) {
		return mGroupMembers.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friend_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String groupMemberName = mGroupMembers.get(position);
		holder.groupMemberNameTv.setText(groupMemberName);
		if(isManager(groupMemberName)){
			holder.managerHint.setVisibility(View.VISIBLE);
		}else{
			holder.managerHint.setVisibility(View.GONE);
		}
		if(isGroupOwner(groupMemberName)) {
			holder.managerHint.setText("群主");
			holder.managerHint.setVisibility(View.VISIBLE);
		}else{
			holder.managerHint.setText("管理员");
		}

		return convertView;
	}

	public class ViewHolder {
		TextView groupMemberNameTv;
		TextView managerHint;

		public ViewHolder(View view) {
			groupMemberNameTv = (TextView) view.findViewById(R.id.friendName);
			managerHint = (TextView) view.findViewById(R.id.manager_hint);
		}
	}

	private boolean isManager(String name) {
		if(mManagers != null) {
			return mManagers.contains(name);
		}
		return false;
	}

	private boolean isGroupOwner(String name) {
		String owner = IMClient.getInstance().getGroupOwnerByGid(mGID);
		return owner.equals(name);
	}

}
