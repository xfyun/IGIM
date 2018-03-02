package com.iflytek.im.demo.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.cloud.im.entity.DownloadInfo;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.OtherSideReadedNotifyMsg;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.bean.ChatInfo;
import com.iflytek.im.demo.common.DisplayUtil;
import com.iflytek.im.demo.common.ExpressionController;
import com.iflytek.im.demo.common.Logging;
import com.iflytek.im.demo.common.PopWindowUtil;
import com.iflytek.im.demo.common.ShareUtil;
import com.iflytek.im.demo.common.TimeStringUtil;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.common.imageUtil.BitmapUtils;
import com.iflytek.im.demo.listener.AudioPlayListener;
import com.iflytek.im.demo.listener.VideoPlayListener;
import com.iflytek.im.demo.ui.activity.ImageShowActivity;
import com.iflytek.im.demo.ui.view.ChatItemView;
import com.nineoldandroids.view.ViewHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.iflytek.im.demo.Constants.OtherSideRead.READED;
import static com.iflytek.im.demo.Constants.SendStatus.SENDED;
import static com.iflytek.im.demo.Constants.SendStatus.SENDING;
import static com.iflytek.im.demo.Constants.SendStatus.SEND_DEFAULT;
import static com.iflytek.im.demo.Constants.SendStatus.UNSEND;

@SuppressLint({"NewApi", "InflateParams", "ShowToast"})
public class ChatLvAdapter2 extends BaseAdapter {
    private static final String TAG = "ChatLvAdapter";
    private static final int TYPE_COUNT = 4;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMG = 1;
    private static final int TYPE_AUDIO = 2;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_TIP_BASE = 100;
    private static final int TYPE_UNKNOW = -1;

    private int delCount = 0;

    public interface AdapterCallBack {
        void reSendMsg(int position, CommonMsgContent msg);
    }

    private View mConvertView;
    private Context mContext;
    private List<ChatInfo> mChatInfoList;
    private AdapterCallBack adapterCallBack;
    /**
     * 弹出的更多选择框
     */
    private PopupWindow mPopupWindow;

    /**
     * 复制，删除
     */
    private TextView mCopyTv, mDeleteTv;

    private LayoutInflater mInflater;
    /**
     * 执行动画的时间
     */
    protected static final long mAnimationTime = 150;
    private int type;  //标记当前是群聊（1）还是单聊（0）

    @Override
    public int getItemViewType(int position) {
        int msgType = mChatInfoList.get(position).getMsgType();
        switch (msgType) {
            case 0:
                return TYPE_TEXT;
            case 1:
                return TYPE_IMG;
            case 2:
                return TYPE_AUDIO;
            case 3:
                return TYPE_VIDEO;
            default:
                break;
        }
        return TYPE_UNKNOW;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    public ChatLvAdapter2(Context mContext, List<ChatInfo> list, int type) {
        super();
        this.mContext = mContext;
        this.mChatInfoList = list;
        this.type = type;
        mInflater = LayoutInflater.from(mContext);
    }

    public List<ChatInfo> getmChatInfoList() {
        return mChatInfoList;
    }

    public void setList(List<ChatInfo> list) {
        this.mChatInfoList = list;
    }

    @Override
    public int getCount() {
        return mChatInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setAdapterCallBack(AdapterCallBack sendMsgCall) {
        this.adapterCallBack = sendMsgCall;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_lv_item2, null);

        mConvertView = convertView;
        final ChatItemView chatItemView = new ChatItemView(convertView);
        final ChatInfo chatInfo = mChatInfoList.get(position);
        if(mChatInfoList.size() - position <= delCount) {
            Log.d(TAG, "getView: cha=" + (mChatInfoList.size() - position));
            chatItemView.friendContainer.setVisibility(View.GONE);
            chatItemView.myContainer.setVisibility(View.GONE);
            chatItemView.time.setVisibility(View.GONE);
            return convertView;
        }
        //时间显示
        if (position != 0) {
            ChatInfo lastChatInfo = mChatInfoList.get(position - 1);
            //时间差距是40s
            if (chatInfo.getTime() - lastChatInfo.getTime() > 40000) {
                chatItemView.time.setText(TimeStringUtil.getShortDateTimeString(chatInfo.getTime()));
            } else {

                chatItemView.time.setText(TimeStringUtil.getShortDateTimeString(chatInfo.getTime()));
                chatItemView.time.setVisibility(View.GONE);
            }
        } else {
            chatItemView.time.setText(TimeStringUtil.getShortDateTimeString(chatInfo.getTime()));
        }

        //聊天的人的头像和名字显示
        chatItemView.connectPerson.setText(chatInfo.getNameFrom());

        //接收到消息内容显示
        if (chatInfo.getIsSend() == 0) {
            //接收到消息显示】
            chatItemView.friendContainer.setVisibility(View.VISIBLE);
            chatItemView.myContainer.setVisibility(View.GONE);
        } else {
            //发送的消息显示
            chatItemView.myContainer.setVisibility(View.VISIBLE);
            chatItemView.friendContainer.setVisibility(View.GONE);
        }

        if (chatInfo.getSendStatus() == UNSEND) {
            //未发送
            chatItemView.sendMsgHint.setVisibility(View.VISIBLE);
            chatItemView.sendMsgHint.setBackgroundResource(R.drawable.z0);
            chatItemView.sendMsgHint.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterCallBack.reSendMsg(position, chatInfo.getMsg());
                }
            });
        } else if (chatInfo.getSendStatus() == SENDING) {
            //正在发送
            chatItemView.sendMsgHint.setVisibility(View.VISIBLE);
            chatItemView.sendMsgHint.setBackgroundResource(R.drawable.send_msg_hint);
            AnimationDrawable animationDrawable = (AnimationDrawable) chatItemView.sendMsgHint.getBackground();
            animationDrawable.start();
        } else if (chatInfo.getSendStatus() == SENDED || chatInfo.getSendStatus() == SEND_DEFAULT) {
            //已经发送
            chatItemView.sendMsgHint.setVisibility(View.GONE);
            if (type == 0) {
                if (chatInfo.getOtherSideRead() == READED) {
                    chatItemView.otherSideHint.setText("已读");
                    chatItemView.otherSideHint.setTextColor(Color.GRAY);
                    chatItemView.otherSideHint.setVisibility(View.VISIBLE);
                } else {
                    chatItemView.otherSideHint.setText("未读");
                    chatItemView.otherSideHint.setTextColor(Color.BLUE);
                    chatItemView.otherSideHint.setVisibility(View.VISIBLE);
                }
            }
        }


        showDifferentView(chatItemView, chatInfo, parent);
        return convertView;
    }


    public void showDifferentView(final ChatItemView chatItemView, final ChatInfo chatInfo, ViewGroup parent) {
        final int width = DisplayUtil.getScreenWidth() / 3;
        if (chatInfo.getMsgType() > TYPE_TIP_BASE) {
            showTipView(chatItemView, chatInfo);

            return;
        }
        switch (chatInfo.getMsgType()) {
            case TYPE_TEXT:
                showTextView(chatItemView, chatInfo);
                break;
            case TYPE_IMG:
                showImageView(chatItemView, chatInfo, width);
                break;
            case TYPE_AUDIO:
                showAudioView(chatItemView, chatInfo);
                break;
            case TYPE_VIDEO:
                showVideoView(chatItemView, chatInfo, width);
                break;
            default:
                break;
        }
    }

    private void showTipView(final ChatItemView chatItemView, final ChatInfo chatInfo) {
        chatItemView.tipMsgContent.setText((String) chatInfo.getContent());
        chatItemView.time.setVisibility(View.VISIBLE);
        chatItemView.tipMsgContent.setVisibility(View.VISIBLE);
        chatItemView.friendContainer.setVisibility(View.GONE);
        chatItemView.myContainer.setVisibility(View.GONE);


    }

    private void showTextView(final ChatItemView chatItemView, final ChatInfo chatInfo) {
        SpannableStringBuilder sb = null;
        if (chatInfo.getContent() != null) {
            sb = ExpressionController.expressionBuilder((String) chatInfo.getContent(), mContext);
            // 对内容做处理
            if (TextUtils.isEmpty(sb.toString())) {
                sb = ExpressionController.expressionBuilder("<!--文本内容为空-->", mContext);
            }
            chatItemView.friendTextContent.setText(sb);
            chatItemView.myTextContent.setText(sb);
        }


        if (chatInfo.getFileName() != null) {
            chatItemView.friendImgContent.setBackgroundResource(R.drawable.voice_right_green);
            chatItemView.myImgContent.setBackgroundResource(R.drawable.voice_left_green);
            chatItemView.friendImgContent.setVisibility(View.VISIBLE);
            chatItemView.myImgContent.setVisibility(View.VISIBLE);
            chatItemView.myImgContent.setOnClickListener(new AudioPlayListener(chatInfo, mContext));
            chatItemView.friendImgContent.setOnClickListener(new AudioPlayListener(chatInfo, mContext));
            chatItemView.friendVoiceDur.setText(chatInfo.getDuration() + "''");
            chatItemView.myVoiceDur.setText(chatInfo.getDuration() + "''");
//                    chatItemView.friendTextContent.setTextColor(Color.rgb(7, 160, 7));
//                    chatItemView.myTextContent.setTextColor(Color.rgb(7, 160, 7));
        } else {
            chatItemView.myTextContent.setTextColor(Color.GRAY);
            chatItemView.friendTextContent.setTextColor(Color.GRAY);
        }

        chatItemView.friendTextContent.setVisibility(View.VISIBLE);
        chatItemView.myTextContent.setVisibility(View.VISIBLE);

        final SpannableStringBuilder sb2 = sb;

        chatItemView.friendTextContent.setOnLongClickListener(getLongClickListener(chatInfo, sb.toString(), false, chatItemView.friendContainer, 3));
        chatItemView.myTextContent.setOnLongClickListener(getLongClickListener(chatInfo, sb.toString(), true, chatItemView.myContainer, 3));


    }

    private void showVideoView(final ChatItemView chatItemView, final ChatInfo chatInfo, final int width) {
        Bitmap thumbnail = null;
        int height = DisplayUtil.getScreenHeight() / 3;
        chatItemView.friendImgContent.setBackgroundResource(R.drawable.send_msg_hint);
        chatItemView.myImgContent.setBackgroundResource(R.drawable.send_msg_hint);
        AnimationDrawable friendAnimationDrawable = (AnimationDrawable) chatItemView.friendImgContent.getBackground();
        AnimationDrawable myAnimationDrawable = (AnimationDrawable) chatItemView.myImgContent.getBackground();

        friendAnimationDrawable.start();
        myAnimationDrawable.start();
        if (chatInfo.getContent() != null && chatInfo.getContent() instanceof Bitmap) {
            thumbnail = (Bitmap) chatInfo.getContent();
            chatItemView.friendImgContent.setImageBitmap(thumbnail);
            chatItemView.myImgContent.setImageBitmap(thumbnail);

            height = thumbnail.getHeight() * width / thumbnail.getWidth();
            chatItemView.friendImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            chatItemView.myImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        } else {
            String thumbnailPath = FileConstant.getPathVideo() + "/" + chatInfo.getThumbnailName();
            Log.d(TAG, "downloadFile: thumbnailPath:" + thumbnailPath);
            downloadPicAndShow(chatInfo, false,
                    chatItemView, width);
//            if(new File(thumbnailPath).exists())
//                chatInfo.setContent(BitmapUtils.readBitmapFromFile(thumbnailPath));
            chatItemView.friendImgContent.setBackgroundResource(R.drawable.video_thumbnail);
            chatItemView.myImgContent.setBackgroundResource(R.drawable.video_thumbnail);
        }

        String videoPath = FileConstant.getPathVideo() + "/" + chatInfo.getFileName();
        VideoPlayListener friendVideoPlayer = new VideoPlayListener(chatInfo, chatItemView.friendVideoFL, chatItemView.friendVideoContent, videoPath,
                chatItemView.friendImgContent, chatItemView.friendVideoPlayPic, width, height, mContext);
        chatItemView.friendImgContent.setOnClickListener(friendVideoPlayer);
        chatItemView.friendImgContent.setOnLongClickListener(getLongClickListener(chatInfo, "0", true, chatItemView.friendContainer, 1));

        VideoPlayListener toVideoPlayer = new VideoPlayListener(chatInfo, chatItemView.myVideoFL, chatItemView.myVideoContent, videoPath,
                chatItemView.myImgContent, chatItemView.myVideoPlayPic, width, height, mContext);
        chatItemView.myImgContent.setOnClickListener(toVideoPlayer);
        chatItemView.myImgContent.setOnLongClickListener(getLongClickListener(chatInfo, "0", true, chatItemView.myContainer, 1));

        chatItemView.friendImgContent.setVisibility(View.VISIBLE);
        chatItemView.myImgContent.setVisibility(View.VISIBLE);

        chatItemView.friendVideoPlayPic.setVisibility(View.VISIBLE);
        chatItemView.myVideoPlayPic.setVisibility(View.VISIBLE);
    }

    private void showImageView(final ChatItemView chatItemView, final ChatInfo chatInfo, final int width) {
        // 收到消息 from显示
        Object imgContent = chatInfo.getContent();
        final String filePath = FileConstant.getPathImg() + "/" + chatInfo.getFileName();
        chatItemView.friendImgContent.setBackgroundResource(R.drawable.send_msg_hint);
        chatItemView.myImgContent.setBackgroundResource(R.drawable.send_msg_hint);
        AnimationDrawable friendAnimationDrawable = (AnimationDrawable) chatItemView.friendImgContent.getBackground();
        AnimationDrawable myAnimationDrawable = (AnimationDrawable) chatItemView.myImgContent.getBackground();

        friendAnimationDrawable.start();
        myAnimationDrawable.start();

        if (imgContent == null) {
            downloadPicAndShow(chatInfo, false, chatItemView, width);
        } else if (imgContent instanceof Bitmap) {
            int height = ((Bitmap) imgContent).getHeight() * width / ((Bitmap) imgContent).getWidth();
            Bitmap bitmap = Bitmap.createBitmap((Bitmap) imgContent);

            chatItemView.friendImgContent.setImageBitmap(bitmap);
            chatItemView.myImgContent.setImageBitmap(bitmap);

            chatItemView.friendImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            chatItemView.myImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        }


        OnClickListener onClickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImageShowActivity.class);
                intent.putExtra(ImageShowActivity.IMG_PATH, filePath);
                mContext.startActivity(intent);
            }
        };

        chatItemView.friendImgContent.setOnClickListener(onClickListener);
        chatItemView.myImgContent.setOnClickListener(onClickListener);

        chatItemView.friendImgContent.setVisibility(View.VISIBLE);
        chatItemView.myImgContent.setVisibility(View.VISIBLE);

        chatItemView.friendTextContent.setOnLongClickListener(getLongClickListener(chatInfo, filePath, false, chatItemView.friendContainer, 2));
        chatItemView.myTextContent.setOnLongClickListener(getLongClickListener(chatInfo, filePath, true, chatItemView.myContainer, 2));

    }

    private void showAudioView(final ChatItemView chatItemView, final ChatInfo chatInfo) {
        chatItemView.friendImgContent.setOnClickListener(new AudioPlayListener(chatInfo, mContext));
        chatItemView.friendImgContent.setOnLongClickListener(getLongClickListener(chatInfo, "0", true, chatItemView.friendContainer, 1));

        chatItemView.myImgContent.setOnClickListener(new AudioPlayListener(chatInfo, mContext));
        chatItemView.myImgContent.setOnLongClickListener(getLongClickListener(chatInfo, "0", true, chatItemView.myContainer, 1));

        int duration = (int) Math.rint(((double) chatInfo.getDuration()) / 1000);
        if (duration != 0) {
            chatItemView.friendVoiceDur.setText(duration + "''");
            chatItemView.myVoiceDur.setText(duration + "''");
        }


        final String originalText = (String) chatInfo.getContent();
        if (!TextUtils.isEmpty(originalText)) {
            //文字转语音
            chatItemView.friendImgContent.setBackgroundResource(R.drawable.voice_right_green);
            chatItemView.myImgContent.setBackgroundResource(R.drawable.voice_left_green);
            chatItemView.friendTextContent.setText(originalText);
            chatItemView.myTextContent.setText(originalText);
            chatItemView.friendTextContent.setVisibility(View.VISIBLE);
            chatItemView.myTextContent.setVisibility(View.VISIBLE);
        } else {
            chatItemView.friendImgContent.setBackgroundResource(R.drawable.voice_right);
            chatItemView.myImgContent.setBackgroundResource(R.drawable.voice_left);
        }

        chatItemView.friendImgContent.setVisibility(View.VISIBLE);
        chatItemView.myImgContent.setVisibility(View.VISIBLE);
        chatItemView.myVoiceDur.setVisibility(View.VISIBLE);
        chatItemView.friendVoiceDur.setVisibility(View.VISIBLE);

    }


    private void downloadPicAndShow(final ChatInfo chatInfo, boolean isVideo, final ChatItemView chatItemView, final int width) {
        CommonMsgContent msg = chatInfo.getMsg();
        IMClient.getInstance().downloadFile(msg, isVideo, new ResultCallback() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onError(int errorCode) {
                Logging.e(TAG, "load picture fail:" + errorCode);
                Resources r = mContext.getResources();
                InputStream is = r.openRawResource(R.drawable.loading_picture_fail);
                BitmapDrawable bmpDraw = new BitmapDrawable(is);
                Bitmap bitmap = bmpDraw.getBitmap();
                int height = bitmap.getHeight() * width / bitmap.getWidth();
                chatItemView.friendImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                chatItemView.myImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                chatItemView.friendImgContent.setImageBitmap(bitmap);
                chatItemView.myImgContent.setImageBitmap(bitmap);
            }

            @Override
            public void onSuccess(Object datas) {
                DownloadInfo info = new DownloadInfo(datas);
                Bitmap bitmap = null;
                Log.d(TAG, "downloadFile:onSuccess: " + info.getFilePath());
                bitmap = BitmapUtils.readBitmapFromFile(info.getFilePath());
                if (bitmap == null)
                    return;
                int height = bitmap.getHeight() * width / bitmap.getWidth();
                chatItemView.friendImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                chatItemView.myImgContent.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                chatItemView.friendImgContent.setImageBitmap(bitmap);
                chatItemView.myImgContent.setImageBitmap(bitmap);

                chatInfo.setContent(bitmap);

                OtherSideReadedNotifyMsg o = new OtherSideReadedNotifyMsg();
                RxBus.get().post(Constants.Event.NEW_MESSAGE_IN,o);
            }
        });
    }

    /**
     * 屏蔽listitem的所有事件
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

  /*  *//**
     * 初始化弹出的pop
     *//*
    private void initPopWindow() {
        View popView = mInflater.inflate(R.layout.chat_item_copy_delete_menu, null);
        mCopyTv = (TextView) popView.findViewById(R.id.chat_copy_menu);
        mDeleteTv = (TextView) popView.findViewById(R.id.chat_delete_menu);
        mPopupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // 设置popwindow出现和消失动画
        // popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
    }


    *//**
     * 显示popWindow
     *//*
    public void showPop(View parent, int x, int y, final View view, final int position, final int fromOrTo) {
        // 设置popwindow显示位置
        mPopupWindow.showAtLocation(parent, 0, x, y);
        // 获取popwindow焦点
        mPopupWindow.setFocusable(true);
        // 设置popwindow如果点击外面区域，便关闭。
        mPopupWindow.setOutsideTouchable(true);
        // 为按钮绑定事件
        // 复制
        mCopyTv.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }

            }
        });
        // 删除
        mDeleteTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                if (fromOrTo == 0) {
                    // from
                    leftRemoveAnimation(view, position);
                } else if (fromOrTo == 1) {
                    // to
                    rightRemoveAnimation(view, position);
                }

                // list.removeUsers(position);
                // notifyDataSetChanged();
            }
        });
        mPopupWindow.update();
        if (mPopupWindow.isShowing()) {

        }
    }

    *//**
     * 每个ITEM中more按钮对应的点击动作
     *//*
    public class popAction implements OnClickListener {
        int position;
        View view;
        int fromOrTo;

        public popAction(View view, int position, int fromOrTo) {
            this.position = position;
            this.view = view;
            this.fromOrTo = fromOrTo;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int[] arrayOfInt = new int[2];
            // 获取点击按钮的坐标
            v.getLocationOnScreen(arrayOfInt);
            int x = arrayOfInt[0];
            int y = arrayOfInt[1];
            // System.out.println("x: " + x + " y:" + y + " w: " +
            // v.getMeasuredWidth() + " h: " + v.getMeasuredHeight() );
            showPop(v, x, y, view, position, fromOrTo);
        }

    }
*/

    /**
     * item删除动画
     */
    private void rightRemoveAnimation(final View view, final int position) {
        Log.d(TAG, "rightRemoveAnimation: position:" + position);
        final Animation animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.chatto_remove_anim);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                mChatInfoList.remove(position);
                try {
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(TAG, "onBindViewHolder: wrong adapter");
                    e.printStackTrace();
                }
            }
        });

        view.startAnimation(animation);
    }

    /**
     * item删除动画
     */
    private void leftRemoveAnimation(final View view, final int position) {
        final Animation animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.chatfrom_remove_anim);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                for(int i=position;i<mChatInfoList.size()-2;i++) {
                    mChatInfoList.set(position,mChatInfoList.get(i+1));
                }
                delCount++;

            }
        });

        view.startAnimation(animation);
    }

    /**
     * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
     *
     * @param dismissView
     * @param dismissPosition
     */
    private void performDismiss(final View dismissView, final int dismissPosition) {
        final LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
        final int originalHeight = dismissView.getHeight();// item的高度

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(mAnimationTime);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mChatInfoList.remove(dismissPosition);

                try {
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(TAG, "onBindViewHolder: wrong adapter");
                    e.printStackTrace();
                }
                // 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
                // 所以我们在动画执行完毕之后将item设置回来
                ViewHelper.setAlpha(dismissView, 1f);
                ViewHelper.setTranslationX(dismissView, 0);
                LayoutParams lp = dismissView.getLayoutParams();
                lp.height = originalHeight;
                dismissView.setLayoutParams(lp);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });

    }


    public OnLongClickListener getLongClickListener(final ChatInfo chatInfo, final String share, final boolean isRight, final View view, int count) {
        if (count == 1) {
            return new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ArrayList text = new ArrayList();
                    text.add("删除");

                    ArrayList onclickListener = new ArrayList();

                    onclickListener.add(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IMClient.getInstance().delMsgById(chatInfo.getMsgID());
                            if (isRight) {
                                rightRemoveAnimation(view, mChatInfoList.indexOf(chatInfo));
                            } else {
                                leftRemoveAnimation(view, mChatInfoList.indexOf(chatInfo));
                            }
                        }
                    });

                    new PopWindowUtil(mContext).popWindow(text, onclickListener, mConvertView);
                    return false;
                }
            };
        }


        if (count == 2) {
            return new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ArrayList text = new ArrayList();
                    text.add("分享至...");
                    text.add("删除");

                    ArrayList onclickListener = new ArrayList();

                    onclickListener.add(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShareUtil.shareImage(mContext, share);
                        }
                    });
                    onclickListener.add(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IMClient.getInstance().delMsgById(chatInfo.getMsgID());
                            if (isRight) {
                                rightRemoveAnimation(view, mChatInfoList.indexOf(chatInfo));
                            } else {
                                leftRemoveAnimation(view, mChatInfoList.indexOf(chatInfo));
                            }
                        }
                    });

                    new PopWindowUtil(mContext).popWindow(text, onclickListener, mConvertView);
                    return false;
                }
            };
        }
        if (count == 3) {
            return new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ArrayList text = new ArrayList();
                    text.add("分享至...");
                    text.add("删除");
                    text.add("复制");
                    ArrayList onclickListener = new ArrayList();

                    onclickListener.add(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShareUtil.shareImage(mContext, share);
                        }
                    });
                    onclickListener.add(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IMClient.getInstance().delMsgById(chatInfo.getMsgID());
                            if (isRight) {
                                rightRemoveAnimation(view, mChatInfoList.indexOf(chatInfo));
                            } else {
                                leftRemoveAnimation(view, mChatInfoList.indexOf(chatInfo));
                            }
                        }
                    });
                    onclickListener.add(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 获取剪贴板管理服务
                            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            // 将文本数据复制到剪贴板
                            cm.setText((CharSequence) chatInfo.getContent());
                            ToastUtil.showText("复制成功");
                        }
                    });

                    new PopWindowUtil(mContext).popWindow(text, onclickListener, mConvertView);
                    return false;
                }
            };
        }

        return null;
    }

}
