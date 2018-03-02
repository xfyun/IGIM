/*
package com.iflytek.im.demo.av.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.cloud.imcall.IMCallClient;
import com.iflytek.cloud.imcall.agora.propeller.UserStatusData;
import com.iflytek.cloud.imcall.agora.propeller.ui.RtlLinearLayoutManager;
import com.iflytek.cloud.imcall.agora.ui.GridVideoViewContainer;
import com.iflytek.cloud.imcall.agora.ui.SmallVideoViewAdapter;
import com.iflytek.cloud.imcall.agora.ui.SmallVideoViewDecoration;
import com.iflytek.cloud.imcall.listener.CallStateListener;
import com.iflytek.cloud.imcall.model.AVSession;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.ui.activity.BaseActivity;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.video.VideoCanvas;

public class CallActivity extends BaseActivity implements View.OnClickListener, CallStateListener {

    private static final String TAG = "CallActivity";

    public static final int TYPE_INCALL = 1;
    public static final int TYPE_OUTCALL = 2;

    public static final int STREAM_TYPE_AUDIO = 1;
    public static final int STREAM_TYPE_VIDEO = 2;

    public static final String PRARM_USER_ID = "user_id";
    public static final String PRARM_SESSION = "session";
    public static final String PRARM_TYPE = "type";
    public static final String PRARM_STREAM_TYPE = "stream_type";

    private int mSessionType = 0;
    private int mStreamType = 0;
    private AVSession mSession = null;
    private String mUserId = null;
    private HashMap<Integer, SoftReference<SurfaceView>> mUidList = new HashMap<>();

    private ViewStub mVideoContainerStub;
    private GridVideoViewContainer mVideoContainer;
    private LinearLayout mSmallVideoViewDock;

    private LinearLayout mBottomBarIncall;
    private LinearLayout mBottomBarOutcall;
    private LinearLayout mUserInfo;
    private Button mBtnAccept;
    private Button mBtnReject;
    private ImageButton mBtnHangup;
    private ImageView mBtnMute;
    private ImageView mBtnSpeaker;
    private ImageView mBtnSwitchCamera;
    private ImageView mBtnVideo;

    private ImageView mIvUserIcon;
    private TextView mTvUserId;
    private TextView mTvState;


    public static void startVideoInCall(Context context, AVSession session, String userId) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PRARM_USER_ID, userId);
        intent.putExtra(PRARM_SESSION, session);
        intent.putExtra(PRARM_TYPE, TYPE_INCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_VIDEO);
        context.startActivity(intent);
    }

    public static void startVideoOutCall(Context context, String userId) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.putExtra(PRARM_USER_ID, userId);
        intent.putExtra(PRARM_TYPE, TYPE_OUTCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_VIDEO);
        context.startActivity(intent);
    }

    public static void startAudioInCall(Context context, AVSession session, String userId) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PRARM_USER_ID, userId);
        intent.putExtra(PRARM_SESSION, session);
        intent.putExtra(PRARM_TYPE, TYPE_INCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_AUDIO);
        context.startActivity(intent);
    }

    public static void startAudioOutCall(Context context, String userId) {
        Intent intent = new Intent(context, CallActivity.class);
        intent.putExtra(PRARM_USER_ID, userId);
        intent.putExtra(PRARM_TYPE, TYPE_OUTCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_AUDIO);
        context.startActivity(intent);
    }



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_call;
    }

    @Override
    protected void initMembers() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mSessionType = getIntent().getIntExtra(PRARM_TYPE, 0);
        mStreamType = getIntent().getIntExtra(PRARM_STREAM_TYPE, 0);
        mSession = getIntent().getParcelableExtra(PRARM_SESSION);
        mUserId = getIntent().getStringExtra(PRARM_USER_ID);
        IMCallClient.getInstance().setCallStateListener(this);
        IMCallClient.getInstance().enableSpeakerphone(false);
        IMCallClient.getInstance().muteLocalAudioStream(false);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
    }

    @Override
    protected void findViews() {
        mVideoContainerStub = f(R.id.call_video_container_stub);
        mBottomBarIncall = f(R.id.call_bottom_incall);
        mBottomBarOutcall = f(R.id.call_bottom_outcall);
        mUserInfo = f(R.id.call_user_info);
        mBtnAccept = f(R.id.btn_accept);
        mBtnReject = f(R.id.btn_reject);
        mBtnHangup = f(R.id.btn_hangup);
        mBtnMute = f(R.id.btn_mute);
        mBtnSpeaker = f(R.id.btn_speaker);
        mBtnSwitchCamera = f(R.id.btn_switch_camera);
        mBtnVideo = f(R.id.btn_video);
        mIvUserIcon = f(R.id.call_user_icon);
        mTvUserId = f(R.id.call_user_name);
        mTvState = f(R.id.call_state);
    }

    @Override
    protected void initViews() {
        if (mSessionType == TYPE_INCALL) {
            mBottomBarIncall.setVisibility(View.VISIBLE);
            mBottomBarOutcall.setVisibility(View.GONE);
            mTvState.setText("来电...");
            mTvUserId.setText(mUserId);
            if (mStreamType == STREAM_TYPE_VIDEO) {
                initLocalVideo();
                IMCallClient.getInstance().switchToVideo();
                mBtnVideo.setSelected(true);
            } else {
                mBtnVideo.setSelected(false);
                IMCallClient.getInstance().switchToVideo();
            }
        } else {
            IMCallClient.getInstance().switchToVideo();
            if (mStreamType == STREAM_TYPE_VIDEO) {
                initLocalVideo();
                IMCallClient.getInstance().makeVideoCall(mUserId);
                mBtnVideo.setSelected(true);
            } else {
                Log.d(TAG, "initViews: user:" + mUserId);
                IMCallClient.getInstance().makeAudioCall(mUserId);
                mBtnVideo.setSelected(false);
            }
            mTvUserId.setText(mUserId);
            mBtnSpeaker.setSelected(false);
            mBtnMute.setSelected(false);
            mBottomBarIncall.setVisibility(View.GONE);
            mBottomBarOutcall.setVisibility(View.VISIBLE);
            mTvState.setText("正在呼叫...");
        }

    }

    private void initLocalVideo() {
        mVideoContainer = (GridVideoViewContainer) mVideoContainerStub.inflate();
        SurfaceView surfaceV = IMCallClient.createRendererView(getApplicationContext());
        IMCallClient.getInstance().setLocalVideoView(surfaceV);
        surfaceV.setZOrderOnTop(false);
        surfaceV.setZOrderMediaOverlay(false);
        mUidList.put(0, new SoftReference<>(surfaceV));
        mVideoContainer.initViewContainer(this, 0, mUidList);
    }

    public int mLayoutType = LAYOUT_TYPE_DEFAULT;

    public static final int LAYOUT_TYPE_DEFAULT = 0;

    public static final int LAYOUT_TYPE_SMALL = 1;

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock.setVisibility(View.GONE);
        }
        if (mVideoContainer == null) {
            initLocalVideo();
        }
        mVideoContainer.initViewContainer(this, 0, mUidList);

        mLayoutType = LAYOUT_TYPE_DEFAULT;
    }

    private void switchToSmallVideoView(int bigBgUid) {
        HashMap<Integer, SoftReference<SurfaceView>> slice = new HashMap<>(1);
        slice.put(bigBgUid, mUidList.get(bigBgUid));
        mVideoContainer.initViewContainer(this, bigBgUid, slice);

        bindToSmallVideoView(bigBgUid);

        mLayoutType = LAYOUT_TYPE_SMALL;
    }


    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (LinearLayout) stub.inflate();
        }

        boolean twoWayVideoCall = mUidList.size() == 2;

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, 0, exceptUid, mUidList, null);
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);


        if (twoWayVideoCall) {
            recycler.setLayoutManager(new RtlLinearLayoutManager(this, RtlLinearLayoutManager.HORIZONTAL, false));
        } else {
            recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        recycler.addItemDecoration(new SmallVideoViewDecoration());
        recycler.setAdapter(mSmallVideoViewAdapter);

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.setLocalUid(0);
            mSmallVideoViewAdapter.notifyUiChanged(mUidList, exceptUid, null, null);
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }


    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidList.containsKey(uid)) {
                    return;
                }

                SurfaceView surfaceV = IMCallClient.createRendererView(getApplicationContext());
                mUidList.put(uid, new SoftReference<>(surfaceV));

                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT && mUidList.size() != 2;

                surfaceV.setZOrderOnTop(!useDefaultLayout);
                surfaceV.setZOrderMediaOverlay(!useDefaultLayout);

                IMCallClient.getInstance()
                        .getWorkerThread()
                        .getRtcEngine()
                        .setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                if (useDefaultLayout) {
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }


    private void doHideTargetView(int targetUid, boolean hide) {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(targetUid, hide ? UserStatusData.VIDEO_MUTED : UserStatusData.DEFAULT_STATUS);
        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
            mVideoContainer.notifyUiChanged(mUidList, targetUid, status, null);
        } else if (mLayoutType == LAYOUT_TYPE_SMALL) {
            UserStatusData bigBgUser = mVideoContainer.getItem(0);
            if (bigBgUser.mUid == targetUid) { // big background is target view
                mVideoContainer.notifyUiChanged(mUidList, targetUid, status, null);
            } else { // find target view in small video view list
                Log.w(TAG, "SmallVideoViewAdapter call notifyUiChanged " + mUidList + " " + (bigBgUser.mUid & 0xFFFFFFFFL) + " taget: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status);
                mVideoContainer.notifyUiChanged(mUidList, bigBgUser.mUid, status, null);
            }
        }
    }


    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                Object target = mUidList.remove(uid);
                if (target == null) {
                    return;
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    @Override
    protected void setupEvents() {
        mBtnAccept.setOnClickListener(this);
        mBtnReject.setOnClickListener(this);
        mBtnHangup.setOnClickListener(this);
        mBtnMute.setOnClickListener(this);
        mBtnSpeaker.setOnClickListener(this);
        mBtnSwitchCamera.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        IMCallClient.getInstance().enableLocalPreview(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        IMCallClient.getInstance().enableLocalPreview(false);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        ToastUtil.cancel();
        IMCallClient.getInstance().hangup();
        super.onDestroy();
    }

    @Override
    public void onSessionOver(String sessionId, String userId) {
        Log.d(TAG, "onSessionOver: ");
        finish();
    }

    @Override
    public void onCallOutgoing(AVSession session) {
        Log.d(TAG, "onCallOutgoing: ");
        mTvState.setText("对方正在响铃...");
    }

    @Override
    public void onRejected(String userId) {
        Log.d(TAG, "onRejected: " + userId);
        ToastUtil.showText(userId + "拒绝了你的请求");
        finish();
    }

    @Override
    public void onAccept(String userId) {
        Log.d(TAG, "onAccept: " + userId);
        mTvState.setText("正在通话");
        if (mStreamType == STREAM_TYPE_VIDEO) {
            mUserInfo.setVisibility(View.INVISIBLE);
        } else {
            mUserInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBusy(String userId) {
        Log.d(TAG, "onBusy: " + userId);
        ToastUtil.showText(userId + "用户正忙");
        finish();
    }

    @Override
    public void onTimeOut(List<String> userId) {
        Log.d(TAG, "onTimeOut: ");
    }

    @Override
    public void onHangup(String userId) {
        Log.d(TAG, "onHangup: ");
        finish();
    }

    @Override
    public void onMuteAudioStream(String userId, boolean isMuted) {
        Log.d(TAG, "onMuteAudioStream: " + userId + "  " + isMuted);
        if (isMuted) {
            mTvState.setText(userId + "关闭了麦克风");
            ToastUtil.showText(userId + "关闭了麦克风");
        } else {
            mTvState.setText("正在通话");
            ToastUtil.showText(userId + "正在通话");
        }
    }

    boolean isDisconnected = true;
    @Override
    public void onUserOffline(String userId, int reason) {
        Log.d(TAG, "onUserOffline: " + userId + " -> " + reason);
        isDisconnected = true;
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isDisconnected) {
                            finish();
                        }
                    }
                }, 10000);
        mTvState.setText("对方已掉线");
        if (!isFinishing()) {
            ToastUtil.showText(userId + "掉线, 如10秒内未重连将自动挂断...");
        }
    }

    @Override
    public void onUserConnected(String userId, int uid, int elapsed) {
        Log.d(TAG, "onUserConnected: " + userId + " -> " + elapsed);
        isDisconnected = false;
        mTvState.setText("正在通话");
        ToastUtil.showText(userId + "已连接");
    }

    @Override
    public void onFirstRemoteVideoDecoded(String userId, int uid, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded: uid = " + uid + ", width = " + width);
        doRenderRemoteUi(uid);
    }

    @Override
    public void onFirstRemoteVideoDecoded(String userId, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded: -----------------");

    }

    @Override
    public void onError(int errorCode) {
        Log.e(TAG, "onError: " + errorCode);
        ToastUtil.showText("错误:" + errorCode);
    }

    @Override
    public void onUserOpenVideo(String userId, boolean isOpened) {
        Log.d(TAG, "onUserOpenVideo: " + userId + " : " + isOpened);
        String tmp;
        if (isOpened) {
            tmp = "打开";
        } else {
            tmp = "关闭";
        }
        ToastUtil.showText(userId + tmp + "了摄像头");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                IMCallClient.getInstance().accept();
                mBottomBarIncall.setVisibility(View.GONE);
                mBottomBarOutcall.setVisibility(View.VISIBLE);
                mTvState.setText("正在通话");
                if (mStreamType == STREAM_TYPE_VIDEO) {
                    mUserInfo.setVisibility(View.INVISIBLE);
                } else {
                    mUserInfo.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.btn_reject:
                IMCallClient.getInstance().reject();
                finish();
                break;
            case R.id.btn_hangup:
                IMCallClient.getInstance().hangup();
                finish();
                break;
            case R.id.btn_speaker:
                if (mBtnSpeaker.isSelected()) {
                    mBtnSpeaker.setSelected(false);
                    IMCallClient.getInstance().enableSpeakerphone(false);
                } else {
                    mBtnSpeaker.setSelected(true);
                    IMCallClient.getInstance().enableSpeakerphone(true);
                }
                break;
            case R.id.btn_mute:
                if (mBtnMute.isSelected()) {
                    mBtnMute.setSelected(false);
                    IMCallClient.getInstance().muteLocalAudioStream(false);
                } else {
                    mBtnMute.setSelected(true);
                    IMCallClient.getInstance().muteLocalAudioStream(true);
                }
                break;
            case R.id.btn_switch_camera:
                if (mUserInfo.getVisibility() != View.VISIBLE) {
                    IMCallClient.getInstance().switchCamera();
                }
                break;
            case R.id.btn_video:
                if (mSmallVideoViewDock == null) {
                    ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
                    mSmallVideoViewDock = (LinearLayout) stub.inflate();
                }

                if (mVideoContainer == null) {
                    initLocalVideo();
                }

                if (mBtnVideo.isSelected()) {
                    mVideoContainer.setVisibility(View.GONE);
                    mSmallVideoViewDock.setVisibility(View.GONE);
                    mUserInfo.setVisibility(View.VISIBLE);
                    mBtnVideo.setSelected(false);
                    IMCallClient.getInstance().muteLocalVideo(true);
                } else {
                    mVideoContainer.setVisibility(View.VISIBLE);
                    mSmallVideoViewDock.setVisibility(View.VISIBLE);
                    mUserInfo.setVisibility(View.GONE);
                    mBtnVideo.setSelected(true);
                    IMCallClient.getInstance().muteLocalVideo(false);
                }
            default:
        }
    }
}
*/
