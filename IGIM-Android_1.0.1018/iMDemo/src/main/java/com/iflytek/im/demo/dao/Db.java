package com.iflytek.im.demo.dao;

import android.content.Context;

import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.im.demo.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Db {
	public static List<String> mFriendList;// 保存当前用户的所有好友信息
	public Context mContext;

	private static Db mInstance = null;

	private Db(Context context) {
		this.mContext = context;
	}

	public static void init(Context context) {
		if (mInstance == null) {
			mInstance = new Db(context);
		}
	}

	public static Db getInstance() {
		return mInstance;
	}
/*
	public List<String> getGroupGids() {
		List<String> groupGids = new ArrayList<>();
		for(String gid : mGroupInfoMap.keySet()){
			groupGids.add(gid);
		}
		for(String gid : mSmallGroupInfoMap.keySet()){
			groupGids.add(gid);
		}
		return groupGids;
	}*/

	public List<String> getFriends() {
		return mFriendList;
	}

	public void setFriends(List<String> friends) {
		this.mFriendList = friends;
	}
/*
	public void setGroupInfos(Map<String, Group> groupInfos) {
		this.mGroupInfoMap = groupInfos;
	}

	public void setGroupInfos(List<Group> data){
		mGroupInfoMap.clear();
		mSmallGroupInfoMap.clear();
		for(Group group :data){
			if(group.getType() == Constants.GroupType.GROUP){
				//群组
				mGroupInfoMap.put(group.getGid(),group);
			}else{
				//讨论组
				mSmallGroupInfoMap.put(group.getGid(),group);
			}
		}
	}*/
}
