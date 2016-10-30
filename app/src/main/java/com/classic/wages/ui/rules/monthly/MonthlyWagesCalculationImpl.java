package com.classic.wages.ui.rules.monthly;

import android.support.annotation.NonNull;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.entity.MonthlyInfo;
import com.classic.wages.ui.rules.base.BaseWagesCalculationImpl;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-月工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class MonthlyWagesCalculationImpl extends BaseWagesCalculationImpl<MonthlyInfo> {

    public MonthlyWagesCalculationImpl(@NonNull MonthlyInfoDao dao) {
        super(dao);
    }

    @Override protected float getWages(@NonNull MonthlyInfo info) {
        return MonthlyUtils.getWages(info);
    }
}
