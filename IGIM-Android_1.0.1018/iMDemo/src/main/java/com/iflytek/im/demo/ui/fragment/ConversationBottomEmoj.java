package com.iflytek.im.demo.ui.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.FaceGvAdapter;
import com.iflytek.im.demo.adapter.FaceVpAdapter;
import com.iflytek.im.demo.common.ExpressionController;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

public class ConversationBottomEmoj extends ConversationBottomFragment{

	private static  final String TAG = "ConversationBottomEmoj";
	
	private static final int COLUMNS = 6;
	private static final int ROWS = 4;
	
	
	private List<View> mViews = new ArrayList<>();
	private List<String> mFaceList;

	
	private Activity mCurrentActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurrentActivity = getActivity();
		initFaces();
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mViewPager.addOnPageChangeListener(new PageChange());
		InitViewPager();
		return view;
	}
	
    public void hideEmoj(){
		mAllEmoj.setVisibility(View.GONE);
	//	KeyboardUtil.showKeyboard(mInputEditText);
        mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
        mPanelRoot.setVisibility(View.GONE);
	//	KeyboardUtil.hideKeyboard(mInputEditText);// 隐藏软键盘
	//	mOtherFuncDetail.setVisibility(View.GONE);
	}
	public void hideOtherFun(){
        mOtherFuncDetail.setVisibility(View.GONE);
	//	KeyboardUtil.showKeyboard(mInputEditText);
        mPanelRoot.setVisibility(View.GONE);
   //     KeyboardUtil.hideKeyboard(mInputEditText);// 隐藏软键盘
    }


	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.image_face:
		
			if (mAllEmoj.getVisibility() == View.GONE) {
				mInputEditText.setVisibility(View.VISIBLE);
				mAudioRecorderBtn.setVisibility(View.GONE);
				mEmojMsg.setBackgroundResource(R.drawable.keyboard);
				mVoiceMsg.setVisibility(View.VISIBLE);
				mAllEmoj.setVisibility(View.VISIBLE);
				KeyboardUtil.hideKeyboard(mInputEditText);// 隐藏软键盘
			} else {
				KeyboardUtil.showKeyboard(mInputEditText);
				mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
				mAllEmoj.setVisibility(View.GONE);
			}
			mOtherFuncDetail.setVisibility(View.GONE);
			break;
		default :
			break;
		}

	}
	private void InitViewPager() {
		for (int i = 0; i < getPagerCount(); i++) {
			mViews.add(viewPagerItem(i));
			LayoutParams params = new LayoutParams(16, 16);
			mFaceDotsLayout.addView(dotsItem(i), params);
		}
		FaceVpAdapter mVpAdapter = new FaceVpAdapter(mViews);
		mViewPager.setAdapter(mVpAdapter);
		mFaceDotsLayout.getChildAt(0).setSelected(true);
	}

	private View viewPagerItem(int position) {
		LayoutInflater inflater = (LayoutInflater) mCurrentActivity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.face_gridview, null);// 表情布局
		GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
		List<String> subList = new ArrayList<String>();
		subList.addAll(mFaceList.subList(position * (COLUMNS * ROWS - 1),
				(COLUMNS * ROWS - 1) * (position + 1) > mFaceList.size() ? mFaceList.size()
						: (COLUMNS * ROWS - 1) * (position + 1)));
		subList.add("emotion_del_normal.png");
		FaceGvAdapter mGvAdapter = new FaceGvAdapter(subList);
		gridview.setAdapter(mGvAdapter);
		gridview.setNumColumns(COLUMNS);
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
					if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
						insert(getFace(png));
					} else {
						ExpressionController.expressionDelete(mInputEditText.getText());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return gridview;
	}
	
	/**
	 * 向输入框里添加表情
	 */
	private void insert(CharSequence text) {
		int iCursorStart = Selection.getSelectionStart((mInputEditText.getText()));
		int iCursorEnd = Selection.getSelectionEnd((mInputEditText.getText()));
		if (iCursorStart != iCursorEnd) {
			((Editable) mInputEditText.getText()).replace(iCursorStart, iCursorEnd, "");
		}
		int iCursor = Selection.getSelectionEnd((mInputEditText.getText()));
		((Editable) mInputEditText.getText()).insert(iCursor, text);
	}


	private SpannableStringBuilder getFace(String png) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		try {
			String tempText = png;
			sb.append(tempText);
			sb.setSpan(
					new ImageSpan(mCurrentActivity,
							BitmapFactory.decodeStream(mCurrentActivity.getAssets().open("Face/" + png))),
					sb.length() - tempText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb;
	}

	
	private ImageView dotsItem(int position) {
		LayoutInflater inflater = (LayoutInflater) mCurrentActivity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dot_image, null);
		ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
		iv.setId(position);
		return iv;
	}

	private int getPagerCount() {
		int count = mFaceList.size();
		return count % (COLUMNS * ROWS - 1) == 0 ? count / (COLUMNS * ROWS - 1) : count / (COLUMNS * ROWS - 1) + 1;
	}

	private void initFaces() {
		try {
			mFaceList = new ArrayList<String>();
			String[] faces = mCurrentActivity.getAssets().list("Face");
			for (int i = 0; i < faces.length; i++) {
				mFaceList.add(faces[i]);
			}
			mFaceList.remove("emotion_del_normal.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class PageChange implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mFaceDotsLayout.getChildCount(); i++) {
				mFaceDotsLayout.getChildAt(i).setSelected(false);
			}
			mFaceDotsLayout.getChildAt(arg0).setSelected(true);
		}

	}

	public int getemojX(){
		int[] leftTop = {0, 0};
		//获取输入框当前的location位置
		mEmojMsg.getLocationInWindow(leftTop);
		int left = leftTop[0];
		int top = leftTop[1];
		int bottom = top + mEmojMsg.getHeight();
		int right = left + mEmojMsg.getWidth();
		return top;
	}
	public int getemojY(){
		int[] leftTop = {0, 0};
		//获取输入框当前的location位置
		mEmojMsg.getLocationInWindow(leftTop);
		int left = leftTop[0];
		int top = leftTop[1];
		int bottom = top + mEmojMsg.getHeight();
		int right = left + mEmojMsg.getWidth();
		return bottom;
	}

	public int getotherfunX(){
		int[] leftTop = {0, 0};
		//获取输入框当前的location位置
		mOtherFunc.getLocationInWindow(leftTop);
		int left = leftTop[0];
		int top = leftTop[1];
		int bottom = top + mOtherFunc.getHeight();
		int right = left + mOtherFunc.getWidth();
		return top;
	}
	public int getotherfunY(){
		int[] leftTop = {0, 0};
		//获取输入框当前的location位置
		mOtherFunc.getLocationInWindow(leftTop);
		int left = leftTop[0];
		int top = leftTop[1];
		int bottom = top + mOtherFunc.getHeight();
		int right = left + mOtherFunc.getWidth();
		return bottom;
	}

	public View getText(){
		return mInputEditText;
	}


}
