package com.iflytek.im.demo.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.FriendRVAdapter;
import com.iflytek.im.demo.listener.OnQuickSideBarTouchListener;
import com.iflytek.im.demo.ui.view.DividerDecoration;
import com.iflytek.im.demo.ui.view.QuickSideBarTipsView;
import com.iflytek.im.demo.ui.view.QuickSideBarView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

/**
 * 一个用来显示朋友列表的Fragment
 * Created by imxqd on 2016/8/30.
 */
public class FriendFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        OnQuickSideBarTouchListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private FriendRVAdapter mAdapter;

    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;

    public FriendFragment() {
        // Required empty public constructor
    }

    public static FriendFragment newInstance() {
        return new FriendFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_friend_list;
    }

    @Override
    protected void initMember() {
        mAdapter = new FriendRVAdapter();
    }

    @Override
    protected void findViews() {
        recyclerView = f(R.id.recyclerView);
        mRefreshLayout = f(R.id.friend_refresh);
        quickSideBarView = f(R.id.quickSideBarView);
        quickSideBarTipsView = f(R.id.quickSideBarTipsView);

    }

    @Override
    protected void initUI() {


        try {
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);


            final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
            recyclerView.addItemDecoration(headersDecor);

            // Add decoration for dividers between list items
            recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
        } catch (Exception e) {
            Log.e("BaseFragment", "the recyclerView is wrong");
            e.printStackTrace();
        }
    }

    @Override
    protected void setupEvents() {
        quickSideBarView.setOnQuickSideBarTouchListener(this);
        mRefreshLayout.setOnRefreshListener(this);
//        mRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
////                mRefreshLayout.setRefreshing(true);
////                if(mAdapter.getItemCount() == 0)
////                    onRefresh();
//            }
//        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        mAdapter.update(new FriendRVAdapter.UpdateCallback() {
            @Override
            public void onComplete() {
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void updateLetter(List letters) {
                quickSideBarView.setLetters(letters);
            }
        });
    }





    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if(mAdapter.getmLetters().containsKey(letter)){
            LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int toPosition = mAdapter.getmLetters().get(letter);
            int nowPosition = llmanager.findFirstVisibleItemPosition();
            if(toPosition > nowPosition)
                llmanager.scrollToPositionWithOffset(toPosition, toPosition -nowPosition);
            else
                llmanager.scrollToPosition(toPosition);
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        quickSideBarTipsView.setVisibility(touching? View.VISIBLE:View.INVISIBLE);
    }




}
