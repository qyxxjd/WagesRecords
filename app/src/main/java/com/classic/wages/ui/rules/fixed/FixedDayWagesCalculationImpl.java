package com.classic.wages.ui.rules.fixed;

import android.support.annotation.NonNull;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseWagesCalculationImpl;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-按天加班工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class FixedDayWagesCalculationImpl extends BaseWagesCalculationImpl<WorkInfo> {

    private final float mHourlyWage;
    private final float mFixedHours;
    private final float mOvertimeHourlyWage;

    public FixedDayWagesCalculationImpl(@NonNull WorkInfoDao dao,
                                        @NonNull SharedPreferencesUtil spUtil) {
        super(dao);
        mHourlyWage = FixedUtils.getPreferencesValue(spUtil,
                Consts.SP_FIXED_DAY_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        mFixedHours = FixedUtils.getPreferencesValue(spUtil,
                Consts.SP_FIXED_DAY_HOURS, Consts.DEFAULT_DAY_FIXED_HOURS);
        mOvertimeHourlyWage = FixedUtils.getPreferencesValue(spUtil,
                Consts.SP_FIXED_DAY_OVERTIME_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
    }

    @Override protected float getWages(@NonNull WorkInfo info) {
        return FixedUtils.getDayWagesByFixedDay(info, mHourlyWage, mFixedHours, mOvertimeHourlyWage);
    }

}
