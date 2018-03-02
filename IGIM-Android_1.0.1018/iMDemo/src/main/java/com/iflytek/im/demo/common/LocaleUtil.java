package com.iflytek.im.demo.common;

import com.iflytek.im.demo.ImApplication;

import java.util.Locale;

/**
 * Created by IMXQD on 2016/8/29.
 * 用于本地化、国际化
 */
public class LocaleUtil {

    public static boolean isChinese() {
        return isSimplifiedChinese() || isTraditionalChinese();
    }

    public static boolean isSimplifiedChinese() {
        Locale locale = ImApplication.getApp().getResources().getConfiguration().locale;
        return locale.getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage());
    }

    public static boolean isTraditionalChinese() {
        Locale locale = ImApplication.getApp().getResources().getConfiguration().locale;
        return locale.getLanguage().equals(Locale.TRADITIONAL_CHINESE.getLanguage());
    }

    public static boolean isEnglish() {
        Locale locale = ImApplication.getApp().getResources().getConfiguration().locale;
        return locale.getLanguage().equals(Locale.ENGLISH.getLanguage());
    }

}
