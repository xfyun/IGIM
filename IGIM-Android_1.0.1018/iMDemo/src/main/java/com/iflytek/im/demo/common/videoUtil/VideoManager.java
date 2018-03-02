package com.iflytek.im.demo.common.videoUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ui.activity.SmallVideoRecorderActivity.RecordingTimeListener;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class VideoManager implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener,Callback {

	private MediaRecorder mMediaRecorder;
	
	private String mCurrentVideoPath;
	private String mCurrentVideoName;
	private int mRecordingTime;
	private boolean isRecording = false;
	private RecordingTimeListener mRecordingTimeListener;
	private static VideoManager mInstance;
	private VideoFinishRecordingListener mVideoFinishRecordingListener;
	private boolean isFinishNormal = false;
	private  Camera mCamera;
	private  CamcorderProfile mProfile;
	private  SurfaceHolder mSurfaceHolder;
	private String mVideoHeight;
	private String mVideoWidth;

	
	private String TAG = "VideoManager";

	private static final int MSG_CHANGE_RECORDING_TIME = 0x110;
	private static final int MSG_TIME_TO_STOP_RECORDING = 0x111;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_CHANGE_RECORDING_TIME:
				mRecordingTimeListener.changeRecordingTimeTV(mRecordingTime);
				break;
			case MSG_TIME_TO_STOP_RECORDING:
				release();
			}
		};
	};

	private Runnable mChangeRecordingTimeRunnable = new Runnable() {

		@Override
		public void run() {

			while (isRecording) {

				try {
					Thread.sleep(100);
					mRecordingTime += 100;
					mHandler.sendEmptyMessage(MSG_CHANGE_RECORDING_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(mRecordingTime>=10000){
					mHandler.sendEmptyMessage(MSG_TIME_TO_STOP_RECORDING);
				}

			}
		}
	};
	
	public interface VideoFinishRecordingListener {
		void onFinish(int Duration,String videoPath, String videoName, String videoWidth,String videoHeight);
	}
	public void setVideoFinishRecordingListener(VideoFinishRecordingListener videoFinishRecorderListener) {
		this.mVideoFinishRecordingListener = videoFinishRecorderListener;
	}

	
	
	private VideoManager() {
		mCamera = Camera.open();
		Log.i(TAG, "打开Camera");
		mProfile = CamcorderProfile.get(0, CamcorderProfile.QUALITY_LOW);
		
	}

	public static VideoManager getInstance() {
		
		if (mInstance == null) {
			synchronized (VideoManager.class) {
				if (mInstance == null) {
					
					mInstance = new VideoManager();
					
				}
			}
		}

		return mInstance;
	}
	
	public Camera getCamera(){
		return mCamera;
	}

	public void setRecordingTimeListener(RecordingTimeListener mRecordingTimeListener) {
		this.mRecordingTimeListener = mRecordingTimeListener;
	}
	
	
	public void startToRecord() {
		mRecordingTime = 0;
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder(); // 创建mediarecorder的对象
		}
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setProfile(mProfile);

		String videoName = GenerateFileName();
		mCurrentVideoName = videoName;
		mCurrentVideoPath = FileConstant.getPathVideo() + File.separator + mCurrentVideoName;

		File videoDir = new File(FileConstant.getPathVideo());

		if (!videoDir.exists()) {
			videoDir.mkdirs();
		}
		File videoFile = new File(videoDir, videoName);


		mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
		mMediaRecorder.setOrientationHint(90);

		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			mCamera.unlock();
			cancel();
		}

		mMediaRecorder.start();
		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setOnInfoListener(this);

		isRecording = true;
		isFinishNormal = true;
		new Thread(mChangeRecordingTimeRunnable).start();
	}

	public RecordingTimeListener getRecordingTimeListener() {
		return mRecordingTimeListener;
	}


	private String GenerateFileName() {
		return UUID.randomUUID().toString() + ".mp4";
	}

	public void release() {
		try {
			isRecording = false;
			if(mMediaRecorder != null){
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setOnInfoListener(null);
				mMediaRecorder.setPreviewDisplay(null);
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		if(isFinishNormal){
			getVideoResolution();
			mVideoFinishRecordingListener.onFinish(mRecordingTime, mCurrentVideoPath, mCurrentVideoName, mVideoWidth, mVideoHeight);
			isFinishNormal = false;
		}
	}

	private void getVideoResolution() {
		MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource(mCurrentVideoPath);
		mVideoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
		mVideoHeight = mediaMetadataRetriever.extractMetadata(mediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
	}



	public void cancel() {

		isFinishNormal = false;
		if (mCurrentVideoPath != null) {
			File file = new File(mCurrentVideoPath);
			file.delete();
			mCurrentVideoPath = null;
            mRecordingTime = 10;
            mHandler.sendEmptyMessage(MSG_CHANGE_RECORDING_TIME);
		}
        release();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

		} else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {

		}
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {

	}

	public int getRecordingTime() {
		return mRecordingTime;
	}



	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		this.mSurfaceHolder = surfaceHolder;
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}



	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		try {
			if(mCamera == null){
				mCamera = Camera.open();
			}
			mCamera.setPreviewDisplay(holder);
//			mSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            mSurfaceHolder = holder;
		} catch (Exception e) {
            if (mCamera != null) {
                mCamera.release();
            }
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		if (mMediaRecorder != null) {
			mMediaRecorder.release(); // Now the object cannot be reused
			mMediaRecorder = null;
		}
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mSurfaceHolder = null;
		}
	}

}
