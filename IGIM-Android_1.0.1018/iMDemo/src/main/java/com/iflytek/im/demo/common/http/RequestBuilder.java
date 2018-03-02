package com.iflytek.im.demo.common.http;


import com.iflytek.im.demo.Constants;

import org.json.JSONException;
import org.json.JSONObject;



public class RequestBuilder {
	
	public static HttpPacket buildLoginVerificationRequest(String appId, String uid, String password) throws JSONException{
		JSONObject builderData = new JSONObject();
		builderData.put("appid", appId);
		builderData.put("uid", uid);
		builderData.put("password", password);

		
		String content = builderData.toString();
		HttpPacket request = HttpPacket.buildPostRequest(Constants.Url.LOGIN, content);
		request.addHeader("Content-Type", "application/json;charset=utf-8");
		
		return request;
	}
	
	public static HttpPacket  buildRegisterRequest(String appId, String uid, String password) throws JSONException{
		JSONObject builderData = new JSONObject();
		builderData.put("appid", appId);
		builderData.put("uid", uid);
		builderData.put("password", password);

		String content = builderData.toString();
		HttpPacket request = HttpPacket.buildPostRequest(Constants.Url.REGISTER, content);
		request.addHeader("Content-Type", "application/json;charset=utf-8");
		
		return request;
	}
	
	public static HttpPacket  buildUserRequest(String appId) throws JSONException{
		JSONObject builderData = new JSONObject();
		builderData.put("appid", appId);
		String content = builderData.toString();
		HttpPacket request = HttpPacket.buildGetRequest(Constants.Url.GET_USERS, content);
		request.addHeader("Content-Type", "application/json;charset=utf-8");
		return request;
	}	

	public static HttpPacket buildUserTokenRequest(String uid, String appId) throws JSONException {
		JSONObject builderData = new JSONObject();
		builderData.put("appid", appId);
		builderData.put("uid", uid);
		String content = builderData.toString();
		HttpPacket request = HttpPacket.buildPostRequest(Constants.Url.GET_TOKEN, content);
		request.addHeader("Content-Type", "application/json;charset=utf-8");
		return request;
	}

}
