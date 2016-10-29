package com.classic.wages.ui.rules.quantity;

import android.support.annotation.NonNull;
import com.classic.wages.entity.QuantityInfo;

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
}
