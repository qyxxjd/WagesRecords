package com.classic.wages.ui.rules.monthly;

import android.content.Context;
import android.support.annotation.NonNull;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.entity.MonthlyInfo;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.base.BaseListLogicImpl;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.monthly
 *
 * 文件描述：列表-月工资展现
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午12:03
 */
public class MonthlyListLogicImpl extends BaseListLogicImpl<MonthlyInfo> {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    public MonthlyListLogicImpl(@NonNull Context context, @NonNull MonthlyInfoDao dao) {
        super(context, dao, ICalculationRules.RULES_MONTHLY);
    }

    @Override protected int getItemLayout() {
        return R.layout.item_monthly;
    }

    @Override protected void onItemUpdate(BaseAdapterHelper helper, MonthlyInfo item, int position) {
        final int color = Util.getColorByWeek(item.getWeek());
        helper.setText(R.id.monthly_item_week, Util.formatWeek(item.getWeek()))
              .setText(R.id.monthly_item_date,
                       DateUtil.formatDate(FORMAT, item.getMonthlyTime()))
              .setTextColorRes(R.id.monthly_item_date, color)
              .setText(R.id.monthly_item_wages, Util.formatWages(MonthlyUtils.getWages(item)))
              .setTextColorRes(R.id.monthly_item_wages, color);
        helper.getView(R.id.monthly_item_week)
              .setBackground(getCircularDrawable(color));

    }
}
