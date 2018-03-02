package com.iflytek.im.demo.ui.view;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.audioUtil.AudioDialogManager;
import com.iflytek.im.demo.common.audioUtil.AudioManager;
import com.iflytek.im.demo.common.audioUtil.AudioManager.AudioStateListener;


public class AudioRecorderButton extends Button implements AudioStateListener ,View.OnLongClickListener{


	private static final String TAG = "AudioRecorderButton";
	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_TO_CANCEL = 3;
	private int mCurState = STATE_NORMAL;
	private boolean isRecording = false;
	private boolean isNeedAudio2Txt = false;
	private boolean mReady;


	private static final int DISTANCE_Y_CANCEL = 50;

	private AudioDialogManager audioDialogManager;

	private AudioManager mAudioManager;
	

	public boolean isNeedAudio2Txt() {
		return isNeedAudio2Txt;
	}

	public void setNeedAudio2Txt(boolean isNeedAudio2Txt) {
		this.isNeedAudio2Txt = isNeedAudio2Txt;
	}

	@Override
	public boolean onLongClick(View view) {
		mReady = true;
		mAudioManager.prepareAudio();
		return true;
	}

	public interface AudioFinishRecorderListener {
		void onFinish(int duration, String audioFilePath,String audioFileName, boolean isNeedAudio2Txt);
	}
	
	private AudioFinishRecorderListener mListener;
	
	public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
		this.mListener =listener;
	}
	
	public AudioRecorderButton(Context context) {
		super(context, null);
	}
	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		audioDialogManager = new AudioDialogManager(getContext());

		mAudioManager = AudioManager.getInstance();
		mAudioManager.setOnAudioStateListener(this);
		setOnLongClickListener(this);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();   
		
		int x = (int) event.getX();       
		int y = (int) event.getY();

		switch (action) {
		
		case MotionEvent.ACTION_DOWN:
			break;

		case MotionEvent.ACTION_MOVE:

			if (isRecording) {
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			if (!mReady) {   
				reset();
				return super.onTouchEvent(event);
			}

			if (!isRecording || mTime < 700) {
				audioDialogManager.tooShort();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
			}else if (mCurState == STATE_RECORDING) {
				audioDialogManager.dismissDialog();
				mAudioManager.release();
				if(mListener !=null){
					mListener.onFinish(mTime, mAudioManager.getCurrentFilePath(), mAudioManager.getCurrentFileName(),isNeedAudio2Txt);
				}				
				
			} else if (mCurState == STATE_WANT_TO_CANCEL) {
				audioDialogManager.dismissDialog();
				mAudioManager.cancel();
			}
			Log.d("audioRecordButton", "onTouchEvent: 停止");
			reset();
			break;
		}
		return super.onTouchEvent(event);
	}

	private void reset() {
        Log.d(TAG, "reset: isRecording = false");
        isRecording = false;
		mReady = false;                
		mTime = 0;
		Log.d(TAG, "reset: ");
		changeState(STATE_NORMAL);
	}

	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
			return true;
		}
		return false;
	}

	private void changeState(int state) {
		if (mCurState != state) {
			mCurState = state;
			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.btn_recorder_normal);
				if(isNeedAudio2Txt){
					setText("语音转写文字发送");
				}else {
					setText(R.string.str_recorder_normal);
				}
				break;

			case STATE_RECORDING:
				setBackgroundResource(R.drawable.btn_recorder_recordering);
				setText(R.string.str_recorder_recording);
				if (isRecording) {
					audioDialogManager.recording();
				}
				break;

			case STATE_WANT_TO_CANCEL:
				setBackgroundResource(R.drawable.btn_recorder_recordering);
				setText(R.string.str_recorder_want_cancel);
				audioDialogManager.wantToCancel();
				break;
			}
		}
	}

	@Override
	public void wellPrepared() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
	}

	@Override
	public void prepareError() {
	}

	@Override
	public void recordFinish() {
		mHandler.sendEmptyMessage(MSG_DIALOG_DIMISS);
	}

	private static final int MSG_AUDIO_PREPARED = 0x110;   
	private static final int MSG_VOICE_CHANGE = 0x111;     
	private static final int MSG_DIALOG_DIMISS = 0x112;    
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:        
				audioDialogManager.showRecordingDialog();
				isRecording = true;
                new Thread(mGetVoiceLevelRunnable).start();
				mCurState = STATE_RECORDING;
				break;

			case MSG_VOICE_CHANGE:          
				audioDialogManager.updateVoiceLevel(mAudioManager
						.getVoiceLevel(7));
				break;

			case MSG_DIALOG_DIMISS:        
				audioDialogManager.dismissDialog();
				break;
			}
		};
	};
	
	private int mTime;  
	private Runnable mGetVoiceLevelRunnable = new Runnable() {

		@Override
		public void run() {
			
			while (isRecording) {
                Log.d(TAG, "run: isRecording");
                try {
					Thread.sleep(100);
					mTime += 100;
                    Log.d(TAG, "run: time:" + mTime);
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	};
}
