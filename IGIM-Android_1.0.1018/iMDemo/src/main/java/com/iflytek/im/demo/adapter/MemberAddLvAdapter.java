package com.iflytek.im.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.iflytek.im.demo.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemberAddLvAdapter extends BaseAdapter {
	private static final String TAG = MemberAddLvAdapter.class.getSimpleName();
	private List<String> mFriendList;
	private Map<Integer, Boolean> mIsSelectedMap;
	private Context mContext;
	private LayoutInflater mInflater = null;
	private List<String> mGroupMemberList;

	public MemberAddLvAdapter(List<String> friendList, Context context, List<String> groupMemberList) {
		this.mContext = context;
		this.mFriendList = friendList;
		this.mInflater = LayoutInflater.from(context);
		this.mIsSelectedMap = new HashMap<>();
		this.mGroupMemberList = groupMemberList;
		initCheckBoxs();
	}

	private void initCheckBoxs() {
		if(mFriendList != null){
			for (int i = 0; i < mFriendList.size(); i++) {
				isSelected().put(i, false);
			}
		}
	}

	@Override
	public int getCount() {
		return mFriendList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFriendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.add_member_item, null);
			holder = new ViewHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String username = mFriendList.get(position);
		// 如果已经属于群成员则不可点击
		if (mGroupMemberList != null && mGroupMemberList.contains(username)) {
			isSelected().put(position, true);
		}
		holder.memberTv.setText(username);
		holder.selectionChk.setChecked(isSelected().get(position));
		return convertView;
	}

	public Map<Integer, Boolean> isSelected() {
		return mIsSelectedMap;
	}

	public void setSelected(HashMap<Integer, Boolean> isSelected) {
		isSelected = isSelected;
	}

	public class ViewHolder {
		public TextView memberTv;
		public CheckBox selectionChk;

		public ViewHolder(View view) {
			memberTv = (TextView) view.findViewById(R.id.item_member);
			selectionChk = (CheckBox) view.findViewById(R.id.item_selection);
		}
	}
}
