package com.classic.wages.ui.rules.fixed;

import android.support.annotation.NonNull;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseMainLogicImpl;
import com.classic.wages.ui.rules.base.BaseWagesDetailEntity;
import com.classic.wages.utils.Util;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-按天加班工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class FixedDayMainLogicImpl extends BaseMainLogicImpl<WorkInfo> {

    private final float mHourlyWage;
    private final float mFixedHours;
    private final float mOvertimeHourlyWage;

    public FixedDayMainLogicImpl(@NonNull WorkInfoDao dao) {
        super(dao);
        mHourlyWage = Util.getPreferencesFloat(
                Consts.SP_FIXED_DAY_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        mFixedHours = Util.getPreferencesFloat(
                Consts.SP_FIXED_DAY_FIXED_HOURS, Consts.DEFAULT_DAY_FIXED_HOURS);
        mOvertimeHourlyWage = Util.getPreferencesFloat(
                Consts.SP_FIXED_DAY_OVERTIME_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
    }

    @Override protected float getTotalWages(List<WorkInfo> list) {
        BaseWagesDetailEntity entity = FixedUtils.getTotalWagesByFixedDay(list, mHourlyWage,
                mFixedHours, mOvertimeHourlyWage);
        return entity.totalWages;
    }
}
