package com.iflytek.im.demo.ui.activity;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.common.videoUtil.VideoManager;
import com.iflytek.im.demo.common.videoUtil.VideoManager.VideoFinishRecordingListener;
import com.iflytek.im.demo.ui.view.VideoRecorderButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SmallVideoRecorderActivity extends BaseActivity implements View.OnLongClickListener {
    public static final String TAG = "SmallVideoRecorder";

    private static final String TIME_UNIT = "s";
    private static final int RECORD_TIME_LIMIT = 10;

    private VideoRecorderButton mStartToRecordBtn;
    private SurfaceView mSurfaceView;
    private TextView mRecordingTimeTV;
    private SurfaceHolder mSurfaceHolder;
    private VideoManager mVideoManagerInstance;
    private String mReceiverName;

    @Override
    public boolean onLongClick(View view) {
        mVideoManagerInstance.startToRecord();
        return true;
    }

    public interface RecordingTimeListener {
        void changeRecordingTimeTV(int recordingTime);
    }

    class RecordingTimeListenerImp implements RecordingTimeListener {

        @Override
        public void changeRecordingTimeTV(int recordingTime) {
            int recordingTimeTv = RECORD_TIME_LIMIT;
            if (recordingTime >= 0 && recordingTime < 500) {
                recordingTime = RECORD_TIME_LIMIT;
            } else {
                recordingTimeTv = RECORD_TIME_LIMIT - (int) Math.rint(((double) recordingTime) / 1000);
            }

            mRecordingTimeTV.setText(recordingTimeTv + TIME_UNIT);
        }

    }

    private RecordingTimeListenerImp mRecordingTimeListener = new RecordingTimeListenerImp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_vedio_recorder);
        mSurfaceView = (SurfaceView) findViewById(R.id.sView);
        mStartToRecordBtn = (VideoRecorderButton) findViewById(R.id.start_to_record);
        mRecordingTimeTV = (TextView) findViewById(R.id.video_time);
//        mStartToRecordBtn.setOnClickListener(this);
        mStartToRecordBtn.setOnLongClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mVideoManagerInstance = VideoManager.getInstance();
        mVideoManagerInstance.setRecordingTimeListener(mRecordingTimeListener);
        mVideoManagerInstance.setSurfaceHolder(mSurfaceHolder);
        mReceiverName = getReceiverName();


        mStartToRecordBtn.setVideoFinishRecordingListenter(new VideoFinishRecordingListener() {
            @Override
            public void onFinish(int duration, String videoPath, String videoName, String videoWidth, String videoHeight) {
                mRecordingTimeTV.setText(RECORD_TIME_LIMIT + TIME_UNIT);
                mRecordingTimeTV.setVisibility(View.GONE);
                Intent intent = new Intent();
                intent.putExtra(Constants.Parameter.KEY_VIDEO_DURATION, duration);
                intent.putExtra(Constants.Parameter.KEY_VIDEO_HEIGHT, videoHeight);
                intent.putExtra(Constants.Parameter.KEY_VIDEO_WIDTH, videoWidth);
                intent.putExtra(Constants.Parameter.KEY_VIDEO_PATH, videoPath);
                intent.putExtra(Constants.Parameter.KEY_VIDEO_NAME, videoName);
                SmallVideoRecorderActivity.this.setResult(Constants.RequestCodeAndResultCode.SMALL_VIDEO_RESULTCODE, intent);
                SmallVideoRecorderActivity.this.finish();
            }
        });
    }


    public String getReceiverName() {
        Intent intent = getIntent();
        return intent.getStringExtra(Constants.Parameter.KEY_RECEIVER_ID);
    }


//    @Override
//    public void onClick(View v) {
//
//    }

    @Override
    protected void onDestroy() {
        mSurfaceHolder = null;
        mSurfaceView = null;
        super.onDestroy();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.small_vedio_recorder;
    }


}
