package com.classic.wages.ui.rules.quantity;

import android.support.annotation.NonNull;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.rules.base.BaseWagesDetailLogicImpl;
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
public class QuantityWagesDetailLogicImpl extends BaseWagesDetailLogicImpl<QuantityInfo> {

    @Override protected List<String> convert(List<QuantityInfo> list) {
        return toList(QuantityUtils.getTotalWages(list));
    }

    private List<String> toList(@NonNull QuantityWagesDetailEntity entity){
        List<String> items = new ArrayList<>();
        items.add(Util.formatWageDetail("计件数量", entity.totalQuantity));
        items.add(Util.formatWageDetail("计件工资", entity.totalNormalWages));
        items.add(Util.formatWageDetail("奖金", entity.totalBonus));
        items.add(Util.formatWageDetail("补贴", entity.totalSubsidy));
        items.add(Util.formatWageDetail("扣款", entity.totalDeductions));
        items.add(Util.formatWageDetail("总工资", entity.totalWages));
        return items;
    }
}
