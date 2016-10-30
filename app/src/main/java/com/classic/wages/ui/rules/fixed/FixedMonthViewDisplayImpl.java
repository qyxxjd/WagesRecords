package com.classic.wages.ui.rules.fixed;

import android.content.Context;
import android.support.annotation.NonNull;
import cn.qy.util.activity.R;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.core.utils.DateUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.base.BaseViewDisplayImpl;
import com.classic.wages.utils.Util;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.impl
 *
 * 文件描述：列表-按天加班
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public class FixedMonthViewDisplayImpl extends BaseViewDisplayImpl<WorkInfo> {

    private final float mHourlyWage;
    //private final float mFixedHours;
    //private final float mOvertimeHourlyWage;

    public FixedMonthViewDisplayImpl(@NonNull Context context, @NonNull IDao<WorkInfo> dao,
                                     @NonNull SharedPreferencesUtil spUtil) {
        super(context, dao, ICalculationRules.RULES_FIXED_MONTH);
        mHourlyWage = FixedUtils.getPreferencesValue(spUtil,
                Consts.SP_FIXED_MONTH_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
        //mFixedHours = FixedUtils.getPreferencesValue(spUtil,
        //        Consts.SP_FIXED_MONTH_FIXED_HOURS, Consts.DEFAULT_MONTH_FIXED_HOURS);
        //mOvertimeHourlyWage = FixedUtils.getPreferencesValue(spUtil,
        //        Consts.SP_FIXED_MONTH_OVERTIME_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE);
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
              .setText(R.id.list_item_wages, Util.formatWages(FixedUtils.getDayWagesByFixedMonth(
                      item, mHourlyWage)))
              .setTextColorRes(R.id.list_item_wages, color)
              .setText(R.id.list_item_hours, Util.formatHours(Util.ms2hour(
                      item.getEndTime() - item.getStartingTime())))
              .setTextColorRes(R.id.list_item_hours, color);
        helper.getView(R.id.list_item_week)
              .setBackground(getCircularDrawable(color));
    }
}
