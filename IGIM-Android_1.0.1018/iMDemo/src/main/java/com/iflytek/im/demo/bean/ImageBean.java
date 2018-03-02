package com.iflytek.im.demo.bean;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.iflytek.im.demo.common.imageUtil.BitmapUtils;

public class ImageBean implements Parcelable {
	public String id;
    public String path;
    private Bitmap bitmap;
    private String name;//图片名称
    private String url;//图片上传后服务端返回的url
    private String format;//图片的后缀
    private int size;//图片的大小
    private boolean isUpload;//图片是否已经上传
    private String md5String;
    private String nameNoFmt;
    private String imgDir;
    

    public String getImgDir() {
		return imgDir;
	}

	public void setImgDir(String imgDir) {
		this.imgDir = imgDir;
	}

	public String getNameNoFmt() {
		return nameNoFmt;
	}

	public void setNameNoFmt(String nameNoFmt) {
		this.nameNoFmt = nameNoFmt;
	}

	public String getMd5String() {
		return md5String;
	}

	public void setMd5String(String md5String) {
		this.md5String = md5String;
	}

	public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String thumbnailPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        if(bitmap == null){
            try {
                bitmap = BitmapUtils.revisionImageSize(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.path);
		dest.writeParcelable(this.bitmap, flags);
		dest.writeString(this.name);
		dest.writeString(this.url);
		dest.writeString(this.format);
		dest.writeInt(this.size);
		dest.writeByte(this.isUpload ? (byte) 1 : (byte) 0);
		dest.writeString(this.md5String);
		dest.writeString(this.nameNoFmt);
		dest.writeString(this.imgDir);
		dest.writeString(this.thumbnailPath);
	}

	public ImageBean() {
	}

	protected ImageBean(Parcel in) {
		this.id = in.readString();
		this.path = in.readString();
		this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
		this.name = in.readString();
		this.url = in.readString();
		this.format = in.readString();
		this.size = in.readInt();
		this.isUpload = in.readByte() != 0;
		this.md5String = in.readString();
		this.nameNoFmt = in.readString();
		this.imgDir = in.readString();
		this.thumbnailPath = in.readString();
	}

	public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>() {
		@Override
		public ImageBean createFromParcel(Parcel source) {
			return new ImageBean(source);
		}

		@Override
		public ImageBean[] newArray(int size) {
			return new ImageBean[size];
		}
	};
}
