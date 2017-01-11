package com.classic.wages.ui.rules.pizzahut;

import android.content.Context;
import android.support.annotation.NonNull;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.base.BaseListLogicImpl;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.impl
 *
 * 文件描述：列表-默认规则
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public class PizzaHutListLogicImpl extends BaseListLogicImpl<WorkInfo> {

    private final float mHourlyWage;
    private final float mRestHourlyWage;
    private final float mNightSubsidy;

    public PizzaHutListLogicImpl(@NonNull Context context, @NonNull IDao<WorkInfo> dao) {
        super(context, dao, ICalculationRules.RULES_PIZZAHUT);
        mHourlyWage = Util.getPreferencesFloat(
                Consts.SP_PIZZA_HUT_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        mRestHourlyWage = Util.getPreferencesFloat(
                Consts.SP_PIZZA_HUT_REST_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        mNightSubsidy = Util.getPreferencesFloat(
                Consts.SP_PIZZA_HUT_NIGHT_SUBSIDY, Consts.DEFAULT_NIGHT_SUBSIDY);
    }

    @Override protected int getItemLayout() {
        return R.layout.item_default;
    }

    @Override protected void onItemUpdate(BaseAdapterHelper helper, WorkInfo item, int position) {
        final int color = Util.getColorByWeek(item.getWeek());
        helper.setText(R.id.list_item_week, Util.formatWeek(item.getWeek()))
              .setText(R.id.list_item_date,
                       DateUtil.formatDate(DateUtil.FORMAT_DATE, item.getStartingTime()))
              .setTextColorRes(R.id.list_item_date, color)
              .setText(R.id.list_item_time, Util.formatTimeBetween(
                       item.getStartingTime(), item.getEndTime()))
              .setTextColorRes(R.id.list_item_time, color)
              .setText(R.id.list_item_wages,
                       formatWages(PizzaHutUtils.getDayWages(item,
                               mHourlyWage, mRestHourlyWage, mNightSubsidy).totalWages))
              .setTextColorRes(R.id.list_item_wages, color)
              .setText(R.id.list_item_hours, Util.formatHours(Util.ms2hour(
                      item.getEndTime() - item.getStartingTime())))
              .setTextColorRes(R.id.list_item_hours, color);
        helper.getView(R.id.list_item_week)
              .setBackground(getCircularDrawable(color));
    }
}
