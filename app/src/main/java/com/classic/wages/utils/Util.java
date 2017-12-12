package com.classic.wages.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.widget.EditText;

import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.widget.CircularDrawable;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.WeakHashMap;

import cn.qy.util.activity.R;
import io.reactivex.disposables.Disposable;
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
            if (null != throwable && !Util.isEmpty(throwable.getMessage())) {
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

    public static float minute2hour(int minute) {
        return MoneyUtil.newInstance(minute).divide(60, Consts.DEFAULT_SCALE).create().floatValue();
    }

    public static float getNumber(@NonNull EditText editText) {
        final String number = editText.getText().toString();
        try {
            return Util.isEmpty(number) ? 0f : Float.valueOf(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public static CircularDrawable getCircularDrawable(int color, float radius) {
        if (!DRAWABLE_MAP.containsKey(color)) {
            CircularDrawable drawable = new CircularDrawable(color, radius);
            DRAWABLE_MAP.put(color, drawable);
            return drawable;
        }
        return DRAWABLE_MAP.get(color);
    }

    public static int getColor(@NonNull Context ctx, @ColorRes int colorId) {
        return ctx.getResources().getColor(colorId);
    }

    public static String getString(@NonNull Context ctx, @StringRes int resId, Object... params) {
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
    public static void setText(@NonNull final EditText editText, @NonNull final String text) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setText(text);
                editText.setSelection(text.length());
            }
        });
    }

    public static void setText(@NonNull EditText editText, Number number) {
        setText(editText, MoneyUtil.replace(number));
    }

    public static void setFocus(@NonNull final EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                if (!Util.isEmpty(editText.getText().toString())) {
                    editText.setSelection(editText.getText().toString().length());
                }
            }
        });
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

    public static void shareText(@NonNull Context context, @NonNull String title, @NonNull String subject,
                                 @NonNull String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, title));
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

    /**
     * 清理Disposable，释放资源
     *
     * @param disposables
     */
    public static void clear(Disposable... disposables) {
        if (null == disposables) {
            return;
        }
        for (Disposable disposable : disposables) {
            if (null != disposable) {
                disposable.dispose();
            }
        }
    }

    public static String getVersionName(@NonNull Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (null != info) {
                return info.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createBackupFileName() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(Consts.BACKUP_PREFIX)
                .append("_")
                .append(sdf.format(new Date(System.currentTimeMillis())))
                .append(Consts.BACKUP_SUFFIX)
                .toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("") || str.trim().length() == 0;
    }

    public static String getNightSubsidyTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return hour + ":" + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * 计算晚班补贴的小时数
     *
     * @param time 下班时间
     * @return 小时
     */
    public static float getNightHours(long time) {
        try {
            String timeString = DateUtil.formatDate(DateUtil.FORMAT_DATE, time) + " " +
                                Util.getPreferencesString(Consts.SP_NIGHT_SUBSIDY_TIME,
                                                          Consts.DEFAULT_NIGHT_SUBSIDY_TIME) + ":00";
            Date date = new SimpleDateFormat(DateUtil.FORMAT_DATE_TIME, Locale.CHINA).parse(timeString);
            if (time > date.getTime()) {
                return Util.ms2hour(time - date.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public static float valueOf(String text) {
        try {
            return Util.isEmpty(text) ? 0 : Float.valueOf(text.trim());
        } catch (Exception e) {
            // 主动上报异常
            CrashReport.postCatchedException(e);
            return 0F;
        }
    }
}
