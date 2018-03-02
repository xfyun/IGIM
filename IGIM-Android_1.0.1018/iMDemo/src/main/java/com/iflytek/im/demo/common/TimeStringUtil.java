package com.iflytek.im.demo.common;

import android.annotation.SuppressLint;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;

import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by imxqd on 2016/8/29.
 * 时间显示的工具函数
 */

public class TimeStringUtil {

    /**
     * 获得简短的、描述时间的字符串
     *
     * 格式为：
     * 17:00、8:30 （今天）
     * 昨天上午、昨天凌晨、yesterday（英文环境没有时间段的描述，因为会很长）
     * 1月29日、5/19 （今年）
     * 1995/1/29、5/19/1996
     *
     * @param pastTime 晚于当前时间的某个时间
     * @return 简短的、描述时间的字符串
     */
    public static String getShortDateTimeString(long pastTime) {
        if (LocaleUtil.isChinese()) {
            return getShortDateTimeStringChinese(pastTime);
        } else return getShortDateTimeStringEnglish(pastTime);
    }

    private static String getShortDateTimeStringChinese(long pastTime) {
        int dayGap = getTimeGap(pastTime, System.currentTimeMillis(), Calendar.DATE);
        DateTime dt = new DateTime(pastTime);
        if (dayGap == 0) { // 今天
            return dt.toString("H:mm");
        } else if (dayGap == -1) { // 昨天
            return getString(R.string.yesterday) + getTimePeriodStr(pastTime);
        } else if (getTimeGap(pastTime, System.currentTimeMillis(), Calendar.YEAR) == 0) {
            // 今年
            String m = getString(R.string.month);
            String d = getString(R.string.day);
            return dt.toString("M" + m + "d" + d);
        } else {
            // 反正不是今年
            return dt.toString("yyyy/M/d");
        }
    }

    private static String getShortDateTimeStringEnglish(long pastTime) {
        int dayGap = getTimeGap(pastTime, System.currentTimeMillis(), Calendar.DATE);
        DateTime dt = new DateTime(pastTime);
        if (dayGap == 0) { // today
            return dt.toString("H:mm");
        } else if (dayGap == -1) {
            return getString(R.string.yesterday);
        } else if (getTimeGap(pastTime, System.currentTimeMillis(), Calendar.YEAR) == 0) {
            // this year
            return dt.toString("M/d");
        } else {
            // 反正不是今年
            return dt.toString("M/d/yyyy");
        }
    }

    /**
     * 获得描述时间的字符串，精确
     *
     * 格式为：
     * 17:00、8:30 （今天）
     * 昨天22:30、16:40 yesterday
     * 6月20日9:59、23:08, Dec 25 （今年）
     * 2012年12月20日7:30、11,11, Jun 1, 2010
     *
     * @param pastTime 晚于当前时间的某个时间
     * @return 符合格式要求的、描述时间的字符串
     */
    public static String getExactDateTimeString(long pastTime) {
        if (LocaleUtil.isChinese()) {
            return getExactDateTimeStringChinese(pastTime);
        } else return getExactDateTimeStringEnglish(pastTime);
    }

    private static String getExactDateTimeStringChinese(long pastTime) {
        int dayGap = getTimeGap(pastTime, System.currentTimeMillis(), Calendar.DATE);
        DateTime dt = new DateTime(pastTime);
        String timeStr = dt.toString("H:mm");
        if (dayGap == 0) { // 今天
            return timeStr;
        } else if (dayGap == -1) { // 昨天
            return getString(R.string.yesterday) + timeStr;
        } else {
            String m = getString(R.string.month);
            String d = getString(R.string.day);
            String pattern = "M" + m + "d" + d;
            if (getTimeGap(pastTime, System.currentTimeMillis(), Calendar.YEAR) < 0) {
                String y = getString(R.string.year);
                pattern = "yyyy" + y + pattern;
            }
            return dt.toString(pattern) + timeStr;
        }
    }

    private static String getExactDateTimeStringEnglish(long pastTime) {
        int dayGap = getTimeGap(pastTime, System.currentTimeMillis(), Calendar.DATE);
        DateTime dt = new DateTime(pastTime);
        String timeStr = dt.toString("H:mm");
        if (dayGap == 0) { // today
            return timeStr;
        } else if (dayGap == -1) { // yesterday
            return timeStr + ", " + getString(R.string.yesterday);
        } else {
            String tmd = timeStr + ", " + dt.toString("MMM d");
            if (getTimeGap(pastTime, System.currentTimeMillis(), Calendar.YEAR) == 0) {
                // this year
                return tmd;
            } else {
                // past years
                return tmd + ", " + dt.toString("yyyy");
            }
        }
    }

    public static String getTimePeriodStr(long time) {
        int hour = new DateTime(time).getHourOfDay();
        String[] periods = getStringArray(R.array.time_period);
        int[] limits = { 6, 8, 12, 13, 17, 19, 22 };
        for (int i = 0; i < limits.length; i++) {
            if (hour < limits[i]) {
                return periods[i];
            }
        }
        return periods[7];
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDurationString(int duration) {
        float second = duration / 1000f;
        if (second < 1) {
            return "< 1s";
        } else if (second < 3600) {
            return new SimpleDateFormat().format(new Date(duration));
        } else return new SimpleDateFormat("HH:mm:ss").format(new Date(duration));
    }

    private static int getTimeGap(long start, long end, int type) {
        DateTime sDt = new DateTime(start).withTime(0, 0, 0, 0);
        DateTime eDt = new DateTime(end)  .withTime(0, 0, 0, 0);

        int gap = 0;
        if (type == Calendar.DATE) {
            gap = Days.daysBetween(sDt, eDt).getDays();
        } else if (type == Calendar.WEEK_OF_YEAR) {
            sDt = sDt.withDayOfWeek(1);
            eDt = eDt.withDayOfWeek(1);
            gap = Weeks.weeksBetween(sDt, eDt).getWeeks();
        } else if (type == Calendar.MONTH) {
            sDt = sDt.withDayOfMonth(1);
            eDt = eDt.withDayOfMonth(1);
            gap = Months.monthsBetween(sDt, eDt).getMonths();
        } else if (type == Calendar.YEAR) {
            return eDt.getYear() - sDt.getYear();
        }

        if (start < end) {
            gap = -gap;
        }

        return gap;
    }


    /**
     * 给定诸如 yyyy-MM-dd HH:mm:ss 格式的时间字符串,返回适合Explore中显示的时间字符串
     * @param datetime  yyy-MM-dd HH:mm:ss 格式的时间字符串
     * @return 适合显示的时间字符串
     */
    public static String formatDatetime(String datetime) {
        final boolean isChinese = LocaleUtil.isChinese();
        final String GAP = " ";
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime time = formatter.parseDateTime(datetime);
        DateTime now = DateTime.now();
        int dayGap = getTimeGap(time.getMillis(), now.getMillis(), Calendar.DATE);
        if (dayGap == 0) {
            Period p = new Period(time, now, PeriodType.standard());
            if (p.getHours() == 0) {
                if (p.getMinutes() < 5) {
                    return getString(R.string.time_just);
                } else {
                    return p.getMinutes() + GAP + getString(R.string.time_minutes_age);
                }
            } else {
                return p.getHours() + GAP + getString(R.string.time_hours_age);
            }
        } else if (dayGap == -1) {
            if (isChinese) {
                return getString(R.string.yesterday) + GAP + time.toString("HH:mm");
            } else {
                return time.toString("HH:mm") + GAP + getString(R.string.yesterday);
            }
        } else if (dayGap == -2) {
            if (isChinese) {
                return getString(R.string.time_before_yesterday) + GAP + time.toString("HH:mm");
            } else {
                return time.toString("HH:mm") + ", " + getString(R.string.time_before_yesterday);
            }
        } else {
            int yearGap = getTimeGap(time.getMillis(), now.getMillis(), Calendar.YEAR);
            if (yearGap == 0) {
                return time.toString("MM-dd");
            } else {
                return time.toString("yyyy-MM-dd");
            }
        }
    }

    private static String getString(@StringRes int id){
        return ImApplication.getApp().getString(id);
    }

    private static String[] getStringArray(@ArrayRes int id){
        return ImApplication.getApp().getResources().getStringArray(id);
    }
}
