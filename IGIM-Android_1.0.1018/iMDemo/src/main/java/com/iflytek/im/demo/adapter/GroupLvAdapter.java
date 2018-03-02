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

public class GroupLvAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<String> mGroupList = new ArrayList<>();

	public GroupLvAdapter(Context context, List<String> groups) {
		super();
		this.mInflater = LayoutInflater.from(context);
		this.mGroupList = groups;
	}

	@Override
	public int getCount() {
		return mGroupList.size();
	}

	@Override
	public Object getItem(int position) {
		return mGroupList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friend_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String groupMemberName = mGroupList.get(position);
		holder.groupMemberNameTv.setText(groupMemberName);
		return convertView;
	}
	
	public class ViewHolder {
		TextView groupMemberNameTv;
		public ViewHolder(View view) {
			groupMemberNameTv = (TextView) view.findViewById(R.id.friendName);
		}
	}

}
