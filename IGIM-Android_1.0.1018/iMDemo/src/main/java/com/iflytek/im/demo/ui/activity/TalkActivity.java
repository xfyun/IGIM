package com.iflytek.im.demo.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.cloud.im.core.util.StringUtil;
import com.iflytek.cloud.im.entity.msg.AudioMsg;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.ImageMsg;
import com.iflytek.cloud.im.entity.msg.MessageConstant;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.entity.msg.OtherSideReadedNotifyMsg;
import com.iflytek.cloud.im.entity.msg.PostRltText;
import com.iflytek.cloud.im.entity.msg.PostRltVoice;
import com.iflytek.cloud.im.entity.msg.TipMsg;
import com.iflytek.cloud.im.entity.msg.VideoMsg;
import com.iflytek.cloud.im.listener.BuildMsgResultCallback;
import com.iflytek.cloud.im.listener.SendMessageCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.ChatLvAdapter2;
import com.iflytek.im.demo.bean.ChatInfo;
import com.iflytek.im.demo.bean.ImageBean;
import com.iflytek.im.demo.common.NotificationUtil;
import com.iflytek.im.demo.common.ProcessUtil;
import com.iflytek.im.demo.common.SoftInputUtil;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.common.imageUtil.BitmapUtils;
import com.iflytek.im.demo.ui.fragment.ConversationBottomEmoj;
import com.iflytek.im.demo.ui.fragment.ConversationBottomFragment;
import com.iflytek.im.demo.ui.view.DropdownListView;
import com.iflytek.im.demo.ui.view.DropdownListView.OnRefreshListenerHeader;

import org.json.JSONException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_MSG_TYPE_AUDIO;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_MSG_TYPE_IMG;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_MSG_TYPE_TEXT;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_MSG_TYPE_VIDEO;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_POSTTYPE_2_TEXT;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_POSTTYPE_2_VOICE;
import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_POSTTYPE_DEFAULT;

public class TalkActivity extends BaseActivity implements OnRefreshListenerHeader,
        ConversationBottomFragment.FragmentCallback, DropdownListView.onFinishCallback {

    private static final int REQUEST_INVITE_AUDIO = 8;
    private static final int REQUEST_INVITE_VIDEO = 9;
    private final String TAG = "TalkActivity";
    private static final long SEND_MSG_RESPONSE_TIME = 5 * 1000;


    // 这个Activity的属性
    private String mConnectPersonID;
    private int mConversationType;
    private String mConnectpersonName;
    private boolean mIsNotification;


    // 界面布局的变量
    private DropdownListView mMsgListView;
    private ConversationBottomEmoj mConvBottomEmojFrag;
    private RelativeLayout mRelativeParent;

    // 数值变量
    private LinkedList<ChatInfo> mChatInfoList;
    private ChatLvAdapter2 mChatLvAdapter;
    private MessageContent mNewestMsg; // 最新的一条消息
    private boolean isShare;
    private String mShareText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTalkActivityAttr();
        initView();
        initData();
        initListener();
        adaptFitsSystemWindows(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTalkActivityAttr();
        if (mConversationType == 0) {
            setTitle(mConnectPersonID);
        }
        NotificationUtil.cancelNotification(mConnectPersonID);
        sendSharedMsg();
        sendReadReceipt();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        removeNotReadNum();
        IMClient.getInstance().stopPlay();
        sendReadReceipt();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.conversation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mConversationType == 1) {
            getMenuInflater().inflate(R.menu.menu_talk, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            Intent intent = new Intent(TalkActivity.this, MoreGroupInfoActivity.class);
            intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, mConnectPersonID);
            startActivityForResult(intent, Constants.RequestCodeAndResultCode.EXIT_GROUP_REQUESTCODE);
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void findViews() {
        super.findViews();
    }

    private void initView() {
        setTitle(mConnectpersonName);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mRelativeParent = (RelativeLayout) findViewById(R.id.chat_main);
        mMsgListView = (DropdownListView) findViewById(R.id.message_chat_listview);
        mConvBottomEmojFrag = (ConversationBottomEmoj) getFragmentManager().findFragmentById(R.id.conv_bottom_menu);
    }


    private void initData() {
        Log.d(TAG, "initData");
        mChatInfoList = new LinkedList<ChatInfo>();
        mChatLvAdapter = new ChatLvAdapter2(this, mChatInfoList, mConversationType);

        getAssignedMessage(mConnectPersonID, 20, null);
        //mChatLvAdapter.setList(mChatInfoList);
        mMsgListView.setAdapter(mChatLvAdapter);
    }

    private void initListener() {
        mMsgListView.setOnRefreshListenerHead(this);
        mMsgListView.setOnFinishCallback(this);
        mRelativeParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mChatLvAdapter.setAdapterCallBack(new ChatLvAdapter2.AdapterCallBack() {
            @Override
            public void reSendMsg(int position, CommonMsgContent msg) {
                int type = msg.getMsgType();
                try {
                    switch (type) {
                        case MESSAGE_CONTENT_MSG_TYPE_TEXT:
                            resendTextMsg(position,msg);
                            break;
                        case MESSAGE_CONTENT_MSG_TYPE_AUDIO:
                            resendAudioMsg(position, msg);
                            break;
                        case MESSAGE_CONTENT_MSG_TYPE_IMG:
                            resendImagMsg(position, msg);
                            break;
                        case MESSAGE_CONTENT_MSG_TYPE_VIDEO:
                            resendVideoMsg(position, msg);
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }


    /**
     * 获取指定条数消息。
     *
     * @param userName 联系人的姓名
     */

    private void getAssignedMessage(String userName, int count, MessageContent msg) {
        List<MessageContent> msgs = IMClient.getInstance().pagedQueryMsgByChatId(userName, count, msg);
//        for(int i =0; i<msgs.size();i++) {
//            Log.d(TAG, "getAssignedMessage: msg:" + msgs.get(i));
//        }
//        int oldSize = mChatInfoList.size();
        if (msgs != null && msgs.size() > 0) {
            if (mNewestMsg != null) {
                mNewestMsg = msgs.get(0).getSeqID() > mNewestMsg.getSeqID() ? msgs.get(0) : mNewestMsg;
            } else {
                mNewestMsg = msgs.get(0);
            }
            mChatInfoList.clear();
            for (int i = msgs.size() - 1; i >= 0; i--) {
                try {
                    MessageContent msg2 = msgs.get(i);
                    if (msg2 instanceof CommonMsgContent) {
                        mChatInfoList.add(new ChatInfo((CommonMsgContent) msg2));
                        Log.d(TAG, "CommonMsgContent:" + (CommonMsgContent) msg2);
                    } else if (msg2 instanceof TipMsg) {
                        mChatInfoList.add(new ChatInfo((TipMsg) msg2));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //接收消息处理
    @Subscribe(tags = {@Tag(Constants.Event.NEW_MESSAGE_IN)})
    public void onNewMessageIn(MessageContent msgContent) {
        Log.d(TAG, "There is one msg arriving demo");
        if (msgContent instanceof CommonMsgContent) {
            //normal msg
            notifyNormalMsg(msgContent);
            Log.d(TAG, "onNewMessageIn: " + msgContent);
        } else if (msgContent instanceof TipMsg) {
            notifyTipMsg(msgContent);
        } else if (msgContent instanceof OtherSideReadedNotifyMsg) {
            refresh();
        } else {
            mChatLvAdapter.notifyDataSetChanged();
        }
    }


    private void refresh() {
        getAssignedMessage(mConnectPersonID, mChatInfoList.size(), null);
        notifyAdapterDataChanged(null, 0);
    }

    private void notifyNormalMsg(MessageContent messageContent) {
        CommonMsgContent msg = (CommonMsgContent) messageContent;
        Log.d(TAG, " msgSender:" + msg.getSender() +
                " msgReceiver=" + msg.getReceiver() +
                " msgContent= " + msg.getMsgBody()
        );

        if (mNewestMsg != null) {
            mNewestMsg = msg.getSeqID() > mNewestMsg.getSeqID() ? msg : mNewestMsg;
        } else {
            mNewestMsg = msg;
        }
        //我发送的消息  然后收到的ACK消息，我接收到当前对话者的消息
        if (msg.getReceiver().equals(mConnectPersonID)
                || (msg.getReceiver().equals(IMClient.getInstance().getCurrentUser()))) {
            Log.d(TAG, "the msg is for this session");
            try {
                updateChatInfoList(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!ProcessUtil.isBackground(this, getPackageName())) {
                sendReadReceipt();
            } else {
                Log.d(TAG, "onNewMessageIn: 不在talk界面");
            }
            notifyAdapterDataChanged(null, msg.getState());

        } else if (!msg.getSender().equals(IMClient.getInstance().getCurrentUser())) {
            //我接收的消息
            Log.d(TAG, "notifyPerson: talkactivity  通知消息");
            NotificationUtil.notifyPerson(msg, true);
        }
    }

    private void notifyTipMsg(MessageContent msgContent) {
        TipMsg tipMsg = (TipMsg) msgContent;
        String reciever = tipMsg.getReceiver();
        if (!TextUtils.isEmpty(reciever) && reciever.equals(mConnectPersonID)) {
            try {
                updateChatInfoList(tipMsg);
                if (mNewestMsg != null) {
                    mNewestMsg = tipMsg.getSeqID() > mNewestMsg.getSeqID() ? tipMsg : mNewestMsg;
                } else {
                    mNewestMsg = tipMsg;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mChatLvAdapter.notifyDataSetInvalidated();
        }
    }


    @Subscribe(tags = {@Tag(Constants.Event.SYNC_MESSAGE_SUCCESS)})
    public void onSyncMessage(MessageContent... msgs) {
        Log.d(TAG, "获取离线消息");
        getAssignedMessage(mConnectPersonID, 20, null);
        mChatLvAdapter.notifyDataSetInvalidated();
    }


    private void updateChatInfoList(MessageContent msgContent) throws JSONException {
        if (msgContent instanceof CommonMsgContent) {
            CommonMsgContent msg = (CommonMsgContent) msgContent;
            boolean isPostRltBack = false;
            int size = mChatInfoList.size();
            for (int i = 0; i < size; i++) {
                ChatInfo chatInfo = mChatInfoList.get(i);
                if (chatInfo.getMsgID().equals(msg.getCMsgID())) {
                    isPostRltBack = true;
                    PostRltVoice postRltVoice = null;
                    PostRltText postRltText = null;
                    if (msg.getPostType() == MESSAGE_CONTENT_POSTTYPE_2_VOICE) {
                        //文字转语音
                        postRltVoice = IMClient.getInstance().parse2PostVoiceRlt(msg);
//                    final String url = postRltVoice.getPostRlt_url();
                        final String fid = postRltVoice.getPostRlt_fid();
                        final String fmt = postRltVoice.getPostRlt_fmt();
                        String audioName = StringUtil.Md5(fid);// + ".wav";
                        chatInfo.setFileFid(fid);
                        chatInfo.setFileName(audioName);
                        chatInfo.setPostType(msg.getPostType());
                        chatInfo.setMsg(msg);
                        Log.d(TAG, "filePath=" + audioName);
                    } else if (msg.getPostType() == MESSAGE_CONTENT_POSTTYPE_2_TEXT) {
                        chatInfo.setPostType(msg.getPostType());
                        postRltText = IMClient.getInstance().parse2PostTextRlt(msg);
                        Log.d(TAG, "updateChatInfoList: postRlttext:" + postRltText);
                        chatInfo.setContent(postRltText.getText());
                        chatInfo.setMsg(msg);
                    }
                    break;
                }
            }

            if (!isPostRltBack) {
                mChatInfoList.add(new ChatInfo(msg));
            }
        } else if (msgContent instanceof TipMsg) {
            TipMsg tipMsg = (TipMsg) msgContent;
            mChatInfoList.add(new ChatInfo(tipMsg));
        }

    }

    private void setTalkActivityAttr() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mConversationType = bundle.getInt(Constants.Parameter.KEY_CONVERSATION_TYPE);
        mConnectPersonID = bundle.getString(Constants.Parameter.KEY_RECEIVER_ID);
        mConnectpersonName = bundle.getString(Constants.Parameter.KEY_RECEIVER_NAME);
        mIsNotification = bundle.getBoolean(Constants.Parameter.KEY_IS_NOTIFICATION);
        Log.d(TAG, "receiver::" + mConnectPersonID + "  mConversationType == " + mConversationType);

        isShare = bundle.getBoolean(Constants.Parameter.KEY_IS_SHARE);
        mShareText = bundle.getString(Constants.Parameter.KEY_TEXT);
        sendReadReceipt();
        sendReadReceipt();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            } else if (mConvBottomEmojFrag.getEmoj().isShown()) {
                v = mConvBottomEmojFrag.getEmoj();
                if (isShouldEmojHideInput(v, ev)) {
                    mConvBottomEmojFrag.hideEmoj();
                }
            } else if (mConvBottomEmojFrag.getOtherFun().isShown()) {
                v = mConvBottomEmojFrag.getOtherFun();
                if (isShouldEmojHideInput(v, ev)) {
                    mConvBottomEmojFrag.hideOtherFun();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean isShouldEmojHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof LinearLayout)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            View v2 = (TextView) mConvBottomEmojFrag.getText();
            int[] leftTop2 = {0, 0};
            //获取输入框当前的location位置
            v2.getLocationInWindow(leftTop);
            int left2 = leftTop[0];
            int top2 = leftTop[1];
            int bottom2 = top + v2.getHeight();
            int right2 = left + v2.getWidth();


            if (event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else if (event.getY() > top2 && event.getY() < bottom2) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean isShouldOtherFunHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof TableLayout)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            View v2 = (TextView) mConvBottomEmojFrag.getText();
            int[] leftTop2 = {0, 0};
            //获取输入框当前的location位置
            v2.getLocationInWindow(leftTop);
            int left2 = leftTop[0];
            int top2 = leftTop[1];
            int bottom2 = top + v2.getHeight();
            int right2 = left + v2.getWidth();


            if (event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else if (event.getY() > top2 && event.getY() < bottom2) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void adaptFitsSystemWindows(final boolean isTranslucentStatusFitSystemWindowTrue) {
        if (isTranslucentStatusFitSystemWindowTrue &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            findViewById(R.id.chat_main).setFitsSystemWindows(true);
        }
    }

    private void notifyAdapterDataChanged(String msgId, int sendStatus) {

        if (msgId != null) {
            Log.d(TAG, "ACK 开始寻找chatinfo");
            for (int i = mChatInfoList.size() - 1; i >= 0; i--) {
                ChatInfo chatInfo = mChatInfoList.get(i);
                if (chatInfo != null && chatInfo.getMsgID() != null && chatInfo.getMsgID().equals(msgId)) {
                    chatInfo.setSendStatus(sendStatus);
                    mChatLvAdapter.notifyDataSetInvalidated();
                    Log.d(TAG, "ACK　　更新adapter");
                    break;
                }
            }
        } else {
            mChatLvAdapter.notifyDataSetChanged();
        }
    }

    private void removeNotReadNum() {
        if (IMClient.getInstance().updateMsgAsReaded(mConnectPersonID)) {
            Log.d(TAG, "未读消息更新成功");
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }


    // 下拉对话列表时，实现数据加载
    @Override
    public void onRefresh() {
        int count = mChatInfoList.size() + 20;
        int oldSize = mChatInfoList.size();
        getAssignedMessage(mConnectPersonID, count, null);
        int newSize = mChatInfoList.size();
        if (oldSize == newSize) {
            mMsgListView.setSelection(0);
        } else if (oldSize < newSize) {
            mMsgListView.setSelection(newSize - oldSize - 2);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: selection==" + mMsgListView.getSelectedItemPosition());


        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void finish() {
        SoftInputUtil.hideSoftInputView(this);
        Log.d("TalkActivity", "misnotification:" + mIsNotification);
        if (mIsNotification || isShare) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        super.finish();
    }


    public void resendTextMsg(int position, CommonMsgContent msg) throws JSONException {
        mChatInfoList.remove(position);
        sendMsg(msg);
        mChatInfoList.add(new ChatInfo(msg));
        mChatLvAdapter.notifyDataSetInvalidated();
    }

    public void resendAudioMsg(int position, CommonMsgContent msg) throws JSONException {
        AudioMsg audioMsg = null;
        try {
            audioMsg = IMClient.getInstance().parse2AudioMsg(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String filePath = FileConstant.getPathAudio() + "/" + audioMsg.getName();
        String fileName = audioMsg.getName();
        String fmt = audioMsg.getFormat();
        rename(filePath, fileName, fmt);

        String newFilePath = filePath + "." + audioMsg.getFormat();
        String newFileName = fileName + "." + audioMsg.getFormat();


        boolean isNeed2Txt = msg.getPostType() == 2;
        Log.d(TAG, "reSendMsg: isNeed2Txt:" + isNeed2Txt);

        sendAudioMsg(newFilePath, newFileName, isNeed2Txt);
        IMClient.getInstance().delMsgById(msg.getCMsgID());
        mChatInfoList.remove(position);
        mChatLvAdapter.notifyDataSetChanged();

    }

    public void resendImagMsg(int position, CommonMsgContent msg) throws JSONException {
        ImageMsg imageMsg = null;
        try {
            imageMsg = IMClient.getInstance().parse2ImageMsg(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String filePath = FileConstant.getPathImg() + "/" + imageMsg.getName();
        String fileName = imageMsg.getName();
        String fmt = imageMsg.getFormat();

        rename(filePath, fileName, fmt);

        String newFilePath = filePath + "." + fmt;
        String newFileName = fileName + "." + fmt;


        ImageBean imageBean = new ImageBean();
        imageBean.setPath(newFilePath);
        imageBean.setName(newFileName);
        imageBean.setImgDir(FileConstant.getPathImg());

        String imgNameNoFmt = newFileName.substring(0, newFileName.indexOf("."));
        imageBean.setNameNoFmt(imgNameNoFmt);
        sendImgMsg(imageBean);

        IMClient.getInstance().delMsgById(msg.getCMsgID());

        mChatInfoList.remove(position);
        mChatLvAdapter.notifyDataSetChanged();
    }

    public void resendVideoMsg(int postion, CommonMsgContent msg) throws JSONException {
        VideoMsg videomsg = null;
        try {
            videomsg = IMClient.getInstance().parse2VideoMsg(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String fileName = videomsg.getName();
        String filePath = FileConstant.getPathVideo() + "/" + fileName;
        String fmt = videomsg.getFormat();
        String thumbName = videomsg.getThumbnailName();
        String thumbPath = FileConstant.getPathVideo() + "/" + thumbName;
        String thumbFmt = videomsg.getThumbnailFmt();

        rename(filePath, fileName, fmt);
        rename(thumbPath, thumbName, thumbFmt);

        String newFilePath = filePath + "." + fmt;
        String newThumbPath = thumbPath + "." + thumbFmt;
        int dur = videomsg.getDuration();

        sendVideoMsg(newFilePath, newThumbPath, dur);

        IMClient.getInstance().delMsgById(msg.getCMsgID());

        mChatInfoList.remove(postion);
        mChatLvAdapter.notifyDataSetChanged();
    }

    private void rename(String oldPath, String oldName, String fmt) {
        //需要更换文件名，因为构建消息的文件路径要加后缀
        File file = new File(oldPath);
        //原本的格式是什么，就加什么后缀
        if (file.renameTo(new File(oldPath + "." + fmt))) {
            Log.d(TAG, "resendImagMsg: 更新名称正确");
        } else {
            Log.d(TAG, "resendImagMsg: 更新名称错误");
        }

    }


    public void sendTextMsg(final String text, int txtPostType) {
        boolean isGroup = mConversationType == 0 ? false : true;
        String ext = "send text msg";
        final CommonMsgContent msg = IMClient.getInstance().buildTextMsg(mConnectPersonID, text, isGroup, txtPostType, ext);
        Log.d(TAG, "msg content：" + text + "msg.state= " + msg.getState());
        if (msg == null) {
            ToastUtil.showText("发送文字失败");
            return;
        }
        String msgId = msg.getCMsgID();
        try {
            mChatInfoList.add(new ChatInfo(msg));
        } catch (JSONException e) {
            Log.e(TAG, "add to chatinfo list failed");
        }
        mChatLvAdapter.notifyDataSetInvalidated();
        sendMsg(msg);
        mConvBottomEmojFrag.sendTextMsgSuccuss();
    }

    public void sendAudioMsg(String audioFilePath, final String audioFileName, boolean isNeed2Txt) {

        boolean isGroup = mConversationType == 0 ? false : true;
        int postType = isNeed2Txt ? 2 : 0;
        //上传成功才有显示，不合理。
        Log.d(TAG, "PostType = " + postType);
        Log.d(TAG, "audioFilePath:" + audioFilePath + " audioFileName:" + audioFileName);
        String ext = "send audio msg";
        final CommonMsgContent commonMsgContent = IMClient.getInstance().buildAudioMsg(mConnectPersonID, audioFilePath, isGroup, postType, ext, new BuildMsgResultCallback<CommonMsgContent>() {

            @Override
            public void onSuccess(final CommonMsgContent msg) {
                sendMsg(msg);
            }


            @Override
            public void onError(Object msgID, int errorCode) {
                // TODO Auto-generated method stub
                Log.d(TAG, "上传失败");
                String messageId = (String) msgID;
                notifyAdapterDataChanged(messageId, MessageConstant.MESSAGE_CONTENT_SEND_UN);
            }
        });
        if (commonMsgContent == null) {
            ToastUtil.showText("发送语音失败");
            return;
        }

        try {
            mChatInfoList.add(new ChatInfo(commonMsgContent));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mChatLvAdapter.notifyDataSetInvalidated();
    }

    public void sendImgMsg(ImageBean imageBean) {
        String imgPath = imageBean.getPath();
        final String compressedImgName = imageBean.getNameNoFmt().concat(Constants.Storage.SUFFIX_JPG);
        final String compressedImgPathDir = FileConstant.getPathImg() + "/";
        final String compressedImgPath = compressedImgPathDir + compressedImgName;
        BitmapUtils.saveCompressedBitmapToFile(imgPath, compressedImgName, compressedImgPathDir);
        boolean isGroup = mConversationType == 0 ? false : true;
        String ext = "send image msg";

        final CommonMsgContent commonMsgContent = IMClient.getInstance().buildImgMsg(mConnectPersonID, compressedImgPath, isGroup, ext, new BuildMsgResultCallback<CommonMsgContent>() {

            @Override
            public void onError(Object msgID, int errorCode) {
                // TODO Auto-generated method stub
                Log.d(TAG, "上传失败");
                ToastUtil.showText("发送图片失败");
                String messageId = (String) msgID;
                notifyAdapterDataChanged(messageId, MessageConstant.MESSAGE_CONTENT_SEND_UN);
            }


            @Override
            public void onSuccess(CommonMsgContent datas) {
                sendMsg(datas);
            }


        });

        if (commonMsgContent == null) {
            ToastUtil.showText("发送图片失败");
            return;
        }
        try {
            mChatInfoList.add(new ChatInfo(commonMsgContent));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mChatLvAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void sendVideoMsg(String videoPath, String thumbnailPath, int duration) {

        boolean isGroup = mConversationType == 0 ? false : true;
        String ext = "send video msg";

        CommonMsgContent commonMsgContent = IMClient.getInstance().buildvideoMsg(mConnectPersonID, isGroup, videoPath, thumbnailPath, ext, new BuildMsgResultCallback<CommonMsgContent>() {
            @Override
            public void onError(Object msgID, int errorCode) {
                Log.d(TAG, "构建消息错误，errorCode1=" + errorCode);
                String messageId = (String) msgID;
                notifyAdapterDataChanged(messageId, MessageConstant.MESSAGE_CONTENT_SEND_UN);
            }

            @Override
            public void onSuccess(CommonMsgContent datas) {
                sendMsg(datas);
            }
        });
        if (commonMsgContent == null) {
            ToastUtil.showText("发送视频失败");
            return;
        }
        try {
            mChatInfoList.add(new ChatInfo(commonMsgContent));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mChatLvAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onVoiceCallClick() {
//        if (mConversationType == 0) {
//            CallActivity.startAudioOutCall(this, mConnectPersonID);
//        } else {
//            InviteActivity.startForResult(this, REQUEST_INVITE_AUDIO, 5, mConnectPersonID, null);
//        }
    }

    @Override
    public void onVideoCallClick() {
//        if (mConversationType == 0) {
//            CallActivity.startVideoOutCall(this, mConnectPersonID);
//        } else {
//            InviteActivity.startForResult(this, REQUEST_INVITE_VIDEO, 5, mConnectPersonID, null);
//        }
    }

    public void sendMsg(final CommonMsgContent msg) {
        Log.d(TAG, "sendMsg: msg::" + msg);
        IMClient.getInstance().sendMessage(msg, SEND_MSG_RESPONSE_TIME, new SendMessageCallback<String>() {
            @Override
            public void onFaile(String messageId, int errorCode) {
                notifyAdapterDataChanged(messageId, MessageConstant.MESSAGE_CONTENT_SEND_UN);
            }

            @Override
            public void onSuccess(String messageId, long seqID) {
                msg.setSeqID(seqID);
                if (mNewestMsg != null)
                    mNewestMsg = msg.getSeqID() > mNewestMsg.getSeqID() ? msg : mNewestMsg;
                else
                    mNewestMsg = msg;
                Log.d(TAG, "ACK  开始改变界面");
                notifyAdapterDataChanged(messageId, MessageConstant.MESSAGE_CONTENT_SEND_DONE);
            }
        });

    }

    private void sendShareText(String text) {
        sendTextMsg(text, MESSAGE_CONTENT_POSTTYPE_DEFAULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_INVITE_AUDIO && resultCode == RESULT_OK) {
//            ArrayList<String> users = data.getStringArrayListExtra(InviteActivity.RESULT_USERS);
//            MultiCallActivity.startAudioOutCall(this, users, mConnectPersonID);
//        } else if (requestCode == REQUEST_INVITE_VIDEO && resultCode == RESULT_OK) {
//            ArrayList<String> users = data.getStringArrayListExtra(InviteActivity.RESULT_USERS);
//            MultiCallActivity.startVideoOutCall(this, users, mConnectPersonID);
//        }

        if (resultCode == Constants.RequestCodeAndResultCode.EXIT_GROUP_RESULTCODE) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void sendReadReceipt() {
        if (mNewestMsg == null) {
            return;
        }

        IMClient.getInstance().sendReadReceipt(mNewestMsg, 10 * 1000);
        Log.d(TAG, "sendReadReceipt: mNewestMsg:" + mNewestMsg);
    }

    private void sendSharedMsg() {
        if (isShare) {
            sendTextMsg(mShareText, MESSAGE_CONTENT_POSTTYPE_DEFAULT);
        }
    }


}
