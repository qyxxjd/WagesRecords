package com.classic.wages.ui.rules.quantity;

import android.support.annotation.NonNull;

import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.utils.DataUtil;

import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:39
 */
final class QuantityUtils {

    static float getWages(@NonNull QuantityInfo info) {
        return info.getUnitPrice() * info.getQuantity() + info.getSubsidy() + info.getBonus() -
                info.getDeductions();
    }

    static QuantityWagesDetailEntity calculationTotalWages(List<QuantityInfo> list) {
        QuantityWagesDetailEntity entity = new QuantityWagesDetailEntity();
        if(DataUtil.isEmpty(list)) return entity;
        for (QuantityInfo item : list) {
            entity.totalQuantity += item.getQuantity();
            entity.totalNormalWages += item.getQuantity() * item.getUnitPrice();
            entity.totalBonus += item.getBonus();
            entity.totalDeductions += item.getDeductions();
            entity.totalSubsidy += item.getSubsidy();
            entity.totalWages += getWages(item);
        }
        return entity;
    }
}
