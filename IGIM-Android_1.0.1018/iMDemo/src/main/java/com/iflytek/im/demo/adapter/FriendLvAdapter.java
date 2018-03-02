package com.iflytek.im.demo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iflytek.im.demo.R;

public class FriendLvAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<String> mFriends = new ArrayList<>();

	public FriendLvAdapter(Context context, List<String> users) {
		this.mInflater = LayoutInflater.from(context);
		this.mFriends = users;
	}

	public int getCount() {
		return mFriends.size();
	}

	public Object getItem(int position) {
		return mFriends.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friend_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String friendName = mFriends.get(position);
		holder.friendNameTv.setText(friendName);
		return convertView;
	}

	public class ViewHolder {
		TextView friendNameTv;
		public ViewHolder(View view) {
			friendNameTv = (TextView) view.findViewById(R.id.friendName);
		}
	}
}
