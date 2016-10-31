package com.classic.wages.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.EditText;
import cn.qy.util.activity.R;
import com.classic.core.utils.DateUtil;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.widget.CircularDrawable;
import java.util.Calendar;
import java.util.WeakHashMap;

/**
 * @author 续写经典
 * @date 2013/12/14
 */
@SuppressWarnings({ "deprecation", "StringBufferReplaceableByString" })
public final class Util {
    private static final WeakHashMap<Integer, CircularDrawable> DRAWABLE_MAP = new WeakHashMap<>();

    /**
     * 根据日期获得星期
     */
    public static int getDayOfWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) {
            dayOfWeek = 0;
        }
        return dayOfWeek;
    }

    /**
     * 根据日期获取颜色
     */
    public static int getColorByWeek(int week) {
        int color = 0;
        switch (week) {
            case 0:
                color = R.color.week0;
                break;
            case 1:
                color = R.color.week1;
                break;
            case 2:
                color = R.color.week2;
                break;
            case 3:
                color = R.color.week3;
                break;
            case 4:
                color = R.color.week4;
                break;
            case 5:
                color = R.color.week5;
                break;
            case 6:
                color = R.color.week6;
                break;
        }
        return color;
    }

    /**
     * 格式化星期
     * @param week
     * @return
     */
    public static String formatWeek(int week){
        return Consts.FORMAT_WEEKS[week];
    }

    public static String formatWages(Number number){
        return new StringBuilder("￥").append(MoneyUtil.replace(number)).toString();
    }
    public static String formatHours(Number number){
        return new StringBuilder().append(MoneyUtil.replace(number)).append(" H").toString();
    }

    public static String formatTimeBetween(long startTime, long endTime){
        return new StringBuilder(DateUtil.formatDate(DateUtil.FORMAT_TIME, startTime))
                .append(" - ")
                .append(DateUtil.formatDate(DateUtil.FORMAT_TIME, endTime))
                .toString();
    }

    /**
     * 毫秒转小时
     * @param ms
     * @return
     */
    public static float ms2hour(long ms){
        return ms2hour(ms, Consts.DEFAULT_SCALE);
    }
    /**
     * 毫秒转小时
     * @param ms
     * @param scale 精度
     * @return
     */
    public static float ms2hour(long ms, int scale){
        return MoneyUtil.newInstance(ms).divide(Consts.HOUR_2_MS, scale).create().floatValue();
    }

    public static float getNumber(@NonNull EditText editText) {
        final String number = editText.getText().toString();
        return TextUtils.isEmpty(number) ? 0f : Float.valueOf(number);
    }

    public static CircularDrawable getCircularDrawable(int color, float radius){
        if(!DRAWABLE_MAP.containsKey(color)){
            CircularDrawable drawable = new CircularDrawable(color, radius);
            DRAWABLE_MAP.put(color, drawable);
            return drawable;
        }
        return DRAWABLE_MAP.get(color);
    }

    public static Drawable getDrawable(@NonNull Context ctx, @DrawableRes int resId){
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        //    return ctx.getResources().getDrawable(resId);
        //}
        //return ctx.getDrawable(resId);
        return ctx.getResources().getDrawable(resId);
    }
    public static int getColor(@NonNull Context ctx, @ColorRes int colorId){
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        //    return ctx.getResources().getColor(colorId);
        //}
        //return ctx.getColor(colorId);
        return ctx.getResources().getColor(colorId);
    }
    public static String getString(@NonNull Context ctx, @StringRes int resId, Object... params){
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        //    return ctx.getResources().getString(resId, params);
        //}
        //return ctx.getString(resId, params);
        return ctx.getResources().getString(resId, params);
    }
    public static float getPreferencesValue(@NonNull SharedPreferencesUtil spUtil,
                                     String key, String defultValue){
        return Float.valueOf(spUtil.getStringValue(key, defultValue));
    }
}
