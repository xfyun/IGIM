package com.iflytek.im.demo.common.audioUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.im.demo.R;

public class AudioDialogManager {
	private Dialog mDialog;

	private ImageView mIcon;    
	private ImageView mVoice;   

	private TextView mLabel;

	private Context mContext;

	public AudioDialogManager(Context context) {
		this.mContext = context;
	}

	public void showRecordingDialog() {
		mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(
				R.layout.voicenotes_recorder_dialog, null);
		mDialog.setContentView(view);

		mIcon = (ImageView) mDialog.findViewById(R.id.recorder_dialog_icon);
		mVoice = (ImageView) mDialog.findViewById(R.id.recorder_dialog_voice);
		mLabel = (TextView) mDialog.findViewById(R.id.recorder_dialog_label);

		mDialog.show();
	}

	/**
	 * 正在录音时，Dialog的显示
	 */
	public void recording() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.VISIBLE);
			mLabel.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.recorder);
			mLabel.setText("手指滑动，取消录音");
		}
	}

	/**
	 * 取消录音提示对话框
	 */
	public void wantToCancel() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLabel.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.cancel);
			mLabel.setText("松开手指，取消录音");
		}
	}

	/**
	 * 录音时间过短
	 */
	public void tooShort() {
		if (mDialog != null && mDialog.isShowing()) {
			
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLabel.setVisibility(View.VISIBLE);
			
			mIcon.setImageResource(R.drawable.voice_to_short);
			mLabel.setText("录音时间过短");
		}
	}

	public void dismissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}	
	}

	/**
	 * 通过Level更新Voice的图片：V1——V7
	 * @param level
	 */
	public void updateVoiceLevel(int level) {
		if (mDialog != null && mDialog.isShowing()) {
			
			int voiceResId=mContext.getResources().getIdentifier("v"+level, "drawable", mContext.getPackageName());  //getIdentifier()��ȡӦ�ð���ָ����Դ��ID
			mVoice.setImageResource(voiceResId);
		}
	}
}
