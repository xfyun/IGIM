package com.iflytek.im.demo.ui.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.im.demo.Config;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.FaceGvAdapter;
import com.iflytek.im.demo.adapter.FaceVpAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * 一个用来显示Emoji表情的Fragment
 * Created by imxqd on 2016/8/30.
 */
public class EmojiFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    public static final String TAG = "EmojiFragment";


    private static final int COLUMNS = 6;
    private static final int ROWS = 5;

    private static final String PARAM_LISTENER = "listener";

    private List<String> mFaceList;
    private List<View> mViews;

    private FaceVpAdapter mAdapter;
    private OnEmojiInputListener mListener;

    private ViewPager mViewPager;
    private CircleIndicator mIndicator;

    public EmojiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EmojiFragment.
     */
    public static EmojiFragment newInstance() {
        return new EmojiFragment();
    }


    @Override
    protected void initMember() {
        mFaceList = new ArrayList<>();
        String[] faces;
        try {
            faces = getActivity().getAssets().list("Face");
            Collections.addAll(mFaceList, faces);
            mFaceList.remove("emotion_del_normal.png");
        } catch (IOException e) {
            if (Config.isDebug) {
                e.printStackTrace();
            }
        }
        mViews = new ArrayList<>();
        int pageCount = getPagerCount();
        for (int i = 0; i < pageCount; i++) {
            mViews.add(getPageView(i));
        }
        mAdapter = new FaceVpAdapter(mViews);
    }

    @Override
    protected void findViews() {
        mViewPager = f(R.id.emoji_page);
        mIndicator = f(R.id.pager_indicator);
    }

    @Override
    protected void initUI() {
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void setupEvents() {

    }

    private View getPageView(int position) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        GridView gridview = (GridView) inflater.inflate(R.layout.face_gridview, mViewPager, false);// 表情布局
        List<String> subList = getSubEmojiList(position);
        FaceGvAdapter mGvAdapter = new FaceGvAdapter(subList);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(COLUMNS);
        gridview.setOnItemClickListener(this);

        return gridview;
    }


    private int getPagerCount() {
        int count = mFaceList.size();
        if (count % (COLUMNS * ROWS - 1) == 0) {
            return count / (COLUMNS * ROWS - 1);
        } else {
            return count / (COLUMNS * ROWS - 1) + 1;
        }
    }

    private List<String> getSubEmojiList(int position) {
        List<String> list = new ArrayList<>();
        if ((COLUMNS * ROWS - 1) * (position + 1) > mFaceList.size()) {
            list.addAll(mFaceList.subList(position * (COLUMNS * ROWS - 1), mFaceList.size()));
        } else {
            list.addAll(mFaceList.subList(position * (COLUMNS * ROWS - 1), (COLUMNS * ROWS - 1) * (position + 1)));
        }
        list.add("emotion_del_normal.png");
        return list;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_emoji;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener == null) {
            return;
        }
        try {
            String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
            if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                mListener.onEmojiInput(png);
            } else {
                mListener.onEmojiDelete();
            }
        } catch (Exception e) {
            if (Config.isDebug) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEmojiInputListener) {
            mListener = (OnEmojiInputListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEmojiInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnEmojiInputListener extends Serializable{
        void onEmojiInput(String emoji);
        void onEmojiDelete();
    }
}
