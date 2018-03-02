package com.iflytek.im.demo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageFolder implements Parcelable {
	/**
	 * 图片的文件夹路径
	 */
	private String dir;

	/**
	 * 第一张图片的路径
	 */
	private String firstImagePath;

	/**
	 * 文件夹的名称
	 */
	private String name;

	/**
	 * 图片的数量
	 */
	private int count;

	public String getDir()
	{
		return dir;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImagePath()
	{
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath)
	{
		this.firstImagePath = firstImagePath;
	}

	public String getName()
	{
		return name;
	}
	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.dir);
		dest.writeString(this.firstImagePath);
		dest.writeString(this.name);
		dest.writeInt(this.count);
	}

	public ImageFolder() {
	}

	protected ImageFolder(Parcel in) {
		this.dir = in.readString();
		this.firstImagePath = in.readString();
		this.name = in.readString();
		this.count = in.readInt();
	}

	public static final Parcelable.Creator<ImageFolder> CREATOR = new Parcelable.Creator<ImageFolder>() {
		@Override
		public ImageFolder createFromParcel(Parcel source) {
			return new ImageFolder(source);
		}

		@Override
		public ImageFolder[] newArray(int size) {
			return new ImageFolder[size];
		}
	};
}
