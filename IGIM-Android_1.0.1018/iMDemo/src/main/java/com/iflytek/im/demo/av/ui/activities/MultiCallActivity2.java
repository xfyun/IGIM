/*
package com.iflytek.im.demo.av.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.imcall.IMCallClient;
import com.iflytek.cloud.imcall.listener.CallStateListener;
import com.iflytek.cloud.imcall.model.AVSession;
import com.iflytek.cloud.imcall.model.AVUser;
import com.iflytek.cloud.imcall.views.VideoContainer;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MultiCallActivity2 extends BaseActivity implements View.OnClickListener, CallStateListener {

    private static final String TAG = "MultiCallActivity2";

    public static final int TYPE_INCALL = 1;
    public static final int TYPE_OUTCALL = 2;

    public static final int STREAM_TYPE_AUDIO = 1;
    public static final int STREAM_TYPE_VIDEO = 2;

    public static final String PRARM_USER_ID = "user_id"; // 邀请人Id
    public static final String PRARM_USER_IDS = "user_ids"; // 会话中的用户,发起会话时使用
    public static final String PRARM_SESSION = "session"; // 会话,收到来电时使用
    public static final String PRARM_TYPE = "type";
    public static final String PRARM_STREAM_TYPE = "stream_type";
    public static final String PRARM_GROUP_ID = "group_id";

    private static final int REQUEST_ADD_USERS = 0x01;


    private int mSessionType = 0;
    private int mStreamType = 0;
    private AVSession mSession = null;
    private ArrayList<String> mUserList = null;
    private String mUserId = null;
    private String mGroupId = null;
    private ViewStub mVideoContainerStub;
    private VideoContainer mVideoContainer;

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
    private ImageView mBtnUserList;
    private ImageView mBtnAddPerson;

    private TextView mTvUserId;
    private TextView mTvState;

    private ArrayList<String> mVideoUserList = new ArrayList<>(1);

    public static void startVideoInCall(Context context, AVSession session, String userId, String groupId) {
        Intent intent = new Intent(context, MultiCallActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PRARM_USER_ID, userId);
        intent.putExtra(PRARM_SESSION, session);
        intent.putExtra(PRARM_GROUP_ID, groupId);
        intent.putExtra(PRARM_TYPE, TYPE_INCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_VIDEO);
        context.startActivity(intent);
    }

    public static void startVideoOutCall(Context context, ArrayList<String> userList, String groupId) {
        Intent intent = new Intent(context, MultiCallActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PRARM_USER_IDS, userList);
        intent.putExtra(PRARM_GROUP_ID, groupId);
        intent.putExtra(PRARM_TYPE, TYPE_OUTCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_VIDEO);
        context.startActivity(intent);
    }

    public static void startAudioInCall(Context context, AVSession session, String userId, String groupId) {
        Intent intent = new Intent(context, MultiCallActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PRARM_USER_ID, userId);
        intent.putExtra(PRARM_SESSION, session);
        intent.putExtra(PRARM_GROUP_ID, groupId);
        intent.putExtra(PRARM_TYPE, TYPE_INCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_AUDIO);
        context.startActivity(intent);
    }

    public static void startAudioOutCall(Context context, ArrayList<String> userList, String groupId) {
        Intent intent = new Intent(context, MultiCallActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PRARM_USER_IDS, userList);
        intent.putExtra(PRARM_GROUP_ID, groupId);
        intent.putExtra(PRARM_TYPE, TYPE_OUTCALL);
        intent.putExtra(PRARM_STREAM_TYPE, STREAM_TYPE_AUDIO);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_multi_call2;
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
        mGroupId = getIntent().getStringExtra(PRARM_GROUP_ID);
        mUserList = getIntent().getStringArrayListExtra(PRARM_USER_IDS);
        if (mUserList == null) {
            mUserList = mSession.getUserIdList();
        }
        mVideoUserList.add(IMClient.getInstance().getCurrentUser());
        IMCallClient.getInstance().setCallStateListener(this);
        IMCallClient.getInstance().enableSpeakerphone(false);
        IMCallClient.getInstance().muteLocalAudioStream(false);

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
        mBtnUserList = f(R.id.btn_user_list);
        mBtnAddPerson = f(R.id.btn_add_person);
        mTvUserId = f(R.id.call_user_name);
        mTvState = f(R.id.call_state);
    }

    @Override
    protected void initViews() {

        if (mSessionType == TYPE_INCALL) {
            mBottomBarIncall.setVisibility(View.VISIBLE);
            mBottomBarOutcall.setVisibility(View.GONE);
            mTvState.setText("来电...");
            if (mStreamType == STREAM_TYPE_VIDEO) {
                initLocalVideo();
                IMCallClient.getInstance().switchToVideo();
                mBtnVideo.setSelected(true);
            } else {
                mBtnVideo.setSelected(false);
                IMCallClient.getInstance().switchToVoice();
            }
            isAccepted = false;
            initUserIdUi();

        } else {
            isAccepted = true;
            if (mStreamType == STREAM_TYPE_VIDEO) {
                initLocalVideo();
                IMCallClient.getInstance().switchToVideo();
                IMCallClient.getInstance().makeVideoCall(mUserList, mGroupId);
                mBtnVideo.setSelected(true);
            } else {
                IMCallClient.getInstance().switchToVoice();
                IMCallClient.getInstance().makeAudioCall(mUserList, mGroupId);
                mBtnVideo.setSelected(false);
            }
            initUserIdUi();
            mBtnSpeaker.setSelected(false);
            mBtnMute.setSelected(false);
            mBottomBarIncall.setVisibility(View.GONE);
            mBottomBarOutcall.setVisibility(View.VISIBLE);
        }

    }

    private void initUserIdUi() {
        if (isAccepted) {
            StringBuilder str = new StringBuilder("成员列表:\n");
            ArrayList<AVUser> list = IMCallClient.getInstance().getUserList();
            if (list != null ) {
                for (AVUser user : list) {
                    str.append(user.getUid());
                    str.append(":");
                    str.append(user.getState() == 1 ? "等待" : "在线");
                    str.append('\n');
                }
            } else {
                for (String user : mUserList) {
                    str.append(user);
                    str.append(":等待");
                    str.append('\n');
                }
            }
            mTvUserId.setText(str.toString());
            mTvState.setText(R.string.multi_call_calling);
            if (mStreamType == STREAM_TYPE_VIDEO) {
                mTvUserId.setVisibility(View.INVISIBLE);
            } else {
                mTvUserId.setVisibility(View.VISIBLE);
            }
        } else {
            StringBuilder str = new StringBuilder(mUserId + "邀请你加入多人会话:\n");
            for (AVUser user : IMCallClient.getInstance().getUserList()) {
                str.append(user.getUid());
                str.append(":");
                str.append(user.getState() == 1 ? "等待" : "在线");
                str.append('\n');
            }
            mTvUserId.setText(str.toString());
            mTvState.setText("正在呼叫...");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> userList = data.getStringArrayListExtra(InviteActivity.RESULT_USERS);
                IMCallClient.getInstance().inviteUsers(userList, mGroupId);
                ToastUtil.showText("已邀请" + userList.size() + "个用户");
                initUserIdUi();
            } else {
                ToastUtil.showText("邀请已取消");
            }
        }
    }

    private void initLocalVideo() {
        IMCallClient.getInstance().switchToVideo();
        IMCallClient.getInstance().enableLocalPreview(true);
        mVideoContainer = (VideoContainer) mVideoContainerStub.inflate();
        initVideoViewUi();
        mVideoContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mBottomBarOutcall.getVisibility() == View.VISIBLE) {
                        mBottomBarOutcall.setVisibility(View.INVISIBLE);
                    } else {
                        mBottomBarOutcall.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }
        });
    }

    private void initVideoViewUi() {
        mVideoContainer.init(mVideoUserList);
    }


    private void doRemoveRemoteUi(String uid) {
        if (isFinishing()) {
            return;
        }
        if (!mVideoUserList.remove(uid)) {
            return;
        }
        initVideoViewUi();
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
        mBtnAddPerson.setOnClickListener(this);
        mBtnUserList.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        IMCallClient.getInstance().hangup();
        super.onDestroy();
    }

    @Override
    public void onSessionOver(String sessionId, String userId) {
        Log.d(TAG, "onSessionOver: " + sessionId);
        finish();
    }

    @Override
    public void onCallOutgoing(AVSession session) {
        Log.d(TAG, "onCallOutgoing: " + session.getSessionId());
        initUserIdUi();
    }

    @Override
    public void onRejected(String userId) {
        Log.d(TAG, "onRejected: " + userId);
        Toast.makeText(this, userId + "拒绝了你的请求", Toast.LENGTH_LONG).show();
        initUserIdUi();
    }

    @Override
    public void onAccept(String userId) {
        Log.d(TAG, "onAccept: " + userId);
        initUserIdUi();
    }

    @Override
    public void onBusy(String userId) {
        Log.d(TAG, "onBusy: ");
        Toast.makeText(this, userId + "用户正忙", Toast.LENGTH_LONG).show();
        initUserIdUi();
    }

    @Override
    public void onTimeOut(List<String> userId) {
        Log.d(TAG, "onTimeOut: ");
        initUserIdUi();
    }

    @Override
    public void onHangup(String userId) {
        Log.d(TAG, "onHangup: ");
        initUserIdUi();
        doRemoveRemoteUi(userId);
    }

    @Override
    public void onMuteAudioStream(String userId, boolean isMuted) {
        Log.d(TAG, "onMuteAudioStream: " + userId + "  " + isMuted);
        if (isMuted) {
            mTvState.setText(userId + "关闭了麦克风");
            Toast.makeText(this, userId + "关闭了麦克风", Toast.LENGTH_LONG).show();
        } else {
            mTvState.setText(R.string.multi_call_calling);
            Toast.makeText(this, R.string.multi_call_calling, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUserOffline(String userId, int reason) {
        Log.d(TAG, "onUserOffline: " + userId + " -> " + reason);
        Toast.makeText(this, userId + "掉线", Toast.LENGTH_LONG).show();
        doRemoveRemoteUi(userId);
    }


    @Override
    public void onUserConnected(String userId, int uid, int elapsed) {
        Log.d(TAG, "onUserConnected: " + userId + " -> " + elapsed);
        Toast.makeText(this, userId + "已连接，延迟" + elapsed + "ms", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFirstRemoteVideoDecoded(String userId, int uid, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded: uid = " + uid + ", width = " + width);
    }

    @Override
    public void onFirstRemoteVideoDecoded(String userId, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded: " + userId + " width= " + width + " height= " + height);
        if (!mVideoUserList.contains(userId)) {
            mVideoUserList.add(userId);
        }
        initVideoViewUi();
    }

    @Override
    public void onError(int errorCode) {
        Log.e(TAG, "onError: " + errorCode);
        Toast.makeText(this, "网络或服务器出错:" + errorCode, Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, userId + tmp + "了摄像头", Toast.LENGTH_LONG).show();
    }

    boolean isAccepted = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                IMCallClient.getInstance().accept();
                isAccepted = true;
                mBottomBarIncall.setVisibility(View.GONE);
                mBottomBarOutcall.setVisibility(View.VISIBLE);
                mTvState.setText(R.string.multi_call_calling);
                if (mStreamType == STREAM_TYPE_VIDEO) {
                    mUserInfo.setVisibility(View.INVISIBLE);
                } else {
                    mUserInfo.setVisibility(View.VISIBLE);
                }
                initUserIdUi();

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
                if (mBtnVideo.isSelected()) {
                    IMCallClient.getInstance().switchCamera();
                } else {
                    ToastUtil.showText("请先打开视频");
                }
                break;
            case R.id.btn_video:
                if (mVideoContainer == null) {
                    initLocalVideo();
                }
                if (mBtnVideo.isSelected()) {
                    mBtnVideo.setSelected(false);
                    IMCallClient.getInstance().muteLocalVideo(true);
                    mStreamType = STREAM_TYPE_AUDIO;
                } else {
                    mBtnVideo.setSelected(true);
                    IMCallClient.getInstance().muteLocalVideo(false);
                    mStreamType = STREAM_TYPE_VIDEO;
                }
                break;
            case R.id.btn_add_person:
                int count;
                if (mSessionType == TYPE_INCALL) {
                    count = 5 - IMCallClient.getInstance().getOnlineUserIdList().size();
                } else {
                    count = 5 - mUserList.size() - 1;
                }

                InviteActivity.startForResult(this, REQUEST_ADD_USERS, count, mGroupId,
                        IMCallClient.getInstance().getOnlineUserIdList());
                break;
            default:
        }
    }
}
*/
