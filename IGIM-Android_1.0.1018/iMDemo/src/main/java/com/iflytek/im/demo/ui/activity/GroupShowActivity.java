package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.GroupLvAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupShowActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "GroupShowActivity";
	private static final String GROUP_GID = "10000";
	private ViewPager mViewPager;
	private TextView mGroupTv, mDisGroupTv, mGroupJoinTv, mGroupCreTv;
	private View mGroupView, mDisGroupView;
	private ListView mGroupLv, mDisGroupLv;
	private List<View> mViews;
	private int mBitmapWidth, mOffSet = 0, mCurrIndex = 0;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupshow);
		initView();
		initListener();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.groupshow;
	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}

	private void initData() {
		ImApplication app = (ImApplication) getApplicationContext();
	}

	private void initListener() {
		mGroupTv.setOnClickListener(new MyPagerOnClickListener(0));
		mDisGroupTv.setOnClickListener(new MyPagerOnClickListener(1));
		mGroupJoinTv.setOnClickListener(this);
		mGroupCreTv.setOnClickListener(this);
		mDisGroupLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 跳转到群聊天的界面
				Intent intent = new Intent(GroupShowActivity.this, TalkActivity.class);
				intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, GROUP_GID);
				intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, 1);
				startActivity(intent);
			}
		});
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.groupPager);
		mImageView = (ImageView) findViewById(R.id.cursorGroup);

		mGroupTv = (TextView) findViewById(R.id.groupText);
		mDisGroupTv = (TextView) findViewById(R.id.disGroupText);
		mGroupJoinTv = (TextView) findViewById(R.id.joinGroup);
		mGroupCreTv = (TextView) findViewById(R.id.createGroup);

		LayoutInflater layoutInflater = getLayoutInflater();
		mGroupView = layoutInflater.inflate(R.layout.group, null);
		mDisGroupView = layoutInflater.inflate(R.layout.disgroup, null);

		mGroupLv = (ListView) mGroupView.findViewById(R.id.groups);
		mDisGroupLv = (ListView) mDisGroupView.findViewById(R.id.disGroups);

		mViews = new ArrayList<>();
		mViews.add(mGroupView);
		mViews.add(mDisGroupView);

		mBitmapWidth = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenW = displayMetrics.widthPixels;
		mOffSet = (screenW / 2 - mBitmapWidth);
		Matrix matrix = new Matrix();
		matrix.postTranslate(screenW, 0);
		mImageView.setImageMatrix(matrix);

		mViewPager.setAdapter(new MyPagerAdapter(mViews));
		mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setCurrentItem(0);
	}

	private class MyPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position));
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	private class MyOnPageChangeListener implements OnPageChangeListener {
		int size = mOffSet * 2 + mBitmapWidth;

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(size * mCurrIndex, size * arg0, 0, 0);
			mCurrIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			mImageView.startAnimation(animation);
			switch (mCurrIndex) {
				case 0:
					mGroupTv.setTextColor(getResources().getColor(R.color.color_bai));
					getGroup();
					break;
				case 1:
					mDisGroupTv.setTextColor(getResources().getColor(R.color.color_bai));
					getGroup();
					break;
				default:
					break;
			}
		}

	}

	private class MyPagerOnClickListener implements OnClickListener {
		private int index = 0;

		public MyPagerOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mViewPager.setCurrentItem(index);

			switch (index) {
				case 0:
					mGroupTv.setTextColor(getResources().getColor(R.color.color_bai));
					getGroup();
					break;
				case 1:
					mDisGroupTv.setTextColor(getResources().getColor(R.color.color_bai));
					getGroup();
					break;
				default:
					break;
			}
		}
	}

	public void getGroup() {
		Map<String, String> getGroupInfoParams = new HashMap<>();
		getGroupInfoParams.put(Group.GID, GROUP_GID);
		IMClient.getInstance().getGroupInfo(getGroupInfoParams, new ResultCallback<Group>() {

			@Override
			public void onSuccess(Group group) {
				getGroupDate(group);
				List<String> groupTitles = new ArrayList<>();
				int groupMemberLength = group.getMemeber().size();
				String groupTitle = group.getgName() + "(" + groupMemberLength + ")";
				groupTitles.add(groupTitle);
				GroupLvAdapter adapter = new GroupLvAdapter(GroupShowActivity.this, groupTitles);
				if (group.getType() == 0) {
					mDisGroupLv.setAdapter(adapter);
				} else {
					mGroupLv.setAdapter(adapter);
				}
			}

			@Override
			public void onError(int errorCode) {
				Log.i(TAG, "获取群组信息失败");
			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.createGroup:
				Intent creIntent = new Intent(GroupShowActivity.this, CreateGroupActivity.class);
				startActivity(creIntent);
				break;
			case R.id.joinGroup:
				Intent joiIntent = new Intent(GroupShowActivity.this, JoinGroupActivity.class);
				startActivity(joiIntent);
				break;
			default:
				break;
		}

	}

	private void getGroupDate(Group group) {
		String gid = group.getGid();
		Map<String, Group> groupInfoMap = new HashMap<>();
		if (TextUtils.isEmpty(gid)) {
			throw new IllegalArgumentException("群组gid不能为空");
		} else {
			groupInfoMap.put(gid, group);
		}
	}

//	@Override
//	public void onBackPressed() {
////		moveTaskToBack(true);
////		super.onBackPressed();
//		Intent intent = new Intent(GroupShowActivity.this, MainActivity.class);
//		startActivity(intent);
//	}
}
