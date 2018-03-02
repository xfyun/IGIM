package com.iflytek.im.demo.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iflytek.im.demo.ui.gif.AnimatedGifDrawable;
import com.iflytek.im.demo.ui.gif.AnimatedImageSpan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

public class ExpressionController {
	
	private static final String TAG = "ExpressionController";
	
	public static void expressionDelete(Editable textContent) {
		if (textContent != null && textContent.length() != 0) {
			int iCursorEnd = Selection.getSelectionEnd(textContent);
			if (iCursorEnd > 0) {
				String st = "xxx@2x.png";
				 textContent.delete(iCursorEnd - st.length(),iCursorEnd);
			}
		}
	}
	
	public static SpannableStringBuilder expressionBuilder(String content, Context context) {
		SpannableStringBuilder sb = new SpannableStringBuilder(content);
		
		if (content != null) {
			String regex = "\\[+\\d{3}+\\]";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(content);
			while (m.find()) {
				String tempText = m.group();
				String png = tempText.substring(1,4)+"@2x.png";

				try {
                    Bitmap icon = BitmapFactory.decodeStream(context.getAssets().open("Face/"+png));
					sb.setSpan(new ImageSpan(context, icon),
							m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (IOException ignored) {

				}
			}
		}
		return sb;
	}
	
	public static String expressionReduce(String content) {
		String regex = "\\d{3}+\\@2x+\\.png";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		int i = 0;
		while (m.find()) {
			i++;
			String tempText = m.group();
			String png = "["+tempText.substring(0,3)+"]";
			try {
				content = content.replace(tempText, png);
			} catch (Exception ignored) {
				
			}
		}	
		return content;
	}

	public static SpannableStringBuilder getFace(String png, Context context) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		try {
			sb.append(png);
			sb.setSpan(
					new ImageSpan(context,
							BitmapFactory.decodeStream(context.getAssets().open("Face/" + png))),
					sb.length() - png.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb;
	}
	

}
