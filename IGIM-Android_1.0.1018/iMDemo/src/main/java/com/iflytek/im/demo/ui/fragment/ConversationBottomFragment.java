package com.iflytek.im.demo.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.bean.ImageBean;
import com.iflytek.im.demo.common.ExpressionController;
import com.iflytek.im.demo.common.PermissonUtil;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.common.imageUtil.BitmapUtils;
import com.iflytek.im.demo.ui.activity.ImageWallActivity;
import com.iflytek.im.demo.ui.activity.SmallVideoRecorderActivity;
import com.iflytek.im.demo.ui.view.AudioRecorderButton;
import com.iflytek.im.demo.ui.view.MsgEditText;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

public class ConversationBottomFragment extends Fragment implements OnClickListener {

    private final String TAG = "ConversationBotFragment";

    protected Button mVoiceMsg;
    private Button mTextMsg;
    private Button mSendBtn;
    protected Button mOtherFunc;
    protected Button mEmojMsg;
    protected MsgEditText mInputEditText;
    protected AudioRecorderButton mAudioRecorderBtn;
    protected LinearLayout mAllEmoj;
    protected TableLayout mOtherFuncDetail;
    protected View mView;
    protected ViewPager mViewPager;
    protected LinearLayout mFaceDotsLayout;
    protected ImageButton mAddPictureBtn;
    protected ImageButton mSmallVideoRecorderBtn; // 拍摄小视屏按钮
    protected ImageButton mVoiceCallBtn;
    protected ImageButton mVideoCallBtn;
    protected KPSwitchPanelLinearLayout mPanelRoot;


    private Activity mCurrentActivity;
    private FragmentCallback mFragmentCallback;
    private InputMethodManager mInputMethodManager;
    private int mTxtPostType;


    public interface FragmentCallback {
        void sendTextMsg(String text, int txtPostType);

        void sendAudioMsg(String audioFilePath, String audioFileName, boolean isNeed2Txt);

        void sendImgMsg(ImageBean imageBean);

        void sendVideoMsg(String videoPath,String thumbnailPath,int duration);

        void onVoiceCallClick();
        void onVideoCallClick();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mCurrentActivity = getActivity();
        if (mCurrentActivity instanceof FragmentCallback) {
            mFragmentCallback = (FragmentCallback) mCurrentActivity;
        }
        mInputMethodManager = (InputMethodManager) mCurrentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_bottom_fragment, container, false);
        mPanelRoot = (KPSwitchPanelLinearLayout) view.findViewById(R.id.panel_root);
        mAllEmoj = (LinearLayout) view.findViewById(R.id.chat_face_container);
        mOtherFuncDetail = (TableLayout) view.findViewById(R.id.other_func_detail);


        mVoiceMsg = (Button) view.findViewById(R.id.voice_msg);
      
        mTextMsg = (Button) view.findViewById(R.id.keyboard);
        mInputEditText = (MsgEditText) view.findViewById(R.id.input_sms);
        mSendBtn = (Button) view.findViewById(R.id.send_sms);
        mOtherFunc = (Button) view.findViewById(R.id.other_func);
        mEmojMsg = (Button) view.findViewById(R.id.image_face);
        mAudioRecorderBtn = (AudioRecorderButton) view.findViewById(R.id.sound_recording);
        mVoiceCallBtn = (ImageButton) view.findViewById(R.id.chat_by_voice);
        mVideoCallBtn = (ImageButton) view.findViewById(R.id.chat_by_video);

        mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mViewPager = (ViewPager) view.findViewById(R.id.face_viewpager);
        mFaceDotsLayout = (LinearLayout) view.findViewById(R.id.face_dots_container);
        mAddPictureBtn = (ImageButton) view.findViewById(R.id.picture);
        mSmallVideoRecorderBtn = (ImageButton) view.findViewById(R.id.small_video);
        PermissonUtil.getRecordState();

        KeyboardUtil.hideKeyboard(mInputEditText);
        initListener();
        mView = view;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setKeyBoard();
    }

    private void initListener() {
        initViewListener();

        mEmojMsg.setOnClickListener(this);
        mTextMsg.setOnClickListener(this);
        mInputEditText.setOnClickListener(this);
        mOtherFunc.setOnClickListener(this);
        mVoiceMsg.setOnClickListener(this);
        mAddPictureBtn.setOnClickListener(this);
        mSmallVideoRecorderBtn.setOnClickListener(this);
        mVoiceCallBtn.setOnClickListener(this);
        mVideoCallBtn.setOnClickListener(this);

        mInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    SendMsg();
                    return true;
                }
                return false;
            }
        });

        mSendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMsg();
            }
        });

        // //注册录音结束时的监听
        mAudioRecorderBtn.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {

            @Override
            public void onFinish(int duration, final String audioFilePath, final String audioFileName,
                                 final boolean isNeed2Txt) {
                // 录音完了之后发送音频文件
                Log.d(TAG, "onFinish: start");
                mFragmentCallback.sendAudioMsg(audioFilePath, audioFileName, isNeed2Txt);
            }
        });


        mAudioRecorderBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAudioRecorderBtn.isNeedAudio2Txt()) {
                    mAudioRecorderBtn.setText("语音转写文字发送");
                    mAudioRecorderBtn.setNeedAudio2Txt(true);
                } else {
                    mAudioRecorderBtn.setText(R.string.btn_press_and_talk);
                    mAudioRecorderBtn.setNeedAudio2Txt(false);
                }

                mInputEditText.setVisibility(View.INVISIBLE);
                mVoiceMsg.setVisibility(View.INVISIBLE);
                mTextMsg.setVisibility(View.VISIBLE);
                mAudioRecorderBtn.setVisibility(View.VISIBLE);
                mOtherFunc.setVisibility(View.VISIBLE);
                mEmojMsg.setVisibility(View.GONE);
                mPanelRoot.setVisibility(View.GONE);
                mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
                KeyboardUtil.hideKeyboard(mInputEditText);
            }
        });

    }

    public void SendMsg(){
        final String textMsgContent = mInputEditText.getText().toString();
        final String text = ExpressionController.expressionReduce(textMsgContent);
        String text2 = text.trim();
        if (!TextUtils.isEmpty(text2)) {
            mFragmentCallback.sendTextMsg(text, mTxtPostType);
        }else {
            ToastUtil.showText("消息不能为空");
            mInputEditText.setText("");
        }
    }

    private void initViewListener() {
        mVoiceMsg.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {


                return true;
            }
        });


        mInputEditText.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mTxtPostType == 0) {
                    mInputEditText.setHint("文字转语音发送");
                    mInputEditText.setBackgroundColor(Color.BLACK);
                    mInputEditText.setTextColor(Color.GREEN);
                    mEmojMsg.setVisibility(View.GONE);
                    KeyboardUtil.showKeyboard(mInputEditText);
                    mTxtPostType = 1;
                } else if (mTxtPostType == 1) {
                    mInputEditText.setHint(R.string.edittext_notice_0);
                    mInputEditText.setBackgroundColor(Color.TRANSPARENT);
                    mInputEditText.setTextColor(Color.BLACK);
                    mEmojMsg.setVisibility(View.VISIBLE);
                    KeyboardUtil.showKeyboard(mInputEditText);
                    mTxtPostType = 0;
                }
                mAllEmoj.setVisibility(View.GONE);
                mOtherFuncDetail.setVisibility(View.GONE);
                mVoiceMsg.setVisibility(View.VISIBLE);
                mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);

                return false;
            }
        });

        mInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInputEditText.getText().toString() != null && !mInputEditText.getText().toString().equals("")) {
                    mOtherFunc.setVisibility(View.INVISIBLE);
                    mSendBtn.setVisibility(View.VISIBLE);
                } else {
                    mOtherFunc.setVisibility(View.VISIBLE);
                    mSendBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyboard:
                mVoiceMsg.setVisibility(View.VISIBLE);
                mTextMsg.setVisibility(View.GONE);
                mInputEditText.setVisibility(View.VISIBLE);
                mAudioRecorderBtn.setVisibility(View.GONE);
                mEmojMsg.setVisibility(View.VISIBLE);
                if(mInputEditText.getText() != null && !TextUtils.isEmpty(mInputEditText.getText())){
                    mSendBtn.setVisibility(View.VISIBLE);
                    mOtherFunc.setVisibility(View.INVISIBLE);
                }else {
                    mOtherFunc.setVisibility(View.VISIBLE);
                }
                mOtherFuncDetail.setVisibility(View.GONE);
                mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
				KeyboardUtil.showKeyboard(mInputEditText);
                break;
		    case R.id.other_func:
                if (mOtherFuncDetail.getVisibility() == View.GONE) {
                    KeyboardUtil.hideKeyboard(mInputEditText);
                    mAllEmoj.setVisibility(View.GONE);
                    mOtherFuncDetail.setVisibility(View.VISIBLE);
                } else
                    mOtherFuncDetail.setVisibility(View.GONE);
                mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
                break;
            case R.id.voice_msg:
				KeyboardUtil.hideKeyboard(mInputEditText);
                mSendBtn.setVisibility(View.GONE);
                mInputEditText.setVisibility(View.INVISIBLE);
                mVoiceMsg.setVisibility(View.INVISIBLE);
                mTextMsg.setVisibility(View.VISIBLE);
                mAudioRecorderBtn.setVisibility(View.VISIBLE);
                mOtherFunc.setVisibility(View.VISIBLE);
 				mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
                mEmojMsg.setVisibility(View.GONE);
               
                mPanelRoot.setVisibility(View.GONE);
                
                break;
            case R.id.input_sms:
                 KeyboardUtil.showKeyboard(mInputEditText);
                mAllEmoj.setVisibility(View.GONE);
                mOtherFuncDetail.setVisibility(View.GONE);
                mVoiceMsg.setVisibility(View.VISIBLE);
                mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
                break;
            case R.id.picture:
                Intent intent = new Intent(getActivity(), ImageWallActivity.class);
                startActivityForResult(intent, Constants.RequestCodeAndResultCode.PICTURE_REQUESTCODE);
                mPanelRoot.setVisibility(View.GONE);
                break;
            case R.id.small_video:
                intent = new Intent(getActivity(), SmallVideoRecorderActivity.class);
                startActivityForResult(intent,Constants.RequestCodeAndResultCode.SMALL_VIDEO_REQUESTCODE);
                mPanelRoot.setVisibility(View.GONE);
                break;
            case R.id.chat_by_voice:
                mFragmentCallback.onVoiceCallClick();
                break;
            case R.id.chat_by_video:
                mFragmentCallback.onVideoCallClick();
                break;
            default:

                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.RequestCodeAndResultCode.PICTURE_RESULTCODE) {
            for (final ImageBean imageBean : BitmapUtils.tempSelectBitmap) {
                // 先要上传图片
                mFragmentCallback.sendImgMsg(imageBean);
            }
            // 发送完成后清空暂存的选组缩略图并放在总的存储缩略图中
            for (ImageBean imageBean : BitmapUtils.tempSelectBitmap) {
                BitmapUtils.allSelectBitmap.add(imageBean);
            }
            BitmapUtils.tempSelectBitmap.clear();
            super.onActivityResult(requestCode, resultCode, data);
        }else if(resultCode == Constants.RequestCodeAndResultCode.SMALL_VIDEO_RESULTCODE){
            Bundle bundle = data.getExtras();
            final String videoPath = bundle.getString(Constants.Parameter.KEY_VIDEO_PATH);
            final String videoHeightStr = bundle.getString(Constants.Parameter.KEY_VIDEO_HEIGHT);
            final String videoWidthStr = bundle.getString(Constants.Parameter.KEY_VIDEO_WIDTH);

            int thumbnailWidth 	=	Integer.parseInt(videoWidthStr) / 5 * 2;
            int thunbnailHeight = 	Integer.parseInt(videoHeightStr) / 5 * 2;
            String name = videoPath.substring(videoPath.lastIndexOf("/")+1);
            name = name.substring(0,videoPath.indexOf("."));
            String thumbnailName = name + Constants.Storage.SUFFIX_JPG;
            String thumbnailPath = FileConstant.getPathVideo() + "/" + thumbnailName;
            final Bitmap bitmap = getVideoBitmap(videoPath, thumbnailWidth, thunbnailHeight);
            BitmapUtils.saveCompressedBitmapToFile(bitmap, thumbnailName, FileConstant.getPathVideo());
            int duration = 5;
            mFragmentCallback.sendVideoMsg(videoPath,thumbnailPath,duration);

        }


    }


    public Bitmap getVideoBitmap(String filePath, int width, int height) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        Bitmap bitmap = mmr.getFrameAtTime();
        mmr.release();
        return bitmap;
    }



    public void sendTextMsgSuccuss() {
        mOtherFuncDetail.setVisibility(View.GONE);
        mSendBtn.setVisibility(View.GONE);
        mOtherFunc.setVisibility(View.VISIBLE);
        mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
        mInputEditText.setText("");
    }

    private void setKeyBoard() {
        KeyboardUtil.attach(getActivity(), mPanelRoot,
                // Add keyboard showing state callback, do like this when you want to listen in the
                // keyboard's show/hide change.
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        Log.d(TAG, String.format("Keyboard is %s", isShowing ? "showing" : "hiding"));
                    }
                });

        // If there are several sub-panels in this activity ( e.p. function-panel, emoji-panel).
        KPSwitchConflictUtil.attach(mPanelRoot, mInputEditText,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            mInputEditText.clearFocus();
                            if(mAllEmoj.getVisibility() == View.VISIBLE){
                                //点击的是表情
                                mEmojMsg.setBackgroundResource(R.drawable.keyboard);
                            }else if(mAllEmoj.getVisibility() == View.GONE){
                                //点击的是其他
                                mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
                            }
                        } else {
                            mInputEditText.requestFocus();
                            
                            mEmojMsg.setBackgroundResource(R.drawable.chat_emo_normal);
                        }
                    }
                },
                new KPSwitchConflictUtil.SubPanelAndTrigger(mAllEmoj, mEmojMsg),
                new KPSwitchConflictUtil.SubPanelAndTrigger(mOtherFuncDetail, mOtherFunc));
    }
    public View getEmoj(){
        return  mAllEmoj;
    }
    public View getOtherFun(){
        return mOtherFuncDetail;
    }




}
