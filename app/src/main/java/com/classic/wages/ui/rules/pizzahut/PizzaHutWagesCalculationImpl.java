package com.classic.wages.ui.rules.pizzahut;

import android.support.annotation.NonNull;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseWagesCalculationImpl;
import com.classic.wages.utils.Util;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-必胜客兼职工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class PizzaHutWagesCalculationImpl extends BaseWagesCalculationImpl<WorkInfo> {

    private final float mHourlyWage;
    private final float mRestHourlyWage;
    private final float mNightSubsidy;

    public PizzaHutWagesCalculationImpl(@NonNull WorkInfoDao dao,
                                        @NonNull SharedPreferencesUtil spUtil) {
        super(dao);
        mHourlyWage = Util.getPreferencesValue(spUtil,
                Consts.SP_PIZZA_HUT_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        mRestHourlyWage = Util.getPreferencesValue(spUtil,
                Consts.SP_PIZZA_HUT_REST_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        mNightSubsidy = Util.getPreferencesValue(spUtil,
                Consts.SP_PIZZA_HUT_NIGHT_SUBSIDY, Consts.DEFAULT_NIGHT_SUBSIDY);
    }

    @Override protected float getWages(@NonNull WorkInfo info) {
        return PizzaHutUtils.getDayWages(info, mHourlyWage, mRestHourlyWage, mNightSubsidy);
    }
}
