package com.iflytek.im.demo.adapter;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.im.demo.Config;
import com.iflytek.im.demo.R;

public class FaceGvAdapter extends BaseAdapter {
	private static final String TAG = "FaceGVAdapter";
	private List<String> mFaceCodeList;

	public FaceGvAdapter(List<String> list) {
		this.mFaceCodeList = list;
	}


	@Override
	public int getCount() {
		return mFaceCodeList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFaceCodeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.face_image, parent, false);
			holder.iv = (ImageView) convertView.findViewById(R.id.face_img);
			holder.tv = (TextView) convertView.findViewById(R.id.face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			Bitmap mBitmap = BitmapFactory.decodeStream(parent.getContext().getAssets().open("Face/" + mFaceCodeList.get(position)));
			holder.iv.setImageBitmap(mBitmap);
		} catch (IOException e) {
			if (Config.isDebug) {
                e.printStackTrace();
			}
		}
		holder.tv.setText( mFaceCodeList.get(position));

		return convertView;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;
	}
}
