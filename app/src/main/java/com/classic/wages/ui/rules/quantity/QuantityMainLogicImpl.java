package com.classic.wages.ui.rules.quantity;

import android.support.annotation.NonNull;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.rules.base.BaseMainLogicImpl;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-计件工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class QuantityMainLogicImpl extends BaseMainLogicImpl<QuantityInfo> {

    public QuantityMainLogicImpl(@NonNull QuantityInfoDao dao) {
        super(dao);
    }

    @Override protected float getTotalWages(List<QuantityInfo> list) {
        return QuantityUtils.getTotalWages(list).totalWages;
    }
}
