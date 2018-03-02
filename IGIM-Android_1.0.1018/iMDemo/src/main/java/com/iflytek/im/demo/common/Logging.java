package com.iflytek.im.demo.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class Logging {

	protected static boolean mLoggingEnabled = true;
	public static final String APP_PREFIX = "IM_";
	
	public static void setDebugLogging(boolean enabled) {
		mLoggingEnabled = enabled;
	}
	
	public static boolean isDebugLogging() {
	    return mLoggingEnabled;
	}

	public static int v(String tag, String msg) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.v(APP_PREFIX + tag, msg);
		}
		return result;
	}

	public static int v(String tag, String msg, Throwable tr) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.v(APP_PREFIX + tag, msg, tr);
		}
		return result;
	}

	public static int d(String tag, String msg) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.w(APP_PREFIX + tag, msg);
		}
		return result;
	}

	public static int d(String tag, String msg, Throwable tr) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.w(APP_PREFIX + tag, msg, tr);
		}
		return result;
	}

	public static int i(String tag, String msg) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.i(APP_PREFIX + tag, msg);
		}
		return result;
	}

	public static int i(String tag, String msg, Throwable tr) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.i(APP_PREFIX + tag, msg, tr);
		}
		return result;
	}

	public static int w(String tag, String msg) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.w(APP_PREFIX + tag, msg);
		}
		return result;
	}

	public static int w(String tag, String msg, Throwable tr) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.w(APP_PREFIX + tag, msg, tr);
		}
		return result;
	}

	public static int w(String tag, Throwable tr) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.w(APP_PREFIX + tag, tr);
		}
		return result;
	}

	public static int e(String tag, String msg) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.e(APP_PREFIX + tag, msg);
		}
		return result;
	}

	public static int e(String tag, String msg, Throwable tr) {
		int result = 0;
		if (mLoggingEnabled) {
			result = Log.e(APP_PREFIX + tag, msg, tr);
		}
		return result;
	}
	
    public static synchronized void saveCustomLog(String file, String content) {
        if (!mLoggingEnabled) {
            return;
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!TextUtils.isEmpty(path)) {
        	File dir = new File(path);
            File f = new File(path + File.separator + file);
            FileOutputStream fs = null;
            try {
                dir.mkdirs();
                fs = new FileOutputStream(f, true);
                fs.write(content.getBytes());
                fs.write('\n');
                fs.close();
            } catch (IOException e) {
                e("", "", e);
            }
        }
    }
	
}
