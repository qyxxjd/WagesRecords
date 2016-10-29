package com.classic.wages.ui.rules.basic;

import android.support.annotation.NonNull;
import com.classic.core.utils.MoneyUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.utils.Util;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:22
 */
final class DefaultUtil {

    static float getDayHours(@NonNull WorkInfo workInfo){
        return Util.ms2hour(workInfo.getEndTime()-workInfo.getStartingTime());
    }

    static float getDayWages(@NonNull WorkInfo workInfo, float hourlyWage){
        return MoneyUtil.newInstance(calculationDayWages(workInfo, hourlyWage))
                        .round(Consts.DEFAULT_SCALE)
                        .create()
                        .floatValue();
    }

    private static float calculationDayWages(@NonNull WorkInfo workInfo, float hourlyWage){
        return getDayHours(workInfo) * hourlyWage * (workInfo.getMultiple() > 0f ? workInfo.getMultiple() : 1)
               + workInfo.getBonus() + workInfo.getSubsidy() - workInfo.getDeductions();
    }
}
