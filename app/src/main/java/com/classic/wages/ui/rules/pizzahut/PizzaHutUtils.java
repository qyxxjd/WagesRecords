package com.classic.wages.ui.rules.pizzahut;

import android.support.annotation.NonNull;
import com.classic.core.utils.DateUtil;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.utils.Util;
import java.text.ParseException;
import java.util.Date;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：TODO
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

    static float getDayWages(
            @NonNull WorkInfo info, float hourlyWage, float restHourlyWage, float nightSubsidy) {
        //总工作时长
        final float dayHours = Util.ms2hour(info.getEndTime() - info.getStartingTime());
        final float nightHours = getNightHours(info);
        float restHours = 0f; //带薪休息时长
        float normalHours = dayHours; //普通时长

        if (dayHours >= 8f) {
            restHours = 0.5f;
            normalHours -= 1f;
        } else if (dayHours >= 6f) {
            restHours = 0.25f;
        } else if (dayHours >= 4f) {
            restHours = 0.25f;
        }

        if (info.getMultiple() > 0f) { //节假日多倍工资，不区分晚班
            // 工作时长 X 时薪 X 倍数 + 奖金 + 补助 - 扣款
            return normalHours * hourlyWage * info.getMultiple() + info.getSubsidy() +
                    info.getBonus() - info.getDeductions();
        }

        float restWage = restHours * restHourlyWage;
        float normalWage = 0f;
        if (nightHours > 0) {
            //晚班补贴需要加上原来每小时的时薪
            float nightWage = nightHours * (hourlyWage + nightSubsidy);
            normalWage = (normalHours - nightHours) * hourlyWage;
            return normalWage + restWage + nightWage + info.getSubsidy() + info.getBonus() -
                    info.getDeductions();
        }
        normalWage = normalHours * hourlyWage;
        return normalWage + restWage + info.getSubsidy() + info.getBonus() - info.getDeductions();
    }

    //晚上10点后的小时数
    private static float getNightHours(WorkInfo info) {
        try {
            long time = info.getEndTime();
            Date date = DateUtil.FORMAT.parse(
                    DateUtil.formatDate(DateUtil.FORMAT_DATE, time) + " 22:00:00");
            if (time > date.getTime()) {
                return Util.ms2hour(time - date.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }
}
