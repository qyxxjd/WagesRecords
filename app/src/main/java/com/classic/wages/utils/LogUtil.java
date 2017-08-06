package com.classic.wages.utils;

import android.text.TextUtils;

import com.elvishew.xlog.XLog;

import cn.qy.util.activity.BuildConfig;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.utils
 *
 * 文件描述: 日志打印
 * 创 建 人: 续写经典
 * 创建时间: 2017/2/24 8:45
 */
public final class LogUtil {
    private LogUtil() {
        // no instance
    }

    public static void d(String content) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(content)) {
            XLog.d(content);
        }
    }

    public static void e(String content) {
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(content)) {
            XLog.e(content);
        }
    }
}
