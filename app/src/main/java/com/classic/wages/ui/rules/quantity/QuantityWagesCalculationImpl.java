package com.classic.wages.ui.rules.quantity;

import android.support.annotation.NonNull;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.rules.base.BaseWagesCalculationImpl;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-计件工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class QuantityWagesCalculationImpl extends BaseWagesCalculationImpl<QuantityInfo> {

    public QuantityWagesCalculationImpl(@NonNull QuantityInfoDao dao) {
        super(dao);
    }

    @Override protected float getWages(@NonNull QuantityInfo info) {
        return QuantityUtils.getWages(info);
    }
}
