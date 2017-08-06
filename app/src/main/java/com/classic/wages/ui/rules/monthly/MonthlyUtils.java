package com.classic.wages.ui.rules.monthly;

import android.support.annotation.NonNull;
import com.classic.wages.entity.MonthlyInfo;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：月工资计算工具类
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:39
 */
final class MonthlyUtils {

    static float getWages(@NonNull MonthlyInfo info) {
        return info.getMonthlyWage() + info.getSubsidy() + info.getBonus() - info.getDeductions();
    }

    static float getTotalWages(List<MonthlyInfo> list) {
        float totalWages = 0f;
        for (MonthlyInfo item : list) {
            totalWages += getWages(item);
        }
        return totalWages;
    }
}
