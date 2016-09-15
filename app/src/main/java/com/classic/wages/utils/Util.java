package com.classic.wages.utils;

import java.util.Calendar;

/**
 * @author 续写经典
 * @date 2013/12/14
 */
public class Util {

    /**
     * 根据日期获得星期
     */
    public static int getDayOfWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) { dayOfWeek = 0; }
        return dayOfWeek;
    }
}
