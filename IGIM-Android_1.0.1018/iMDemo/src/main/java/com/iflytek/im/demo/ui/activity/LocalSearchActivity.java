package com.iflytek.im.demo.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.FriendRVAdapter;
import com.iflytek.im.demo.dao.Db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.iflytek.im.demo.dao.Db.mFriendList;

/**
 * Created by Administrator on 2016/11/14.
 * Modified by IMXQD
 */

public class LocalSearchActivity extends BaseActivity {
    private static final String TAG = "LocalSearchActivity";

    private Toolbar mToolbar;
    private SearchView mSearchView;
//    private TextView mFriendTV, mGroupTV, mDiscussionGroupTV;
    private RecyclerView searchResultRV;

    private FriendRVAdapter searchResultAdapter;


//    private Map<String,Group> mGroupMap = Db.getInstance().mGroupInfoMap;
//    private Map<String, Group> mDiscussionGroup = Db.getInstance().mSmallGroupInfoMap;
    private List<String> mFriends = Db.getInstance().mFriendList;


    @Override
    protected void findViews() {
        mToolbar = f(R.id.toolbar);
        mSearchView = f(R.id.search_bar);
        searchResultRV = f(R.id.search_result);
        setSupportActionBar(mToolbar);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchView.onActionViewExpanded();
            }
        });
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initMembers() {
        searchResultAdapter = new FriendRVAdapter();
    }

    @Override
    protected void setupEvents() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)) {
                    startToSearch(newText);
                }
                return true;
            }
        });

    }

    public void startToSearch(String keyword){
        List<String> friends = searchInFriend(keyword);
//        List<Group> groups = searchInGroup(keyword);
//        List<Group> discussionGroups = searchInDiscussionGroup(keyword);
//
//        Map<Integer,List> result = new HashMap<>();
//        result.put(0,friends);
//        result.put(1,groups);
//        result.put(2,discussionGroups);
//        searchResultRV.setMinimumHeight(DisplayUtil.getScreenHeight());
//        int height = DisplayUtil.dp2px((friends.size()+groups.size()+discussionGroups.size()+2)*40);
//        int width = DisplayUtil.getScreenWidth();
//        Log.d(TAG, "startToSearch: width:" + width + "height:" + DisplayUtil.getScreenHeight());
        searchResultAdapter.setmList(friends);
        searchResultRV.setAdapter(searchResultAdapter);
        searchResultRV.setLayoutManager(new LinearLayoutManager(this));
//        searchResultRV.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        ViewGroup.LayoutParams  params =  searchResultRV.getLayoutParams();
//        params.height = height;
//        params.width =width;
//        searchResultRV.setLayoutParams(params);
        searchResultAdapter.notifyDataSetChanged();
    }

   /* public List<Group> searchInGroup(String keyWord){
        List<Group>  searchResult = new ArrayList<>();



        for (String gid : mGroupMap.keySet()){
            Group group = mGroupMap.get(gid);
            if(gid.contains(keyWord)){
                searchResult.add(group);
            }else if(group.getgName().contains(keyWord)){
                searchResult.add(group);
            }
        }
        return searchResult;

    }


    public List<Group> searchInDiscussionGroup(String keyWord){
        List<Group>  searchResult = new ArrayList<>();
        for (String gid : mDiscussionGroup.keySet()){
            Group group = mDiscussionGroup.get(gid);
            if(gid.contains(keyWord)){
                searchResult.add(group);
            }else if(group.getgName().contains(keyWord)){
                searchResult.add(group);
            }
        }
        return searchResult;
    }

*/
    public List<String> searchInFriend(String keyWord){
        List<String> searchResult = new ArrayList<>();
        for(String uid : mFriendList){
            if(uid.contains(keyWord)){
                searchResult.add(uid);
            }
        }
        return searchResult;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.mainactivity_search;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.translate_finsh_in,R.anim.translate_finsh_out);
    }
}
