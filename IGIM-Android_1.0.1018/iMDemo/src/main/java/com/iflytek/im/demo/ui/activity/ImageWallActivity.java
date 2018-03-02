package com.iflytek.im.demo.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.ImageWallAdapter;
import com.iflytek.im.demo.bean.ImageFolder;
import com.iflytek.im.demo.common.ToastUtil;
import com.iflytek.im.demo.common.imageUtil.BitmapUtils;
import com.iflytek.im.demo.ui.view.ListImageDirPopupWindow;
import com.iflytek.im.demo.ui.view.ListImageDirPopupWindow.OnImageDirSelected;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ImageWallActivity extends BaseActivity implements OnImageDirSelected {
	private static final String TAG = "ImageWallActivity";
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private ImageWallAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFolder> mImageFolders = new ArrayList<>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;

	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
					mProgressDialog.dismiss();
					// 为View绑定数据
					data2View();
					// 初始化展示文件夹的popupWindow
					initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "没扫描到图片", Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
                return filename.endsWith(Constants.Storage.SUFFIX_PNG) ||
                        filename.endsWith(Constants.Storage.SUFFIX_JPG) ||
                        filename.endsWith(Constants.Storage.SUFFIX_JPEG);
            }
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageWallAdapter(getApplicationContext(), mImgs, R.layout.grid_item,
				mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "张");
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if(BitmapUtils.tempSelectBitmap != null)
			BitmapUtils.tempSelectBitmap.clear();
	}

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT,
				(int) (mScreenHeight * 0.7), mImageFolders, LayoutInflater.from(
						getApplicationContext()).inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.iamge_wall;
	}

    @Override
    protected void initMembers() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
    }

    @Override
    protected void findViews() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);

        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
    }

    @Override
    protected void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getImages();
    }

    @Override
    protected void setupEvents() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_image_wall, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_send ) {
			if(BitmapUtils.tempSelectBitmap == null || BitmapUtils.tempSelectBitmap.size() == 0 ){
				ToastUtil.showText("您尚未选择图片。");
				return false;
			}
			//点击完发送界面后就关闭，进入聊天界面
			Intent intent = new Intent();
			ImageWallActivity.this.setResult(Constants.RequestCodeAndResultCode.PICTURE_RESULTCODE, intent);
			ImageWallActivity.this.finish();
            return true;
		}
		return false;
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageWallActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor cursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?", new String[] {
								"image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_TAKEN+" DESC");
				if (cursor == null || cursor.getCount() == 0) {
					mHandler.sendEmptyMessage(0);
					return;
				}
				cursor.moveToLast();
				do{
					// 获取图片的路径
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					//Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFolder imageFolder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFolder = new ImageFolder();
						imageFolder.setDir(dirPath);
						imageFolder.setFirstImagePath(path);
					}

					String[] fileNames= parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					});
					int picSize = 0;
					if(fileNames != null ){
						picSize = fileNames.length;
					}else{
						continue;
					}
					totalCount += picSize;

					imageFolder.setCount(picSize);
					mImageFolders.add(imageFolder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}while(cursor.moveToPrevious());
				cursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	@Override
	public void selected(ImageFolder floder) {

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageWallAdapter(getApplicationContext(), mImgs, R.layout.grid_item,
				mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

}
