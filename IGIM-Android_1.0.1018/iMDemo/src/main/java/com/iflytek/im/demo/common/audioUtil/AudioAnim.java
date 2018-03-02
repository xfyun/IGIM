package com.iflytek.im.demo.common.audioUtil;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.bean.ChatInfo;
import com.iflytek.im.demo.common.ToastUtil;

/**
 * Created by Administrator on 2016/9/20.
 */
public class AudioAnim {

    private AnimationDrawable animationDrawable;
    private static AudioAnim instance;

    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //stop
                    stopAnim();
                    ToastUtil.showText("停止播放");
                    break;
                case 1:
                    //complete
                    stopAnim();
                    ToastUtil.showText("播放完成");
                    break;
                case 2:
                    //error
                    stopAnim();
                    ToastUtil.showText("播放出错");
                    break;
                case 3:
                    //pause
                    stopAnim();
                    ToastUtil.showText("暂停播放");
                    break;
            }
        }
    };

    public synchronized static AudioAnim getInstance() {
        if (instance == null) {
            instance = new AudioAnim();
        }
        return instance;
    }


    public void start(ChatInfo chatInfo, ImageView audio){


        String originalText = (String) chatInfo.getContent();
        if(chatInfo.getIsSend() == 0){
            if(!TextUtils.isEmpty(originalText)){
                audio.setBackgroundResource(R.drawable.audio_play_anim_left_green);
            }else{
                audio.setBackgroundResource(R.drawable.audio_play_anim_left);
            }
        }else{
            if(!TextUtils.isEmpty(originalText)){
                audio.setBackgroundResource(R.drawable.audio_play_anim_right_green);
            }else{
                audio.setBackgroundResource(R.drawable.audio_play_anim_right);
            }
        }
        stopAnim();
        animationDrawable = (AnimationDrawable)audio.getBackground();
        animationDrawable.start();
    }



    public void stop(int what){
        mainHandler.sendEmptyMessage(what);
    }
    public void stopAnim() {
        if(animationDrawable != null){
            Log.e("AudioAnim", "stopAnim: ");
            animationDrawable.stop();
            animationDrawable.selectDrawable(2);
            animationDrawable = null;
        }
    }
}
