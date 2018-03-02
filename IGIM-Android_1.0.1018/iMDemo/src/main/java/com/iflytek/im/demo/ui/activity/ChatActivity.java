package com.iflytek.im.demo.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ExpressionController;
import com.iflytek.im.demo.common.SoftInputUtil;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.ui.fragment.AddAttachmentFragment;
import com.iflytek.im.demo.ui.fragment.EmojiFragment;
import com.iflytek.im.demo.ui.fragment.SoundRecordFragment;

public class ChatActivity extends BaseActivity implements EmojiFragment.OnEmojiInputListener,
        AddAttachmentFragment.OnAddAttachmentClickListener, TextWatcher,
        View.OnFocusChangeListener ,SoundRecordFragment.RecordCallback{
    private static final String TAG = "ChatActivity";
    private static final int RESULT_CODE = 21;
    private static final int IMAGE_WALL_REQUEST_CODE = 20;
    private static final int SMALL_VIDEO_RESULT_CODE = 33;
    private static final int SMALL_VIDEO_REQUEST_CODE = 32;

    private RecyclerView mRVChatList;
    private ImageView mIVVoice;
    private ImageView mIVEmoji;
    private ImageView mIVAttachment;
    private FrameLayout mAttachmentContainer;
    private ImageView mChatSend;
    private EditText mETChatText;

    private AddAttachmentFragment mAddAttachmentFragment;
    private EmojiFragment mEmojiFragment;
    private SoundRecordFragment mSoundRecordFragment;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initMembers() {

    }

    @Override
    protected void findViews() {
        mRVChatList = f(R.id.chat_list);
        mETChatText = f(R.id.chat_text);
        mChatSend = f(R.id.chat_send);
        mIVVoice = f(R.id.chat_voice);
        mIVEmoji = f(R.id.chat_emoji);
        mIVAttachment = f(R.id.chat_attachment);
        mAttachmentContainer = f(R.id.attachment_container);
    }

    @Override
    protected void initViews() {
        mAddAttachmentFragment = AddAttachmentFragment.newInstance();
        mEmojiFragment = EmojiFragment.newInstance();
        mSoundRecordFragment = SoundRecordFragment.newInstance();
    }

    @Override
    protected void setupEvents() {
        mETChatText.addTextChangedListener(this);
        mETChatText.setOnFocusChangeListener(this);
        SoftInputUtil.addKeyboardCallback(getWindow(), new SoftInputUtil.KeyboardCallback() {
            @Override
            public void onKeyboardShow(int keyboardHeight) {
                Log.i(TAG, "onKeyboardShow: keyboardHeight = " + keyboardHeight);
                if (mAttachmentContainer.getVisibility() == View.VISIBLE) {
                    mAttachmentContainer.setVisibility(View.GONE);
                    mIVEmoji.setSelected(false);
                    mIVVoice.setSelected(false);
                    mIVAttachment.setSelected(false);
                }
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                        mAttachmentContainer.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, keyboardHeight);
                mAttachmentContainer.setLayoutParams(layoutParams);
            }

            @Override
            public void onKeyboardHide() {
                Log.i(TAG, "onKeyboardHide");
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                        mAttachmentContainer.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                mAttachmentContainer.setLayoutParams(layoutParams);
            }
        });
    }

    public void onVoiceClick(View view) {
        if (view.isSelected()) {
            mAttachmentContainer.setVisibility(View.GONE);
            view.setSelected(false);
        } else {
            SoftInputUtil.hideSoftInputView(this);
            mAttachmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.attachment_container, mSoundRecordFragment)
                    .commit();
            view.setSelected(true);
            mIVAttachment.setSelected(false);
            mIVEmoji.setSelected(false);
        }
    }

    public void onEmojiClick(View view) {
        if (view.isSelected()) {
            SoftInputUtil.showSoftInputView(this, mETChatText);
            mAttachmentContainer.setVisibility(View.GONE);
            view.setSelected(false);
        } else {
            SoftInputUtil.hideSoftInputView(this);
            mAttachmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.attachment_container, mEmojiFragment)
                    .commit();
            view.setSelected(true);
            mIVAttachment.setSelected(false);
            mIVVoice.setSelected(false);
        }
    }

    public void onAttachmentClick(View view) {
        if (view.isSelected()) {
            mAttachmentContainer.setVisibility(View.GONE);
            view.setSelected(false);
        } else {
            SoftInputUtil.hideSoftInputView(this);
            mAttachmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.attachment_container, mAddAttachmentFragment)
                    .commit();
            view.setSelected(true);
            mIVEmoji.setSelected(false);
            mIVVoice.setSelected(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAttachmentContainer.getVisibility() == View.VISIBLE) {
            mAttachmentContainer.setVisibility(View.GONE);
            mIVEmoji.setSelected(false);
            mIVVoice.setSelected(false);
            mIVAttachment.setSelected(false);
            return;
        }
        super.onBackPressed();
    }


    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((mETChatText.getText()));
        int iCursorEnd = Selection.getSelectionEnd((mETChatText.getText()));
        if (iCursorStart != iCursorEnd) {
            mETChatText.getText().replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((mETChatText.getText()));
        mETChatText.getText().insert(iCursor, text);
    }

    @Override
    public void onAddAttachmentClicked(@Constants.AttachmentType.Value int type) {

    }

    @Override
    public void onEmojiInput(String emoji) {
        insert(ExpressionController.getFace(emoji, this));
    }

    @Override
    public void onEmojiDelete() {
        ExpressionController.expressionDelete(mETChatText.getText());
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            mChatSend.setVisibility(View.GONE);
            mIVAttachment.setVisibility(View.VISIBLE);
        } else {
            mChatSend.setVisibility(View.VISIBLE);
            mIVAttachment.setVisibility(View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mETChatText && hasFocus) {
            SoftInputUtil.showSoftInputView(this, v);
        } else {
            SoftInputUtil.hideSoftInputView(this);
        }
    }

    @Override
    public void onRecordingCancel() {
        ToastUtil.showText("取消");
    }

    @Override
    public void onRecordingSend(String filename) {
        ToastUtil.showText("发送:"+filename);
    }
}
