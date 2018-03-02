package com.iflytek.im.demo.ui.fragment;


import android.animation.Animator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.audioUtil.AudioManager;

/**
 * 用于显示和处理录音的Fragment
 * Created by imxqd on 2016/9/5.
 */
public class SoundRecordFragment extends BaseFragment implements View.OnTouchListener {

    private static final int DISTANCE_Y_CANCEL = 50;

    private View mBgView;
    private TextView mText;
    private ImageView mIcon;

    private ViewPropertyAnimator animator;
    private boolean isRecording = false;

    private float size = 1;

    private RecordCallback mCallback;


    public SoundRecordFragment() {
        // Required empty public constructor
    }

    public static SoundRecordFragment newInstance() {
        SoundRecordFragment fragment = new SoundRecordFragment();
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_sound_record;
    }


    @Override
    protected void initMember() {
        super.initMember();
    }

    @Override
    protected void findViews() {
        mBgView = f(R.id.voice_bg_view);
        mText = f(R.id.voice_text);
        mIcon = f(R.id.voice_icon);
    }

    @Override
    protected void initUI() {
        animator = mBgView.animate();
    }

    @Override
    protected void setupEvents() {
        mIcon.setOnTouchListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecordCallback) {
            mCallback = (RecordCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RecordCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mBgView.setBackgroundResource(R.drawable.circular_grey_200);
            mText.setText(R.string.voice_release_to_send);
            AudioManager.getInstance().prepareAudio();
            isRecording = true;
            startAnim();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mBgView.setBackgroundResource(R.drawable.circular_outline_grey_200);
            isRecording = false;
            stopAnim();
            mText.setText(R.string.voice_press_to_record);
            if (isWantToCancel(x, y)) {
                AudioManager.getInstance().cancel();
                mCallback.onRecordingCancel();
            } else {
                mCallback.onRecordingSend(AudioManager.getInstance().getCurrentFilePath());
                AudioManager.getInstance().release();
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isRecording = true;
            if (isWantToCancel(x, y)) {
                mText.setText(R.string.voice_release_to_cancel);
            } else {
                mText.setText(R.string.voice_release_to_send);
            }
        }
        return false;
    }

    public boolean isWantToCancel(int x, int y){
        if (x < 0 || x > mIcon.getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > mIcon.getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void startAnim() {
        if (!isRecording) {
            return;
        }
        size = AudioManager.getInstance().getVoiceLevel(100) / 33f + 1;
        size = size > 3 ? 3 : size;
        animator.scaleX(size)
                .scaleY(size);
        if (size > mBgView.getScaleX()) {
            animator.setDuration(100);
        } else {
            animator.setDuration(200);
        }
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startAnim();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void stopAnim() {
        animator.scaleX(1).scaleY(1).start();
    }

    public interface RecordCallback {
        void onRecordingCancel();
        void onRecordingSend(String filename);
    }

}
