package com.iflytek.im.demo.common.audioUtil;

import java.io.File;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class MediaManager {
	
	private static MediaPlayer mMediaPlayer;   
	private static boolean isPause;
	
	public static boolean playSound(String filePath,MediaPlayer.OnCompletionListener onCompletionListenter){
		if(mMediaPlayer==null){
			mMediaPlayer=new MediaPlayer();
			mMediaPlayer.setOnErrorListener( new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mMediaPlayer.reset();
					AudioAnim.getInstance().stop(2);
					return false;
				}
			});
		}else{
			mMediaPlayer.reset();
		}
		
		
		try {
			File file = new File(filePath);
			if(file.exists()){
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setOnCompletionListener(onCompletionListenter);
				mMediaPlayer.setDataSource(filePath);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				return true;
			}else{
				return false;
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			release();
			return false;
		} catch (SecurityException e) {
			e.printStackTrace();
			release();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			release();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			release();
			return false;
		}
	}
	
	public static void pause(){
		if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
			mMediaPlayer.pause();
			isPause=true;
		}
	}
	
	public static void resume(){
		if(mMediaPlayer!=null && isPause){
			mMediaPlayer.start();
			isPause=false;
		}
	}
	
	public static void release(){
		if(mMediaPlayer!=null){
			mMediaPlayer.release();
			mMediaPlayer=null;
		}
	}

}
