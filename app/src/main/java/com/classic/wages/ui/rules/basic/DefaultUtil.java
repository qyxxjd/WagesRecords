package com.classic.wages.ui.rules.basic;

import android.support.annotation.NonNull;

import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseWagesDetailEntity;
import com.classic.wages.utils.DataUtil;
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

    static float getDayHours(@NonNull WorkInfo workInfo){
        return Util.ms2hour(workInfo.getEndTime()-workInfo.getStartingTime());
    }

    static float getDayWages(@NonNull WorkInfo workInfo, float hourlyWage){
        return Util.round(calculationDayWages(workInfo, hourlyWage));
    }

    private static float calculationDayWages(@NonNull WorkInfo workInfo, float hourlyWage){
        return getDayHours(workInfo) * hourlyWage * (workInfo.getMultiple() > 0f ? workInfo.getMultiple() : 1)
               + workInfo.getBonus() + workInfo.getSubsidy() - workInfo.getDeductions();
    }

    static BaseWagesDetailEntity calculationTotalWages(List<WorkInfo> list, float hourlyWage){
        final BaseWagesDetailEntity entity = new BaseWagesDetailEntity();
        if(!DataUtil.isEmpty(list)){
            long holidayTime = 0L;
            long normalTime = 0L;
            for (WorkInfo item : list) {
                final long time = item.getEndTime() - item.getStartingTime();
                if(item.getMultiple() > 0f){
                    holidayTime += time;
                    //每条数据的节假日倍数可能不同，总工资只能在这里算
                    entity.totalHolidayWages +=
                            Util.ms2hour(time) * hourlyWage * item.getMultiple();
                } else {
                    normalTime += time;
                }
                entity.totalBonus += item.getBonus();
                entity.totalDeductions += item.getDeductions();
                entity.totalSubsidy += item.getSubsidy();
            }
            entity.totalNormalHours = Util.ms2hour(normalTime);
            entity.totalNormalWages = entity.totalNormalHours * hourlyWage;
            //节假日总时长，不区分倍数
            entity.totalHolidayHours = Util.ms2hour(holidayTime);
            //正常工资 + 节假日工资 + 奖金 + 补助 - 扣款
            entity.totalWages = entity.totalNormalWages + entity.totalHolidayWages +
                    entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
        }
        return entity;
    }

}
