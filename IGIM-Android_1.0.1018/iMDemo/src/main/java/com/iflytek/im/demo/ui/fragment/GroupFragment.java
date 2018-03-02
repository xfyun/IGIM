package com.iflytek.im.demo.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.GroupRVAdapter;

import java.util.List;

import static com.iflytek.im.demo.Constants.Event.NEW_MESSAGE_IN;

/**
 * 一个用来显群组列表的Fragment
 * Created by imxqd on 2016/8/30.
 */
public class GroupFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private boolean needToSync = true;
    private RecyclerView mRVList;
    private SwipeRefreshLayout mRefreshLayout;

    private GroupRVAdapter mAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initMember() {
        mAdapter = new GroupRVAdapter(1);
    }

    @Override
    protected void findViews() {
        mRVList = f(R.id.group_list);
        mRefreshLayout = f(R.id.group_refresh);
    }

    @Override
    protected void initUI() {
        mRVList.setAdapter(mAdapter);
        mRVList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void setupEvents() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mAdapter.update(needToSync, new GroupRVAdapter.UpdateCallback() {
            @Override
            public void onComplete(List<Group> groups) {
                mRefreshLayout.setRefreshing(false);
                mAdapter.setList(groups);
                if (mRVList.getScrollState() == RecyclerView.SCROLL_STATE_IDLE ||
                        !mRVList.isComputingLayout()) {
                    mAdapter.notifyDataSetChanged();
                }
//                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe(tags = {@Tag(Constants.Event.CREATE_GROUP_SUCCESS)})
    public void onUpdateList(Object o){
        onRefresh();
    }


    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
        if (needToSync)
            needToSync = false;
    }
}
