package com.iflytek.im.demo.common.audioUtil;

import java.io.File;
import java.util.UUID;

import android.media.MediaRecorder;
import android.util.Log;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.cloud.im.core.util.FileUtil;
import com.iflytek.cloud.im.core.util.LogDebug;
import com.iflytek.cloud.im.record.PcmRecorder.PcmRecordListener;

public class AudioManager {
	private static final String TAG = "Audiomanager";

//	private MediaRecorder mMediaRecorder;
	private String mDir;             
	private String mCurrentFilePath;
	private String mCurrentFileName;

	private static AudioManager mInstance;

	private boolean isPrepared;
	private double currentVolume;

	private AudioManager(String dir) {
		mDir = dir;
	}

	public interface AudioStateListener {
		void wellPrepared();
		void prepareError();
		void recordFinish();
	}

	public AudioStateListener mListener;

	public void setOnAudioStateListener(AudioStateListener audioStateListener) {
		mListener = audioStateListener;
	}
	
	public static AudioManager getInstance() {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null) {
					mInstance = new AudioManager(FileConstant.getPathAudio());
				}
			}
		}

		return mInstance;
	}

	public void prepareAudio() {

		try {
			isPrepared = false;

			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

//            String fileName = GenerateFileName();
            String fileName = GenerateFileName(".pcm");
			File file = new File(dir, fileName);

			mCurrentFilePath = file.getAbsolutePath();
			mCurrentFileName = fileName;
            Log.d(TAG, "prepareAudio: path" + mCurrentFilePath);

            //正常录音
//			mMediaRecorder = new MediaRecorder();

//            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//			mMediaRecorder.setOutputFile(file.getAbsolutePath());
//            mMediaRecorder.prepare();
//            mMediaRecorder.start();
//			isPrepared = true;
//			mListener.wellPrepared();


			IMClient.getInstance().setAudioPath(mDir);
			IMClient.getInstance().startRecording(mCurrentFileName, new PcmRecordListener() {
				@Override
				public void onRecordBuffer(int length, double volume) {
					Log.d(TAG, "onRecordBuffer: get RecordBuffer");
					currentVolume = volume;
				}

				@Override
				public void onError(int error) {
					Log.w(TAG,"recoder error: "+error);
					if(mListener != null)
						mListener.prepareError();
				}

				@Override
				public void onRecordStarted(boolean success) {
					Log.d(TAG, "onRecordStarted: onRecrodStarted");
					isPrepared = true;
					if (mListener != null) {
						mListener.wellPrepared();
					}
				}

				@Override
				public void onRecordFinished(String filePath) {
					Log.d(TAG, "onRecordFinished: ");
					Log.i(TAG,"file is :"+filePath);
                    mCurrentFilePath = filePath;
                    int index = filePath.lastIndexOf("/");
                    mCurrentFileName = filePath.substring(index + 1);
                    mListener.recordFinish();
				}
			});


//			Log.i("audiomanage:filePath", file.getAbsolutePath());


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String GenerateFileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	private String GenerateFileName(String fmt) {
		return UUID.randomUUID().toString() + fmt;
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepared) {
			try {
				Log.d("adsfsda", "getVoiceLevel: "+ currentVolume);
				return maxLevel * (int)currentVolume / 110 + 1;
			} catch (Exception e) {
			}
		}
		return 1;
	}

	public void release() {
		IMClient.getInstance().stopRecord();
//        mMediaRecorder.stop();
//        mMediaRecorder = null;
	
	}

	public void cancel() {

		release();

		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			file.delete();    
			mCurrentFilePath = null;
		}
	}

	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}
	
	public String getCurrentFileName(){
		return mCurrentFileName;
	}
}
