package com.iflytek.im.demo.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.User;
import com.iflytek.cloud.im.entity.msg.AudioMsg;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupMsgContent;
import com.iflytek.cloud.im.entity.msg.PostRltText;
import com.iflytek.cloud.im.entity.msg.TextMsg;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.AppManager;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.ui.activity.GroupNotifyActivity;
import com.iflytek.im.demo.ui.activity.LoginActivity;
import com.iflytek.im.demo.ui.activity.MainActivity;
import com.iflytek.im.demo.ui.activity.TalkActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iflytek.im.demo.Constants.Id.IM_TOKEN;
import static com.iflytek.im.demo.Constants.LoginErrorCode.KEY_LOGIN_ERRORCODE;

/**
 * Created by imxqd on 2016/8/29.
 * 通知栏工具
 */

public class NotificationUtil {

    private final static String TAG = "NotificationUtil";

    private static Map<String, Integer> mPersonNotificationCount = new HashMap<>();
    private static Map<String, Integer> mPersonNotificationMsgCount = new HashMap<>();
    private static Context mContext = ImApplication.getApp();
    private static NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    private static final String mNotifyGroupMsgID = "AllGroupMsgNotify";

    private static List<String> mMsgFlag = new ArrayList<String>();
    public static Map<String, Integer> getPersonNotificationCount() {
        return mPersonNotificationCount;
    }

    public static void notifyPerson(CommonMsgContent msg,boolean isTalking) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.logo_300_5)
                .setTicker("新消息提醒：")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND);

        String personID;
        String personName;
        if (msg.getRecvType() ==0) {
            personID = msg.getSender();
            personName = msg.getSender();
        } else {
            personID = msg.getReceiver();
            personName = IMClient.getInstance().getGroupNameByGid(personID);
        }
        PendingIntent pendingIntent;

        int requestCode = 0;
        if (mPersonNotificationCount.containsKey(personID)  && !mMsgFlag.contains(msg.getCMsgID())) {
            Log.d(TAG, "notifyPerson: 有这个personID");
            mMsgFlag.add(msg.getCMsgID());
            requestCode = mPersonNotificationCount.get(personID);
            mPersonNotificationMsgCount.put(personID,mPersonNotificationMsgCount.get(personID)+1);
        } else if(!mMsgFlag.contains(msg.getCMsgID())){
            Log.d(TAG, "notifyPerson: 没有这个personID");
            mMsgFlag.add(msg.getCMsgID());
            requestCode = mPersonNotificationCount.size() + 1;
            mPersonNotificationCount.put(personID, requestCode);
            mPersonNotificationMsgCount.put(personID,1);
        }
        Intent intent = null;
        if(isTalking){
            intent = new Intent(mContext, MainActivity.class);
        }else{
            intent = new Intent(mContext, TalkActivity.class);
        }
        intent.putExtra(Constants.Parameter.KEY_RECEIVER_ID, personID);
        intent.putExtra(Constants.Parameter.KEY_CONVERSATION_TYPE, msg.getRecvType());
        intent.putExtra(Constants.Parameter.KEY_RECEIVER_NAME,personName);
        intent.putExtra(Constants.Parameter.KEY_IS_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        switch (msg.getMsgType()) {
            case 0:
                TextMsg textMsg = null;
                try {
                    textMsg = IMClient.getInstance().parse2TextMsg(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (msg.getPostType() == 0) {
                    builder.setContentIntent(pendingIntent)
                            .setContentText(textMsg.getContent())
                            .setContentTitle(personName);
                } else {
                    builder.setContentIntent(pendingIntent)
                            .setContentText(textMsg.getContent())
                            .setContentTitle(personName)
                            .setContentText(Constants.Message.AUDIO_DATA);
                }
                break;
            case 2:
                AudioMsg audioMsg = null;
                PostRltText postRltText = null;
                try {
                    audioMsg = IMClient.getInstance().parse2AudioMsg(msg);
                    postRltText = IMClient.getInstance().parse2PostTextRlt(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String text = null;
                if (postRltText != null) {
                    text = postRltText.getText();
                    if (TextUtils.isEmpty(text)) {
                        text = "<!-- 听写无内容  -->";
                    }
                }
                if (msg.getPostType() == 0) {
                    builder.setContentTitle(personName)
                            .setContentText(Constants.Message.AUDIO_DATA)
                            .setContentIntent(pendingIntent);
                } else {
                    builder.setContentIntent(pendingIntent)
                            .setContentText(text)
                            .setContentTitle(personName)
                            .setContentIntent(pendingIntent);
                }
                break;
            case 1:

                builder.setContentIntent(pendingIntent)
                        .setContentText(Constants.Message.IMAGE_DATA)
                        .setContentTitle(personName)
                        .setContentIntent(pendingIntent);

                break;
            case 3:
                builder.setContentIntent(pendingIntent)
                        .setContentText(Constants.Message.VIDEO_DATA)
                        .setContentTitle(personName)
                        .setContentIntent(pendingIntent);
                break;
            default:
                break;
        }

        builder.setNumber(mPersonNotificationMsgCount.get(personID));
        builder.setAutoCancel(true); // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示

        int notificationID = requestCode;
        nm.notify(notificationID, builder.build());

/*        mPersonNotificationCount.remove(personID);
        mPersonNotificationMsgCount.remove(personID);*/
    }


    public static void notifyGroupMsg(GroupMsgContent groupMsgContent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.logo_300_5)
                .setTicker("群通知：")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND);
        String content = MsgParseUtil.getGroupMsgContent(groupMsgContent);
        PendingIntent pendingIntent;

        int requestCode = 0;
        if (mPersonNotificationCount.containsKey(mNotifyGroupMsgID)) {
            requestCode = mPersonNotificationCount.get(mNotifyGroupMsgID);
            if(mPersonNotificationMsgCount.get(requestCode) == null){
                mPersonNotificationMsgCount.put(mNotifyGroupMsgID,1);
            }else{
                mPersonNotificationMsgCount.put(mNotifyGroupMsgID,mPersonNotificationMsgCount.get(requestCode)+1);
            }
        } else {
            requestCode = mPersonNotificationCount.size() + 1;
            mPersonNotificationCount.put(mNotifyGroupMsgID, requestCode);
            mPersonNotificationMsgCount.put(mNotifyGroupMsgID,1);

        }
        Intent intent = new Intent(mContext, GroupNotifyActivity.class);
        pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setContentText(content)
                .setContentTitle("群通知");
        builder.setNumber(mPersonNotificationMsgCount.get(mNotifyGroupMsgID));
        builder.setAutoCancel(true);
        nm.notify(requestCode,builder.build());
    }





    public static void cancelNotification(String personID){
        if(mPersonNotificationCount.keySet().contains(personID)){
            int id = mPersonNotificationCount.get(personID);
            nm.cancel(id);
            mPersonNotificationCount.remove(personID);
            mPersonNotificationMsgCount.remove(personID);
            mMsgFlag.clear();
        }
    }

    public static void handleSomeoneOnline() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AppManager.currentActivity());
        builder.setMessage("当前有其他设备上线，如果不是本人操作，请修改密码")
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(AppManager.currentActivity(), LoginActivity.class);
                        AppManager.currentActivity().startActivity(intent);
                        AppManager.currentActivity().finish();
                    }
                })
                .setPositiveButton("重新上线",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                String token = SharePreferenceHelper.getInstance().getString(TOKEN);
//                                if(TextUtils.isEmpty(token)){
//                                    return;
//                                }
                                User user = new User();
                                user.setUid(IMClient.getInstance().getCurrentUser());
                                user.setName(IMClient.getInstance().getCurrentUser());
                                String token = IM_TOKEN;

                                IMClient.getInstance().login(user, true, token, new ResultCallback<String>() {
                                    @Override
                                    public void onError(final int errorCode) {
                                        Intent intent = new Intent(AppManager.currentActivity(), LoginActivity.class);
                                        intent.putExtra(KEY_LOGIN_ERRORCODE, errorCode);
                                        AppManager.currentActivity().startActivity(intent);
                                        AppManager.currentActivity().finish();
                                    }

                                    @Override
                                    public void onSuccess(String datas) {
                                        Intent intent = new Intent(AppManager.currentActivity(), MainActivity.class);
                                        AppManager.currentActivity().startActivity(intent);
                                        AppManager.currentActivity().finish();
                                    }
                                });


                            }
                        });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.setCancelable(false);
        alert.show();
    }


}

