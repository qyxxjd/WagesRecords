package com.classic.wages.ui.rules.monthly;

import android.support.annotation.NonNull;
import com.classic.wages.entity.MonthlyInfo;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:39
 */
final class MonthlyUtils {

    static float getWages(@NonNull MonthlyInfo info) {
        return info.getMonthlyWage() + info.getSubsidy() + info.getBonus() - info.getDeductions();
    }
}
