package com.iflytek.im.demo.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.bean.ImageBean;
import com.iflytek.im.demo.common.imageUtil.BitmapUtils;
import com.iflytek.im.demo.common.imageUtil.ViewHolder;

public class ImageWallAdapter extends CommonAdapter<String> {
	private static final String TAG = "ImageWallAdapter";

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	private  List<String> mSelectedImage = new LinkedList<>();


	@Override
	public String getItem(int position) {
		return mDatas.get(getCount() - position - 1);
	}

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public ImageWallAdapter(Context context, List<String> mDatas, int itemLayoutId, String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		final String filePath = mDirPath + "/" + item;
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select, R.drawable.picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, filePath);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {  
				final ImageBean imageBean = new ImageBean();
				// 已经选择过该图片
				if (mSelectedImage.contains(filePath)) {
					Log.e(TAG, "------>delete");
					mSelectedImage.remove(filePath);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
					// 将选择过得照片删除
					List<ImageBean> delete = new ArrayList<>();
					for (ImageBean im : BitmapUtils.tempSelectBitmap) {
						if (im.getPath().equals(filePath)) {
							delete.add(im);
						}
					}
					BitmapUtils.tempSelectBitmap.removeAll(delete);

				} else
				// 未选择该图片
				{
					mSelectedImage.add(filePath);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
//					imageBean.setSize(getBitmapSize(filePath));
					imageBean.setPath(filePath);
					imageBean.setName(item);
					imageBean.setImgDir(mDirPath);
					
					Log.i(TAG, "item  --->"+ mDirPath + item);
					String imgNameNoFmt = item.substring(0,item.indexOf("."));

					imageBean.setNameNoFmt(imgNameNoFmt);
					BitmapUtils.tempSelectBitmap.add(imageBean);
				}
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(filePath)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}

	
}
