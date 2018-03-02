package com.iflytek.im.demo.listener;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.msg.OtherSideReadedNotifyMsg;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.download.DownloadObserverInfo;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.bean.ChatInfo;
import com.iflytek.im.demo.common.ToastUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/9/18.
 */
public class VideoPlayListener  implements View.OnClickListener{
    private final String TAG = "VideoPlayListener";
    private VideoView videoView;
    private String videoPath;
    private ImageView imgContent, playPic;
//    private LinearLayout txtImgContainer;
    private FrameLayout videoViewFL;
    private int width;
    private int height;
    private ChatInfo chatInfo;
    private Context context;
    MediaController mediaController;

    public VideoPlayListener(ChatInfo chatInfo, FrameLayout videoViewFL, VideoView toVideoView, String videoPath, ImageView imgContent, ImageView playPic, int width, int height, Context context) {
        // TODO Auto-generated constructor stub
        this.videoView = toVideoView;
        this.videoPath = videoPath;
//        this.txtImgContainer = txtImgContainer;
        this.videoViewFL = videoViewFL;
        this.height = height;
        this.width = width;
        this.chatInfo = chatInfo;
        this.context = context;
        this.imgContent = imgContent;
        this.playPic = playPic;
        mediaController = new MediaController(context);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (!(new File(videoPath)).exists()) {
            final String fileFid = chatInfo.getFileFid();


            IMClient.getInstance().downloadFile(chatInfo.getMsg(), true, new ResultCallback() {
                @Override
                public void onError(int errorCode) {
                    Log.d(TAG, "Download video failed, fid = " + fileFid);
                }

                @Override
                public void onSuccess(Object datas) {
                    DownloadObserverInfo info = (DownloadObserverInfo) datas;
                    try {
                        playVideo();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showText("播放失败");
                    }
                }
            });
        } else {
            try {
                playVideo();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showText("播放失败");
            }
        }

    }


    private void playVideo() {
        videoView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        imgContent.setVisibility(View.GONE);
        playPic.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoViewFL.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.start();
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "播放结束");
                mediaController.removeAllViews();
                imgContent.setVisibility(View.VISIBLE);
                playPic.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                videoViewFL.setVisibility(View.GONE);
                OtherSideReadedNotifyMsg o = new OtherSideReadedNotifyMsg();
                RxBus.get().post(Constants.Event.NEW_MESSAGE_IN,o);
                mp.release();

            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                ToastUtil.showText("播放失败");
                mediaController.removeAllViews();
                imgContent.setVisibility(View.VISIBLE);
                playPic.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                videoViewFL.setVisibility(View.GONE);
                OtherSideReadedNotifyMsg o = new OtherSideReadedNotifyMsg();
                RxBus.get().post(Constants.Event.NEW_MESSAGE_IN,o);
                mediaPlayer.release();
                return true;
            }
        });
    }
}

