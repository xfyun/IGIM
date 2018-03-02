package com.iflytek.im.demo.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class FaceVpAdapter extends PagerAdapter {

	// 界面列表
	private List<View> mViewList;

	public FaceVpAdapter(List<View> views) {
		this.mViewList = views;
	}

	@Override
	public void destroyItem(ViewGroup container, int index, Object arg2) {
		container.removeView(mViewList.get(index));
	}

	@Override
	public int getCount() {
		if (mViewList != null) {
			return mViewList.size();
		}

		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(ViewGroup container, int index) {
		container.addView(mViewList.get(index), 0);

		return mViewList.get(index);
	}

	// 判断是否由对象生成界
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

}