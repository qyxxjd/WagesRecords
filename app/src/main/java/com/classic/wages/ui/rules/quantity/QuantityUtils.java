package com.classic.wages.ui.rules.quantity;

import android.support.annotation.NonNull;

import com.classic.wages.consts.Consts;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：计件工资工具类
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:39
 */
final class QuantityUtils {

    static float getWages(@NonNull QuantityInfo info) {
        return info.getUnitPrice() * info.getQuantity() + info.getSubsidy() + info.getBonus() -
                info.getDeductions();
    }

    static QuantityWagesDetailEntity getTotalWages(List<QuantityInfo> list) {
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

    static List<String> getGroupDetail(List<QuantityInfo> list) {
        if (DataUtil.isEmpty(list)) {
            return null;
        }

        HashMap<String, Float> groupMap = new HashMap<>();
        for (QuantityInfo item : list) {
            final String title = item.getTitle();
            if (groupMap.containsKey(title)) {
                groupMap.put(title, groupMap.get(title) + item.getQuantity());
            } else {
                groupMap.put(title, item.getQuantity());
            }
        }
        List<String> groups = new ArrayList<>();

        for(String key : groupMap.keySet()) {
            groups.add(Util.formatWageDetail(key, groupMap.get(key)));
        }
        Collections.sort(groups, new Comparator<String>() {
            @Override public int compare(String o1, String o2) {
                final int v1 = Integer.valueOf(o1.split(Consts.WAGES_DETAIL_SEPARATOR)[1]);
                final int v2 = Integer.valueOf(o2.split(Consts.WAGES_DETAIL_SEPARATOR)[1]);
                return v1 <= v2 ? 1 : -1;
            }
        });
        groups.add(0, "物品名称" + Consts.WAGES_DETAIL_SEPARATOR + "总数量");
        return groups;
    }
}
