package com.classic.wages.ui.rules.quantity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.base.BaseListLogicImpl;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：列表-月工资展现
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:03
 */
public class QuantityListLogicImpl extends BaseListLogicImpl<QuantityInfo> {

    public QuantityListLogicImpl(@NonNull Context context, @NonNull QuantityInfoDao dao) {
        super(context, dao, ICalculationRules.RULES_QUANTITY);
    }

    @Override protected int getItemLayout() {
        return R.layout.item_quantity;
    }

    @Override protected void onItemUpdate(BaseAdapterHelper helper, QuantityInfo item, int position) {
        final int color = Util.getColorByWeek(item.getWeek());
        helper.setText(R.id.quantity_item_week, Util.formatWeek(item.getWeek()))
              .setText(R.id.quantity_item_date,
                       DateUtil.formatDate(DateUtil.FORMAT_DATE, item.getWorkTime()))
              .setTextColorRes(R.id.quantity_item_date, color)
              .setText(R.id.quantity_item_quantity, String.valueOf(item.getQuantity()))
              .setTextColorRes(R.id.quantity_item_quantity, color)
              .setText(R.id.quantity_item_title, item.getTitle())
              .setTextColorRes(R.id.quantity_item_title, color)
              .setText(R.id.quantity_item_wages, formatWages(QuantityUtils.getWages(item)))
              .setTextColorRes(R.id.quantity_item_wages, color);
        helper.getView(R.id.quantity_item_week)
              .setBackground(getCircularDrawable(color));

    }
}
