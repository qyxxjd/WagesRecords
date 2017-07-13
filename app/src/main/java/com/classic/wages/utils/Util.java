package com.classic.wages.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.EditText;

import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.widget.CircularDrawable;

import java.util.Calendar;
import java.util.WeakHashMap;

import cn.qy.util.activity.R;
import io.reactivex.functions.Consumer;

/**
 * @author 续写经典
 * @date 2013/12/14
 */
@SuppressWarnings({"deprecation", "StringBufferReplaceableByString"}) public final class Util {
    private static final WeakHashMap<Integer, CircularDrawable> DRAWABLE_MAP = new WeakHashMap<>();

    public static final Consumer<Throwable> ERROR_ACTION = new Consumer<Throwable>() {
        @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                throws Exception {
            if (null != throwable && !TextUtils.isEmpty(throwable.getMessage())) {
                throwable.printStackTrace();
            }
        }
    };

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
        int color;
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
            default:
                color = R.color.week6;
                break;
        }
        return color;
    }

    /**
     * 格式化星期
     *
     * @param week
     * @return
     */
    public static String formatWeek(int week) {
        return Consts.FORMAT_WEEKS[week];
    }

    public static String formatHours(Number number) {
        return new StringBuilder().append(MoneyUtil.replace(number)).append(" H").toString();
    }

    public static String formatTimeBetween(long startTime, long endTime) {
        return new StringBuilder(DateUtil.formatDate(DateUtil.FORMAT_TIME, startTime)).append(" - ")
                                                                                      .append(DateUtil.formatDate(
                                                                                              DateUtil.FORMAT_TIME,
                                                                                              endTime))
                                                                                      .toString();
    }

    public static String formatWageDetail(@NonNull String label, float value) {
        return new StringBuilder().append(label)
                                  .append(Consts.WAGES_DETAIL_SEPARATOR)
                                  .append(MoneyUtil.replace(value))
                                  .toString();
    }

    /**
     * 毫秒转小时
     *
     * @param ms
     * @return
     */
    public static float ms2hour(long ms) {
        return ms2hour(ms, Consts.DEFAULT_SCALE);
    }

    /**
     * 毫秒转小时
     *
     * @param ms
     * @param scale 精度
     * @return
     */
    public static float ms2hour(long ms, int scale) {
        return MoneyUtil.newInstance(ms).divide(Consts.HOUR_2_MS, scale).create().floatValue();
    }

    public static float getNumber(@NonNull EditText editText) {
        final String number = editText.getText().toString();
        return TextUtils.isEmpty(number) ? 0f : Float.valueOf(number);
    }

    public static CircularDrawable getCircularDrawable(int color, float radius) {
        if (!DRAWABLE_MAP.containsKey(color)) {
            CircularDrawable drawable = new CircularDrawable(color, radius);
            DRAWABLE_MAP.put(color, drawable);
            return drawable;
        }
        return DRAWABLE_MAP.get(color);
    }

    public static Drawable getDrawable(@NonNull Context ctx, @DrawableRes int resId) {
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        //    return ctx.getResources().getDrawable(resId);
        //}
        //return ctx.getDrawable(resId);
        return ctx.getResources().getDrawable(resId);
    }

    public static int getColor(@NonNull Context ctx, @ColorRes int colorId) {
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        //    return ctx.getResources().getColor(colorId);
        //}
        //return ctx.getColor(colorId);
        return ctx.getResources().getColor(colorId);
    }

    public static String getString(@NonNull Context ctx, @StringRes int resId, Object... params) {
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        //    return ctx.getResources().getString(resId, params);
        //}
        //return ctx.getString(resId, params);
        return ctx.getResources().getString(resId, params);
    }

    public static String getPreferencesString(String key, String defaultValue) {
        return WagesApplication.getPreferencesUtil().getStringValue(key, defaultValue);
    }

    public static float getPreferencesFloat(String key, String defaultValue) {
        return Float.valueOf(WagesApplication.getPreferencesUtil().getStringValue(key, defaultValue));
    }

    public static int getPreferencesInt(String key, int defaultValue) {
        return WagesApplication.getPreferencesUtil().getIntValue(key, defaultValue);
    }

    public static void putPreferencesString(String key, String value) {
        WagesApplication.getPreferencesUtil().putStringValue(key, value);
    }

    public static void putPreferencesInt(String key, int value) {
        WagesApplication.getPreferencesUtil().putIntValue(key, value);
    }

    /**
     * 设置文本，并将光标移动到文本末尾
     */
    public static void setText(@NonNull EditText editText, @NonNull String text) {
        editText.setText(text);
        editText.setSelection(text.length());
    }

    public static void setText(@NonNull EditText editText, Number number) {
        setText(editText, MoneyUtil.replace(number));
    }

    public static int dp2px(@NonNull Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 文件选择
     *
     * @param activity
     * @param mimeType mime类型
     * @param title 文件选择标题
     * @param fileChooserCode startActivityForResult requestCode
     * @param activityNotFoundHint 未找到系统默认文件选择错误提示
     */
    public static void showFileChooser(@NonNull Fragment activity, String mimeType,
                                       CharSequence title, int fileChooserCode,
                                       String activityNotFoundHint) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, title), fileChooserCode);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtil.showToast(activity.getContext(), activityNotFoundHint);
        }
    }

    /**
     * 打开目录
     *
     * @param activity
     * @param mimeType mime类型
     * @param title 标题
     */
    public static void showDirectory(@NonNull Activity activity, @NonNull String path,
                                     String mimeType, CharSequence title) {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, mimeType);
        try {
            activity.startActivity(Intent.createChooser(intent, title));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }


}
