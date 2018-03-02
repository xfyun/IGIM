package com.iflytek.im.demo.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.AppManager;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.SectionsPagerAdapter;
import com.iflytek.im.demo.common.NetworkControl;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.dao.SharePreferenceHelper;

import static com.iflytek.cloud.im.IMClientError.ERROR_NETWORK;
import static com.iflytek.im.demo.Constants.Preference.KEY_UID;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private final static String TAG="MainActivity";
    
    
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mUserIcon;
    private TextView mUserName;
//    private static boolean isExit = false;


    private IFlytekUpdate updater;

   /* Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
*/

    @Override
    protected void onResume() {
        runTalkActivity();
        super.onResume();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initMembers() {
        updater = IFlytekUpdate.getInstance(getApplicationContext());
        if(NetworkControl.getInstance().isNetworkConnected())
            checkForUpdate();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
    }

    @Override
    protected void findViews() {
        mToolbar = f(R.id.toolbar);
        mDrawer = f(R.id.drawer_layout);
        mNavigationView = f(R.id.nav_view);
        mViewPager = f(R.id.container);
        mTabLayout = f(R.id.tabs);
        View header = mNavigationView.getHeaderView(0);
        mUserIcon = (ImageView) header.findViewById(R.id.nav_header_icon);
        mUserName = (TextView) header.findViewById(R.id.nav_header_name);
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        setTitle(getPageTitle(0));
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_message_white_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_contacts_white_24dp);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_group_work_white_24dp);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_group_white_24dp);

        mUserIcon.setImageResource(R.drawable.iv_head_to);
        String uid = SharePreferenceHelper.getInstance().getString(KEY_UID);
        mUserName.setText(uid);

        requestPermissions();
    }

    @Override
    protected void setupEvents() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mViewPager.addOnPageChangeListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_group) {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_add_small_group) {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_join_group) {
            Intent intent = new Intent(this, JoinGroupActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(this, LocalSearchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.nav_logout:
                logout();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        IMClient.getInstance().logout(new ResultCallback<String>() {
            @Override
            public void onError(int errorCode) {
                ToastUtil.showText("error :"+errorCode);
            }

            @Override
            public void onSuccess(String datas) {
                logoutSuccess();
            }

        }, false);
    }

    private void logoutSuccess() {
        SharePreferenceHelper.getInstance().clear();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(Constants.Preference.KEY_UID, IMClient.getInstance().getCurrentUser());
        startActivity(intent);
        finish();
    }

    @Override
    public void onPageSelected(int position) {
        setTitle(getPageTitle(position));
    }

    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return getString(R.string.title_conversation);
            case 1:
                return getString(R.string.title_friend);
            case 2:
                return getString(R.string.title_group_work);
            case 3:
                return getString(R.string.title_group);
        }
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void checkForUpdate() {
        updater = IFlytekUpdate.getInstance(getApplicationContext());
        updater.setDebugMode(true);
        updater.setParameter(UpdateConstants.EXTRA_WIFIONLY, "true");
        updater.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false");
        updater.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
        updater.forceUpdate(getApplicationContext(), updateListener);
    }

    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {

        @Override
        public void onResult(int errorCode, UpdateInfo result) {
            switch (errorCode) {
                case UpdateErrorCode.OK:
                    if (result != null && result.getUpdateType() == UpdateType.NoNeed) {
                        return;
                    }
                    updater.showUpdateInfo(MainActivity.this, result);
                    break;
                case 20002:
                    Looper.prepare();
                    ToastUtil.showText("网络不给力哦");
                    Looper.loop();
                    break;
                default:
                    Looper.prepare();
                    ToastUtil.showText("已有最新版本，更新失败，建议手动更新");
                    Looper.loop();
                    break;
            }
        }
    };



    protected void runTalkActivity() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        int conversationType = bundle.getInt(Constants.Parameter.KEY_CONVERSATION_TYPE);
        String connectPersonID = bundle.getString(Constants.Parameter.KEY_RECEIVER_ID);
        String connectpersonName = bundle.getString(Constants.Parameter.KEY_RECEIVER_NAME);
        Intent intent2 = new Intent(this, TalkActivity.class);
        intent2.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, conversationType);
        intent2.putExtra(Constants.Parameter.KEY_RECEIVER_ID, connectPersonID);
        intent2.putExtra(Constants.Parameter.KEY_RECEIVER_NAME, connectpersonName);
        intent2.putExtra(Constants.Parameter.KEY_IS_NOTIFICATION, true);
        startActivity(intent2);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.finishAllActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.READ_PHONE_STATE},1122);
                }

                if (!Settings.System.canWrite(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1133);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
