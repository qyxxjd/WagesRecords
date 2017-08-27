package com.classic.wages.ui.rules.kfc;

import android.support.annotation.NonNull;

import com.classic.wages.consts.Consts;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseWagesDetailLogicImpl;
import com.classic.wages.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：肯德基兼职工资详情实现类
 * 创 建 人：续写经典
 * 创建时间：16/11/4 下午6:19
 */
public class KFCWagesDetailLogicImpl extends BaseWagesDetailLogicImpl<WorkInfo> {

    @Override protected List<String> convert(List<WorkInfo> list) {
        final float hourlyWage = Util.getPreferencesFloat(
                Consts.SP_PIZZA_HUT_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        final float nightSubsidy = Util.getPreferencesFloat(
                Consts.SP_NIGHT_SUBSIDY, Consts.DEFAULT_NIGHT_SUBSIDY);
        return toList(KFCUtil.getTotalWages(list, hourlyWage, nightSubsidy));
    }

    private List<String> toList(@NonNull KFCWagesDetailEntity entity){
        List<String> items = new ArrayList<>();
        items.add(Util.formatWageDetail("工作时长", entity.totalNormalHours));
        items.add(Util.formatWageDetail("基本工资", entity.totalNormalWages));
        items.add(Util.formatWageDetail("晚班时长", entity.totalNightHours));
        items.add(Util.formatWageDetail("晚班补贴", entity.totalNightWages));
        items.add(Util.formatWageDetail("节假日时长", entity.totalHolidayHours));
        items.add(Util.formatWageDetail("节假日工资", entity.totalHolidayWages));
        items.add(Util.formatWageDetail("奖金", entity.totalBonus));
        items.add(Util.formatWageDetail("补贴", entity.totalSubsidy));
        items.add(Util.formatWageDetail("扣款", entity.totalDeductions));
        items.add(Util.formatWageDetail("总工资", entity.totalWages));
        return items;
    }
}
