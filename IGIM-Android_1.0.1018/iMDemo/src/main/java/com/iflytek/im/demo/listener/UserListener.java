package com.iflytek.im.demo.listener;

import java.util.List;


public interface UserListener {
	void onUser(List<String> users);
	void onFailed();
}
