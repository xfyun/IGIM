package com.iflytek.im.demo.common.videoUtil;

import com.iflytek.im.demo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class VideoDialogManager {
	private Context mContext;
	private TextView mVideoTimeTV;

	
	public VideoDialogManager(Context context) {
		this.mContext = context;
	}
	

	public void showRecorderingDialog() {

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(
				R.layout.small_vedio_recorder, null);
		mVideoTimeTV = (TextView) view.findViewById(R.id.video_time);
	}
	
	public void changeVideoTime(int time){
		mVideoTimeTV.setText(time + "s");
		mVideoTimeTV.setVisibility(View.VISIBLE);
	}
	
	public void dimissVideoTime(){
		mVideoTimeTV.setVisibility(View.GONE);
		mVideoTimeTV.setText("");
	}

	
	

}
