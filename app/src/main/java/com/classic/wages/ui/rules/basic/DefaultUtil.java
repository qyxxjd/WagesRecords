package com.classic.wages.ui.rules.basic;

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

    static float getDayHours(WorkInfo workInfo){
        return Util.ms2hour(workInfo.getEndTime()-workInfo.getStartingTime());
    }

    static float getDayWages(WorkInfo workInfo, float hourlyWage){
        return calculationDayWages(workInfo, hourlyWage)
                .round(Consts.DEFAULT_SCALE)
                .create()
                .floatValue();
    }

    static float getTotalWages(List<WorkInfo> list, float hourlyWage){
        if(DataUtil.isEmpty(list)) return 0f;
        MoneyUtil util = null;
        for (WorkInfo workInfo : list) {
            if(null == util){
                util = calculationDayWages(workInfo, hourlyWage);
            } else {
                util.add(calculationDayWages(workInfo, hourlyWage));
            }
        }
        return util.round(Consts.DEFAULT_SCALE)
                   .create()
                   .floatValue();
    }

    private static MoneyUtil calculationDayWages(WorkInfo workInfo, float hourlyWage){
        return MoneyUtil.newInstance(getDayHours(workInfo))
                        .multiply(hourlyWage)
                        .multiply(workInfo.getMultiple() > 0f ? workInfo.getMultiple() : 1)
                        .add(workInfo.getBonus())
                        .add(workInfo.getSubsidy())
                        .subtract(workInfo.getDeductions());
    }
}
