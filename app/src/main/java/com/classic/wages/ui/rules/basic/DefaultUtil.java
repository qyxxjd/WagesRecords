package com.classic.wages.ui.rules.basic;

import android.support.annotation.NonNull;
import com.classic.core.utils.DataUtil;
import com.classic.core.utils.DateUtil;
import com.classic.core.utils.MoneyUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.utils.Util;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:22
 */
final class DefaultUtil {

    static String formatTimeBetween(long startTime, long endTime){
        return new StringBuilder(DateUtil.formatDate(DateUtil.FORMAT_TIME, startTime))
                .append(" - ")
                .append(DateUtil.formatDate(DateUtil.FORMAT_TIME, endTime))
                .toString();
    }

    static float getDayHours(@NonNull WorkInfo workInfo){
        return Util.ms2hour(workInfo.getEndTime()-workInfo.getStartingTime());
    }

    static float getDayWages(@NonNull WorkInfo workInfo, float hourlyWage){
        return MoneyUtil.newInstance(calculationDayWages(workInfo, hourlyWage))
                        .round(Consts.DEFAULT_SCALE)
                        .create()
                        .floatValue();
    }

    static float getTotalWages(List<WorkInfo> list, float hourlyWage){
        if(DataUtil.isEmpty(list)) return 0f;
        float totalWages = 0f;
        for (WorkInfo workInfo : list) {
            totalWages += calculationDayWages(workInfo, hourlyWage);
        }
        return MoneyUtil.newInstance(totalWages).round(Consts.DEFAULT_SCALE).create().floatValue();
    }

    private static float calculationDayWages(@NonNull WorkInfo workInfo, float hourlyWage){
        return getDayHours(workInfo) * hourlyWage * (workInfo.getMultiple() > 0f ? workInfo.getMultiple() : 1)
               + workInfo.getBonus() + workInfo.getSubsidy() - workInfo.getDeductions();
    }
}
