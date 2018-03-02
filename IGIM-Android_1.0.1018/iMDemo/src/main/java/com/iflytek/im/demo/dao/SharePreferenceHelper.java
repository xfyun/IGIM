package com.iflytek.im.demo.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.Map;

import static com.iflytek.im.demo.Constants.Preference.KEY_EMPTY_BOOLEAN;
import static com.iflytek.im.demo.Constants.Preference.KEY_EMPTY_FLOAT;
import static com.iflytek.im.demo.Constants.Preference.KEY_EMPTY_INT;
import static com.iflytek.im.demo.Constants.Preference.KEY_EMPTY_LONG;
import static com.iflytek.im.demo.Constants.Preference.KEY_EMPTY_STRING;

public class SharePreferenceHelper {

	private Context mContext;
    private static SharedPreferences sharedPreferences;
	private static SharePreferenceHelper instance;

	public static SharePreferenceHelper getInstance(){
        if (instance == null) {
            throw new RuntimeException("Need Invoke createInstance method first");
        }
		return instance;
	}

    public static void createInstance(Context context){
        instance = new SharePreferenceHelper(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

	private SharePreferenceHelper(Context context) {
		this.mContext = context;
	}

	public void saveSharePreference(Map<String, Object> map) {
		Editor editor = sharedPreferences.edit();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object object = entry.getValue();
			if (object instanceof Boolean) {
				boolean b = (Boolean) object;
				editor.putBoolean(key, b);
			}
			if (object instanceof String) {
				String s = (String) object;
				editor.putString(key, s);
			}
			if (object instanceof Integer) {
				Integer i = (Integer) object;
				editor.putInt(key, i);
			}
			if (object instanceof Float) {
				Float f = (Float) object;
				editor.putFloat(key, f);
			}
			if (object instanceof Long) {
				Long l = (Long) object;
				editor.putLong(key, l);
			}
		}
		editor.commit();
	}


	public String getString(String key) {
		return sharedPreferences.getString(key, KEY_EMPTY_STRING);
	}

	public int getInt(String key) {
        return sharedPreferences.getInt(key, KEY_EMPTY_INT);
	}

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, KEY_EMPTY_FLOAT);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, KEY_EMPTY_BOOLEAN);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, KEY_EMPTY_LONG);
    }

    public Map getAll(){
        return sharedPreferences.getAll();
    }

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }
}
