package com.iflytek.im.demo.processor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.iflytek.im.demo.common.http.HttpPacket;
import com.iflytek.im.demo.common.http.HttpUtil;
import com.iflytek.im.demo.common.http.RequestBuilder;
import com.iflytek.im.demo.listener.ResultListener;
import com.iflytek.im.demo.listener.UserListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class Processor {
	private static String TAG = "Verification";
	private static Handler mMainHandler = new Handler(Looper.getMainLooper());

	public static void loginVerification(final String appid, final String uid,
										 final String password, final ResultListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					HttpPacket loginVerificationRequest = RequestBuilder
							.buildLoginVerificationRequest(appid, uid, password);
					String requestResult = HttpUtil.processHttpsRequest(loginVerificationRequest);
					if(requestResult != null && requestResult.equals("network error")){
						listener.onFailed();
						return;
					}
					
					JSONObject loginResp = new JSONObject(requestResult);
					if (loginResp.has("ret") && loginResp.getInt("ret") == 0) {
						listener.onSuccess();
					} else {
						listener.onFailed();
					}
				} catch (Exception e) {
					e.printStackTrace();
					listener.onFailed();
				}
			}
		}).start();
	}

	public static void register(final String appid, final String uid, final String password,
								final ResultListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Log.e(TAG, "运行Register");
					HttpPacket loginVerificationRequest = RequestBuilder.buildRegisterRequest(appid,
							uid, password);
					JSONObject loginResp = new JSONObject(
							HttpUtil.processHttpsRequest(loginVerificationRequest));
					if (loginResp.has("ret") && loginResp.getInt("ret") == 0) {
						listener.onSuccess();
					}else if(loginResp.has("ret") && loginResp.getInt("ret") == -1){
						listener.onFailed();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 获取好友列表
	public static void getUser(final String appid, final UserListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					HttpPacket loginRequest = RequestBuilder.buildUserRequest(appid);
					JSONObject loginResp = new JSONObject(HttpUtil.processRequest(loginRequest));
					if (loginResp.has("ret") && loginResp.getInt("ret") == 0) {
						JSONArray userNamesArr = loginResp.getJSONArray("uids");
						List<String> userNamesList = new ArrayList<>();
						for (int i = 0; i < userNamesArr.length(); i++) {
							String userName = (String) userNamesArr.get(i);
							if (!"".equals(userName) && !" ".equals(userName)) {
								userNamesList.add(userName);
							}
						}
						invokeSuccessListener(userNamesList, listener);
					} else {
						Log.e(TAG, "getUser failed reponse ret "
								+ (loginResp.has("ret") ? "null" : loginResp.getInt("ret")));
					    invokeFailedListener(listener);
						listener.onFailed();
					}
				} catch (Exception e) {
					Log.e(TAG, "getuser failed", e);
					// invokeFailedListener(listener);
					listener.onFailed();
				}
			}
		}).start();
	}
	
	private static void invokeSuccessListener(final List<String> userNamesList,final UserListener listener){
		mMainHandler.post(new Runnable() {
			public void run() {
				listener.onUser(userNamesList);
			}
		});
	}
	
	private static void invokeFailedListener(final UserListener listener){
		mMainHandler.post(new Runnable() {
			public void run() {
				listener.onFailed();
			}
		});
	}
}
