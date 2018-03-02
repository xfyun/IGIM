package com.iflytek.im.demo.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.GroupRVAdapter;

import java.util.List;

import static java.security.AccessController.getContext;

/**
 * 一个用来显讨论组列表的Fragment
 * Created by imxqd on 2016/8/30.
 */
public class SmallGroupFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView mRVList;
    private SwipeRefreshLayout mRefreshLayout;
    private GroupRVAdapter mAdapter;

    public SmallGroupFragment() {
        // Required empty public constructor
    }

    public static SmallGroupFragment newInstance() {
        return new SmallGroupFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_small_group;
    }

    @Override
    protected void initMember() {
        mAdapter = new GroupRVAdapter(0);
    }

    @Override
    protected void findViews() {
        mRVList = f(R.id.small_group_list);
        mRefreshLayout = f(R.id.small_group_refresh);
    }

    @Override
    protected void initUI() {
        mRVList.setAdapter(mAdapter);
        mRVList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        onRefresh();
    }

    @Override
    protected void setupEvents() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mAdapter.update(false,new GroupRVAdapter.UpdateCallback() {
            @Override
            public void onComplete(List<Group> groups) {
                mRefreshLayout.setRefreshing(false);
                mAdapter.setList(groups);
                if (mRVList.getScrollState() == RecyclerView.SCROLL_STATE_IDLE ||
                        !mRVList.isComputingLayout()) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscribe(tags = {@Tag(Constants.Event.CREATE_DIS_GROUP_SUCCESS)})
    public void onUpdateList(Object o){
        onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}
