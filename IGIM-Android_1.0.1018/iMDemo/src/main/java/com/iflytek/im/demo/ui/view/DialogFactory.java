package com.iflytek.im.demo.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.iflytek.im.demo.common.DisplayUtil;
import com.iflytek.im.demo.R;

public class DialogFactory {

	public static Dialog creatRequestDialog(final Context context, String tip) {

		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_layout);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		int width = DisplayUtil.getScreenWidth();
		lp.width = (int) (0.6 * width);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		if (tip == null || tip.length() == 0) {
			titleTxtv.setText("登录中...");
		} else {
			titleTxtv.setText(tip);
		}

		return dialog;
	}

}
