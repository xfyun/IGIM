package com.iflytek.im.demo.ui.view;


import com.iflytek.im.demo.common.videoUtil.VideoManager;
import com.iflytek.im.demo.common.videoUtil.VideoManager.VideoFinishRecordingListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;


public class VideoRecorderButton extends Button {
	
	private VideoManager mVideoManagerInstance;
	
	
	private static final int DISTANCE_Y_CANCEL = 50;

	public VideoRecorderButton(Context context) {
		super(context);
	}

	
	public VideoRecorderButton(Context context ,AttributeSet attr) {
		super(context, attr);
		
		mVideoManagerInstance = VideoManager.getInstance();
	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {


        int x = (int) event.getX();
        int y = (int) event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (isWantToCancel(x, y) || mVideoManagerInstance.getRecordingTime() < 500) {
                    Toast.makeText(getContext(), "取消视频或者视频时间太短", Toast.LENGTH_SHORT).show();
                    mVideoManagerInstance.cancel();
                } else {
                    mVideoManagerInstance.release();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
	
	
	
	public boolean isWantToCancel(int x, int y){
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
			return true;
		}
		return false;
	}


	public void setVideoFinishRecordingListenter(VideoFinishRecordingListener videoFinishRecorderListenter) {
		mVideoManagerInstance.setVideoFinishRecordingListener(videoFinishRecorderListenter);
	}
	
	
}
