package com.classic.wages.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 应用名称: BaseProject
 * 包 名 称: com.classic.android.utils
 *
 * 文件描述: 日期操作工具类
 * 创 建 人: 续写经典
 * 创建时间: 2015/11/4 17:26
 */
public final class DateUtil {

    private DateUtil() { }

    public static final String FORMAT_DATE      = "yyyy-MM-dd";
    public static final String FORMAT_TIME      = "HH:mm";
    //public static final String FORMAT_TIME      = "HH:mm:ss";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static int getYear() {
        return getTimeByType(Calendar.YEAR);
    }

    public static int getMonth() {
        return getTimeByType(Calendar.MONTH);
    }

    private static int getTimeByType(int type) {
        return Calendar.getInstance().get(type);
    }

    /**
     * 时间格式化
     */
    public static String formatDate(String format, Long time) {
        return formatDate(new SimpleDateFormat(format, Locale.CHINA), time);
    }

    /**
     * 时间格式化
     */
    public static String formatDate(SimpleDateFormat format, Long time) {
        if (null == time || time <= 0) { return ""; }
        return format.format(new Date(String.valueOf(time).length() == 13 ? time : time * 1000));
    }

    /**
     * 根据日期获得星期
     */
    public static String getWeek(Date d) {
        final String[] dayNames = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) { dayOfWeek = 0; }
        return (dayNames[dayOfWeek]);
    }

    /**
     * 获取N天前、N天后的 日期
     *
     * @param nowDate   当前日期;
     * @param dayAddNum 正数：N天前，负数：N天后;
     */
    public static Date getAddDay(Date nowDate, int dayAddNum) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, dayAddNum);
        return calendar.getTime();
    }

    /**
     * 获取N天前、N天后的 日期
     *
     * @param nowDate   当前时间戳;
     * @param dayAddNum 正数：N天前，负数：N天后;
     */
    public static Date getAddDay(long nowDate, int dayAddNum) {
        return getAddDay(new Date(nowDate), dayAddNum);
    }
}
