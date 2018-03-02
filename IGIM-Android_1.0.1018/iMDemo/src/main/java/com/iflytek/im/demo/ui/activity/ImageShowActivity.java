package com.iflytek.im.demo.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.ui.view.photeview.PhotoView;

public class ImageShowActivity extends BaseActivity {
	private static final String TAG = "ImageShowActivity";

    public static final String IMG_PATH = "imgPath";


	private PhotoView mPhotoView;
	private String mImgPath;

    @Override
    protected void initMembers() {
        mImgPath = getIntent().getStringExtra(IMG_PATH);
        Log.i(TAG, "imagPath = " + mImgPath);
    }

    @Override
    protected void findViews() {

        mPhotoView = f(R.id.photoview);
    }

    @Override
    protected void initViews() {
            Glide.with(this)
                    .load(mImgPath)
                    .asBitmap()
                    .placeholder(R.drawable.loading_picture_anim)
                    .error(R.drawable.loading_picture_fail)
                    .into(mPhotoView);
    }

    @Override
	protected int getLayoutRes() {
		return R.layout.imageshow_layout;
	}


}
