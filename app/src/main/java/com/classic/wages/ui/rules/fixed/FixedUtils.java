package com.classic.wages.ui.rules.fixed;

import android.support.annotation.NonNull;
import com.classic.core.utils.DataUtil;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseWagesDetailEntity;
import com.classic.wages.utils.Util;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:39
 */
final class FixedUtils {

    static float getDayWagesByFixedDay(@NonNull WorkInfo info, float hourlyWage, float fixedHours,
                                       float overtimeHourlyWage) {
        float dayHours = Util.ms2hour(info.getEndTime() - info.getStartingTime());
        if(info.getMultiple() > 0f){ //节假日多倍工资，不区分加班
            // 工作时长 X 时薪 X 倍数 + 奖金 + 补助 - 扣款
            return dayHours * hourlyWage * info.getMultiple() + info.getSubsidy() + info.getBonus()
                    - info.getDeductions();
        }
        float normalWage = 0f;
        float overtimeWage = 0f;
        if(dayHours > fixedHours){ //有加班的情况
            normalWage = fixedHours * hourlyWage + info.getSubsidy() + info.getBonus()
                    - info.getDeductions();
            overtimeWage = (dayHours - fixedHours) * overtimeHourlyWage;
            return normalWage + overtimeWage;
        }
        //无加班的情况
        normalWage = dayHours * hourlyWage + info.getSubsidy() + info.getBonus()
                - info.getDeductions();
        return normalWage;
    }
    static BaseWagesDetailEntity getTotalWagesByFixedDay(List<WorkInfo> list, float hourlyWage,
                                                         float fixedHours,
                                                         float overtimeHourlyWage) {
        final BaseWagesDetailEntity entity = new BaseWagesDetailEntity();
        if(DataUtil.isEmpty(list)){ return entity; }
        for (WorkInfo item : list) {
            final float hours = Util.ms2hour(item.getEndTime() - item.getStartingTime());
            if(item.getMultiple() > 0f){
                entity.totalHolidayHours += hours;
                //每条数据的节假日倍数可能不同，总工资只能在这里算
                entity.totalHolidayWages += hours * hourlyWage * item.getMultiple();
            } else if(hours > fixedHours){
                entity.totalOvertimeHours += (hours - fixedHours);
                entity.totalNormalHours += fixedHours;
            } else {
                entity.totalNormalHours += hours;
            }
            entity.totalBonus += item.getBonus();
            entity.totalDeductions += item.getDeductions();
            entity.totalSubsidy += item.getSubsidy();
        }
        entity.totalNormalWages = entity.totalNormalHours * hourlyWage;
        entity.totalOvertimeWages = entity.totalOvertimeHours * overtimeHourlyWage;
        //正常工资 + 节假日工资 + 加班工资 + 奖金 + 补助 - 扣款
        entity.totalWages = entity.totalNormalWages + entity.totalHolidayWages +
                entity.totalOvertimeWages +
                entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
        return entity;
    }

    static float getDayWagesByFixedMonth(@NonNull WorkInfo info, float hourlyWage) {
        float dayHours = Util.ms2hour(info.getEndTime() - info.getStartingTime());
        if(info.getMultiple() > 0f){ //节假日多倍工资，不区分加班
            // 工作时长 X 时薪 X 倍数 + 奖金 + 补助 - 扣款
            return dayHours * hourlyWage * info.getMultiple() + info.getSubsidy() + info.getBonus()
                    - info.getDeductions();
        }
        return dayHours * hourlyWage + info.getSubsidy() + info.getBonus()
                - info.getDeductions();
    }

    static BaseWagesDetailEntity getMonthWages(List<WorkInfo> list, float hourlyWage,
                                               float fixedHours, float overtimeHourlyWage) {
        final BaseWagesDetailEntity entity = new BaseWagesDetailEntity();
        if(DataUtil.isEmpty(list)){ return entity; }
        for (WorkInfo item : list) {
            final float hours = Util.ms2hour(item.getEndTime() - item.getStartingTime());
            if(item.getMultiple() > 0f){
                entity.totalHolidayHours += hours;
                //每条数据的节假日倍数可能不同，总工资只能在这里算
                entity.totalHolidayWages += hours * hourlyWage * item.getMultiple();
            } else {
                entity.totalNormalHours += hours;
            }
            entity.totalBonus += item.getBonus();
            entity.totalDeductions += item.getDeductions();
            entity.totalSubsidy += item.getSubsidy();
        }
        if(entity.totalNormalHours > fixedHours){
            entity.totalOvertimeHours = entity.totalNormalHours - fixedHours;
            entity.totalNormalHours = fixedHours;
        }
        entity.totalNormalWages = entity.totalNormalHours * hourlyWage;
        entity.totalOvertimeWages = entity.totalOvertimeHours * overtimeHourlyWage;
        //正常工资 + 节假日工资 + 加班工资 + 奖金 + 补助 - 扣款
        entity.totalWages = entity.totalNormalWages + entity.totalHolidayWages +
                entity.totalOvertimeWages +
                entity.totalBonus + entity.totalSubsidy - entity.totalDeductions;
        return entity;
    }

    static float getTotalWages(HashMap<String, List<WorkInfo>> map,
                               float hourlyWage, float fixedHours, float overtimeHourlyWage){
        if(DataUtil.isEmpty(map)) return 0f;
        float totalWages = 0f;
        for(String key : map.keySet()){
            List<WorkInfo> list = map.get(key);
            totalWages += getMonthWages(list, hourlyWage, fixedHours, overtimeHourlyWage).totalWages;
        }
        return totalWages;
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMM", Locale.CHINA);
    static HashMap<String, List<WorkInfo>> convert(List<WorkInfo> list){
        if(DataUtil.isEmpty(list)) return null;
        HashMap<String, List<WorkInfo>> map = new HashMap<>();
        for (WorkInfo item : list) {
            final String date = SDF.format(new Date(item.getStartingTime()));
            if(map.containsKey(date)){
                map.get(date).add(item);
            } else {
                List<WorkInfo> dateList = new ArrayList<>();
                dateList.add(item);
                map.put(date, dateList);
            }
        }
        return map;
    }


}
