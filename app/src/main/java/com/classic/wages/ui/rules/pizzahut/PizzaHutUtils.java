package com.classic.wages.ui.rules.pizzahut;

import android.support.annotation.NonNull;

import com.classic.wages.entity.WorkInfo;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：必胜客工资计算工具类
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:39
 */
final class PizzaHutUtils {

    //必胜客兼职： 
    //时间计算 
    //大于8小时 : 休息1小时,带薪0.5小时 
    //大于6小时 : 休息0小时,带薪0.5小时 
    //大于4小时 : 休息0小时,带薪0.25小时 
    //小于4小时 : 休息0小时,带薪0小时 
    //晚上22点后享受晚班津贴 

    static PizzaHutWagesDetailEntity getDayWages(
            @NonNull WorkInfo info, float hourlyWage, float restHourlyWage, float nightSubsidy) {
        PizzaHutWagesDetailEntity entity = new PizzaHutWagesDetailEntity();
        //总工作时长
        final float dayHours = Util.ms2hour(info.getEndTime() - info.getStartingTime());
        entity.totalNightHours = getNightHours(info);
        entity.totalNormalHours = dayHours;

        if (dayHours >= 8f) {
            entity.totalRestHours = 0.5f;
            entity.totalNormalHours -= 1f;
        } else if (dayHours >= 6f) {
            entity.totalRestHours = 0.25f;
        } else if (dayHours >= 4f) {
            entity.totalRestHours = 0.25f;
        }
        entity.totalBonus = info.getBonus();
        entity.totalDeductions = info.getDeductions();
        entity.totalSubsidy = info.getSubsidy();
        if (info.getMultiple() > 0f) { //节假日多倍工资，不区分晚班
            //节假日,按倍数算工资。其它工资忽略
            entity.totalHolidayHours = entity.totalNormalHours + entity.totalRestHours;
            entity.totalHolidayWages = entity.totalHolidayHours * hourlyWage * info.getMultiple();
            //节假日工资 + 奖金 + 补助 - 扣款
            entity.totalWages = entity.totalHolidayWages +
                    entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
            return entity;
        }
        entity.totalRestWages = entity.totalRestHours * restHourlyWage;
        if(entity.totalNightHours > 0f){
            entity.totalNightWages = entity.totalNightHours * nightSubsidy;
        }
        entity.totalNormalWages = entity.totalNormalHours * hourlyWage;
        //正常工资 + 带薪休息工资 + 晚班补贴 + 奖金 + 补助 - 扣款
        entity.totalWages = entity.totalNormalWages + entity.totalRestWages + entity.totalNightWages
                + entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
        return entity;
    }

    //晚上10点后的小时数
    private static float getNightHours(WorkInfo info) {
        try {
            long time = info.getEndTime();
            Date date = new SimpleDateFormat(DateUtil.FORMAT_DATE_TIME, Locale.CHINA).parse(
                    DateUtil.formatDate(DateUtil.FORMAT_DATE, time) + " 22:00:00");
            if (time > date.getTime()) {
                return Util.ms2hour(time - date.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    static PizzaHutWagesDetailEntity getTotalWages(List<WorkInfo> list, float hourlyWage,
                                                   float restHourlyWage, float nightSubsidy) {
        PizzaHutWagesDetailEntity entity = new PizzaHutWagesDetailEntity();
        if(DataUtil.isEmpty(list)) return entity;
        for (WorkInfo item : list) {
            PizzaHutWagesDetailEntity subItem = getDayWages(item, hourlyWage, restHourlyWage,
                    nightSubsidy);
            entity.totalNormalHours += subItem.totalNormalHours;
            entity.totalNormalWages += subItem.totalNormalWages;
            entity.totalHolidayHours += subItem.totalHolidayHours;
            entity.totalHolidayWages += subItem.totalHolidayWages;
            entity.totalRestHours += subItem.totalRestHours;
            entity.totalRestWages += subItem.totalRestWages;
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
