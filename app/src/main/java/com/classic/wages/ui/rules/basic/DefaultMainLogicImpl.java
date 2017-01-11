package com.classic.wages.ui.rules.basic;

import android.support.annotation.NonNull;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.base.BaseMainLogicImpl;
import com.classic.wages.utils.Util;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-默认规则计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class DefaultMainLogicImpl extends BaseMainLogicImpl<WorkInfo> {

    private final float mHourlyWage;

    public DefaultMainLogicImpl(@NonNull WorkInfoDao dao) {
        super(dao);
        mHourlyWage = Util.getPreferencesFloat(Consts.SP_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
    }

    @Override protected float getTotalWages(List<WorkInfo> list) {
        return DefaultUtil.getTotalWages(list, mHourlyWage).totalWages;
    }
}
