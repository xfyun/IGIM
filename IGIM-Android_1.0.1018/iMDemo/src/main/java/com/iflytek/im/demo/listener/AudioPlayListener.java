package com.iflytek.im.demo.listener;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.cloud.im.entity.DownloadInfo;
import com.iflytek.cloud.im.entity.msg.OtherSideReadedNotifyMsg;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.cloud.im.record.AudioPlayer;
import com.iflytek.download.DownloadObserverInfo;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.bean.ChatInfo;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.common.audioUtil.AudioAnim;

import java.io.File;

/**
 * Created by Administrator on 2016/9/13.
 */
public class AudioPlayListener implements View.OnClickListener {

    private String TAG = "AudioPlayListener";
    private Context mContext;
    private ChatInfo chatInfo;
    public AudioPlayListener(ChatInfo chatInfo, Context context) {
        this.mContext = context;
        this.chatInfo = chatInfo;
    }


    @Override
    public void onClick(final View v) {
        String fileName = chatInfo.getFileName();
        Log.d(TAG,"fileName is "+fileName);

        if (fileName == null) {
            ToastUtil.showText("这就是纯文字");
            return;
        }
        final String originalText = (String) chatInfo.getContent();
        final String wavPath = FileConstant.getPathAudio() + "/" + fileName;

        final String fid = chatInfo.getFileFid();

        /*if(originalText != null) {
            if (!new File(wavPath).exists()) {
                if (chatInfo.getPostType() == 1) {
                    //TODO 合成语音的下载

                    IMClient.getInstance().downloadFile(chatInfo.getMsg(), false, new ResultCallback() {
                        @Override
                        public void onError(int errorCode) {
                            Log.e(TAG, "Download text2Audio failed");
                        }

                        @Override
                        public void onSuccess(Object datas) {
                            DownloadObserverInfo info = (DownloadObserverInfo) datas;
                            Log.d(TAG, "Download text2Audio success, file path = " + info.getFilePath());
                            //convertWaveFile(info.getFilePath(),wavPath);
                            //resultCallback.onSuccess(datas);
                            currentFilePath = info.getFilePath();
                        }
                    });

                }
            }
        }*/
        Log.d(TAG, "onClick: wavPath:" + wavPath);
        playSound(wavPath);



        final ImageView audio = (ImageView) v;
        AudioAnim.getInstance().start(chatInfo, audio);
    }


    public void playSound(String filepath){
        //boolean playOk;
       /* boolean playOk = MediaManager.playSound(filepath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ToastUtil.showText("语音播放完成");
                AudioAnim.getInstance().stop();
            }
        });*/

        File file = new File(filepath);
        if(!file.exists()){
            IMClient.getInstance().downloadFile( chatInfo.getMsg(), false, new ResultCallback() {
                @Override
                public void onError(int errorCode) {
                    Log.i("AudioPlayListner", "Load audio file failed, fid = " + chatInfo.getFileFid());
                }

                @Override
                public void onSuccess(Object obj) {
                    Log.i("AudioPlayListner", "下载完成");
                    DownloadInfo info = new DownloadInfo(obj);
                    OtherSideReadedNotifyMsg o = new OtherSideReadedNotifyMsg();
                    RxBus.get().post(Constants.Event.NEW_MESSAGE_IN,o);
                    IMClient.getInstance().initPlayer(info.getFilePath(), new AudioPlayer.PlayerListener() {
                        @Override
                        public void onPause() {
                            AudioAnim.getInstance().stop(3);
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {
                            AudioAnim.getInstance().stop(0);
                        }

                        @Override
                        public void onCompleted() {
                            AudioAnim.getInstance().stop(1);
                        }

                        @Override
                        public void onError(int errorCode) {
                            AudioAnim.getInstance().stop(2);
                        }
                    });
                    IMClient.getInstance().startPlay();
                }
            });
        } else {
            IMClient.getInstance().initPlayer(filepath, new AudioPlayer.PlayerListener() {
                @Override
                public void onPause() {
                    AudioAnim.getInstance().stop(3);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {
                    AudioAnim.getInstance().stop(0);
                }

                @Override
                public void onCompleted() {
                    AudioAnim.getInstance().stop(1);
                }

                @Override
                public void onError(int errorCode) {
                    AudioAnim.getInstance().stop(2);
                }
            });
            IMClient.getInstance().startPlay();
        }


        //if(!playOk){

        //}


    }


}
