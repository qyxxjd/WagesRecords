package com.classic.wages.ui.rules.fixed;

import android.support.annotation.NonNull;
import com.classic.core.utils.DataUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.entity.WorkInfo;
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


    static float getPreferencesValue(@NonNull SharedPreferencesUtil spUtil,
                                     String key, String defultValue){
        return Float.valueOf(spUtil.getStringValue(key, defultValue));
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

    static float getMonthWages(List<WorkInfo> list, float hourlyWage, float fixedHours,
                               float overtimeHourlyWage) {
        if(DataUtil.isEmpty(list)) return 0f;
        float monthTotalHours = 0f; //月工作总时长
        float monthOtherWage = 0f; //其它工资(奖金、补助、扣款)
        float monthOvertimeWage = 0f; //加班工资
        float monthHolidaysWage = 0f; //节假日工资
        float monthNormalWage = 0f; //正常工资

        float dayHours = 0f;
        float itemOtherWage = 0f;
        for (WorkInfo item : list) {
            dayHours = Util.ms2hour(item.getEndTime() - item.getStartingTime());
            itemOtherWage = item.getSubsidy() + item.getBonus() - item.getDeductions();
            if(item.getMultiple() > 0f){
                monthHolidaysWage += (dayHours * hourlyWage * item.getMultiple() + itemOtherWage);
            } else {
                monthTotalHours += dayHours;
                monthOtherWage += itemOtherWage;
            }
        }
        if(monthTotalHours > fixedHours){ //有加班的情况
            monthNormalWage = fixedHours * hourlyWage;
            monthOvertimeWage = (monthTotalHours - fixedHours) * overtimeHourlyWage;
            return monthNormalWage + monthOvertimeWage + monthHolidaysWage + monthOtherWage;
        }
        //无加班的情况
        monthNormalWage = monthTotalHours * hourlyWage;
        return monthNormalWage + monthHolidaysWage + monthOtherWage;
    }

    static float getTotalWages(HashMap<String, List<WorkInfo>> map,
                               float hourlyWage, float fixedHours, float overtimeHourlyWage){
        if(DataUtil.isEmpty(map)) return 0f;
        float totalWages = 0f;
        for(String key : map.keySet()){
            List<WorkInfo> list = map.get(key);
            totalWages += getMonthWages(list, hourlyWage, fixedHours, overtimeHourlyWage);
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
