package com.classic.wages.ui.rules.kfc;

import android.support.annotation.NonNull;

import com.classic.wages.entity.WorkInfo;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.Util;

import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：肯德基工资计算工具类
 * 创 建 人：续写经典
 * 创建时间：17/08/27 下午1:22
 */
final class KFCUtil {

    static float getDayHours(@NonNull WorkInfo info) {
        long time = info.getEndTime() - info.getStartingTime() - (info.getRestTime() * 60000);
        return time < 0 ? 0 : Util.ms2hour(time);
    }

    static KFCWagesDetailEntity getDayWages(@NonNull WorkInfo info, float hourlyWage, float nightSubsidy) {
        KFCWagesDetailEntity entity = new KFCWagesDetailEntity();
        final float dayHours = getDayHours(info);
        entity.totalBonus = info.getBonus();
        entity.totalDeductions = info.getDeductions();
        entity.totalSubsidy = info.getSubsidy();
        if (info.getMultiple() > 0f) { //节假日多倍工资，不区分晚班
            //节假日,按倍数算工资。其它工资忽略
            entity.totalHolidayHours = dayHours;
            entity.totalHolidayWages = entity.totalHolidayHours * hourlyWage * info.getMultiple();
            //节假日工资 + 奖金 + 补助 - 扣款
            entity.totalWages = entity.totalHolidayWages +
                                entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
            return entity;
        }

        entity.totalNightHours = Util.getNightHours(info.getEndTime());
        entity.totalNormalHours = dayHours;

        if(entity.totalNightHours > 0f) {
            entity.totalNightWages = entity.totalNightHours * nightSubsidy;
        }
        entity.totalNormalWages = entity.totalNormalHours * hourlyWage;
        //正常工资 + 带薪休息工资 + 晚班补贴 + 奖金 + 补助 - 扣款
        entity.totalWages = entity.totalNormalWages  + entity.totalNightWages
                            + entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
        return entity;
    }

    static KFCWagesDetailEntity getTotalWages(List<WorkInfo> list, float hourlyWage, float nightSubsidy) {
        KFCWagesDetailEntity entity = new KFCWagesDetailEntity();
        if(DataUtil.isEmpty(list)) return entity;
        for (WorkInfo item : list) {
            KFCWagesDetailEntity subItem = getDayWages(item, hourlyWage, nightSubsidy);
            entity.totalNormalHours += subItem.totalNormalHours;
            entity.totalNormalWages += subItem.totalNormalWages;
            entity.totalHolidayHours += subItem.totalHolidayHours;
            entity.totalHolidayWages += subItem.totalHolidayWages;
            entity.totalNightHours += subItem.totalNightHours;
            entity.totalNightWages += subItem.totalNightWages;
            entity.totalSubsidy += subItem.totalSubsidy;
            entity.totalBonus += subItem.totalBonus;
            entity.totalDeductions += subItem.totalDeductions;
            entity.totalWages += subItem.totalWages;
        }
        return entity;
    }
}
