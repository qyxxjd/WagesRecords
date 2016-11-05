package com.classic.wages.ui.rules.fixed;

import android.support.annotation.NonNull;
import com.classic.wages.consts.Consts;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseWagesDetailLogicImpl;
import com.classic.wages.ui.rules.base.BaseWagesDetailEntity;
import com.classic.wages.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/11/4 下午6:19
 */
public class FixedDayWagesDetailLogicImpl extends BaseWagesDetailLogicImpl<WorkInfo> {

    @Override protected List<String> convert(List<WorkInfo> list) {
        final float hourlyWage = Util.getPreferencesFloat(
                Consts.SP_FIXED_DAY_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        final float fixedHours = Util.getPreferencesFloat(
                Consts.SP_FIXED_DAY_FIXED_HOURS, Consts.DEFAULT_DAY_FIXED_HOURS);
        final float overtimeHourlyWage = Util.getPreferencesFloat(
                Consts.SP_FIXED_DAY_OVERTIME_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        return toList(FixedUtils.getTotalWagesByFixedDay(list, hourlyWage, fixedHours, overtimeHourlyWage));
    }

    private List<String> toList(@NonNull BaseWagesDetailEntity entity){
        List<String> items = new ArrayList<>();
        items.add(Util.formatWageDetail("工作时长", entity.totalNormalHours));
        items.add(Util.formatWageDetail("基本工资", entity.totalNormalWages));
        items.add(Util.formatWageDetail("加班时长", entity.totalOvertimeHours));
        items.add(Util.formatWageDetail("加班工资", entity.totalOvertimeWages));
        items.add(Util.formatWageDetail("节假日时长", entity.totalHolidayHours));
        items.add(Util.formatWageDetail("节假日工资", entity.totalHolidayWages));
        items.add(Util.formatWageDetail("奖金", entity.totalBonus));
        items.add(Util.formatWageDetail("补贴", entity.totalSubsidy));
        items.add(Util.formatWageDetail("扣款", entity.totalDeductions));
        items.add(Util.formatWageDetail("总工资", entity.totalWages));
        return items;
    }
}
