package com.iflytek.im.demo.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.GroupSearchResultAdapter;
import com.iflytek.im.demo.adapter.GroupSearchResultAdapter.GroupSearchCallBack;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinGroupActivity extends BaseActivity implements GroupSearchCallBack {
	private final static String TAG = "JoinGroupActivity";

	private EditText mKeyWordEdt;
	private Button mGroupSearchBtn;
	private RecyclerView mSearchResultRV;
	private GroupSearchResultAdapter mGroupSearchResultAdapter;
    private LinearLayout mNoSearchResultToShow;

        @Override
	protected int getLayoutRes() {
		return R.layout.joingroup;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    protected void initMembers() {
        super.initMembers();
        mGroupSearchResultAdapter = new GroupSearchResultAdapter();
    }

    @Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void findViews() {
		super.findViews();
		mKeyWordEdt = f(R.id.key_word);
		mGroupSearchBtn = f(R.id.search_group);
		mSearchResultRV = f(R.id.group_search_result);
        mNoSearchResultToShow = f(R.id.no_result_to_show);
	}

	@Override
	protected void initViews() {
		super.initViews();
		setTitle("加入群组");
		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void setupEvents() {
		super.setupEvents();
        mGroupSearchResultAdapter.setCallBack(this);
		mGroupSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyWords = mKeyWordEdt.getText().toString();
				if(TextUtils.isEmpty(keyWords)){
					ToastUtil.showText("请输入群ID");
					return;
				}
				searchGroup(keyWords);
			}

		});

	}

	private void showSearchResult(List<Group> result) {
        if(result.size() != 0){
            mSearchResultRV.setVisibility(View.VISIBLE);
            mNoSearchResultToShow.setVisibility(View.GONE);
            mGroupSearchResultAdapter.setmGroupList(result);
            mSearchResultRV.setAdapter(mGroupSearchResultAdapter);
            mSearchResultRV.setLayoutManager(new LinearLayoutManager(this));
            mGroupSearchResultAdapter.notifyDataSetChanged();
        }else{
            mSearchResultRV.setVisibility(View.GONE);
            mNoSearchResultToShow.setVisibility(View.VISIBLE);
        }
	}


	private void searchGroup(String keyWords) {
        List<Group> result = new ArrayList<>();
		IMClient.getInstance().searchGroup(keyWords, new ResultCallback<List<Group>>() {
			@Override
			public void onError(int errorCode) {
				Log.d(TAG,"search group error, errorCode = " + errorCode);
				showSearchResult(new ArrayList<Group>());
			}

			@Override
			public void onSuccess(List<Group> datas) {
				Log.d(TAG,"search group success, datas = " +datas);
				showSearchResult(datas);
			}
		});


	}

	public void joinGroup(Map<String, String> joinGroupParams) {
		IMClient.getInstance().joinToGroup(joinGroupParams,
				new ResultCallback<String>() {

					@Override
					public void onSuccess(String data) {
						ToastUtil.showText("申请成功，请耐心等待");
						finish();
					}

					@Override
					public void onError(int errorCode) {
						ToastUtil.showText("申请失败,请是否已在该群中");
					}

				});


	}



}
