package com.iflytek.im.demo.common.imageUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.iflytek.im.demo.bean.ImageBean;

/**
 * Created by Hankkin on 15/10/10.
 */
public class BitmapUtils {
	public static int max = 0;

	public static ArrayList<ImageBean> getTempSelectBitmap() {
		return tempSelectBitmap;
	}

	public static void setTempSelectBitmap(ArrayList<ImageBean> tempSelectBitmap) {
		BitmapUtils.tempSelectBitmap = tempSelectBitmap;
	}

	public static ArrayList<ImageBean> tempSelectBitmap = new ArrayList<>(); // 选择的图片的临时列表
	public static ArrayList<ImageBean> allSelectBitmap = new ArrayList<>(); // 用来保存所有选择发送和接收到的图片的缩略图

	public static ArrayList<ImageBean> getAllSelectBitmap() {
		return allSelectBitmap;
	}

	public static void setAllSelectBitmap(ArrayList<ImageBean> allSelectBitmap) {
		BitmapUtils.allSelectBitmap = allSelectBitmap;
	}

	public static Bitmap revisionImageSize(String path) throws IOException {
		if (path == null) {
			return null;
		}
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		if(in == null){
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 600) && (options.outHeight >> i <= 600)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	public static void saveCompressedBitmapToFile(Bitmap bitmap, String imgName, String imgFilePath) {
		File file = new File(imgFilePath);
		if (!file.exists()) {
			file.mkdir();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		File imageFile = new File(imgFilePath, imgName);
		try {
			int options = 100;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			while (baos.toByteArray().length / 1024 > 300) {
				baos.reset();
				options -= 10;
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			}
			FileOutputStream out = new FileOutputStream(imageFile);
			out.write(baos.toByteArray());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap readBitmapFromFile(String imgName)  {
		String fullImgPath = imgName;
		return BitmapFactory.decodeFile(fullImgPath);
	}

	public static void saveCompressedBitmapToFile(String imgPath, String fileName,
			String imgFilePath) {
		Bitmap bitmap;
		try {
			bitmap = BitmapUtils.revisionImageSize(imgPath);
			saveCompressedBitmapToFile(bitmap, fileName, imgFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	// 写入图片到指定的路径 每次发送完后都会保存所发送的图片的副本到指定的文件夹下
//	public static void saveBitmapToFile(String imgPath, String fileName, String imgSuffix,
//			String imgFilePath) {
//		Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
//		saveBitmapToFile(bitmap, fileName, imgSuffix, imgFilePath);
//	}
//
//	public static void saveBitmapToFile(Bitmap bitmap, String imgName, String imgSuffix,
//			String imgFilePath) {
//		File file = new File(imgFilePath);
//		if (!file.exists()) {
//			file.mkdir();
//		}
//		File imageFile = new File(imgFilePath, imgName + imgSuffix);
//		try {
//			FileOutputStream out = new FileOutputStream(imageFile);
//			bitmap.compress(CompressFormat.JPEG, 100, out);
//			out.flush();
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
