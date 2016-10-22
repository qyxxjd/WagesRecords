package com.classic.wages.ui.rules.impl;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cn.qy.util.activity.R;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.core.utils.ConversionUtil;
import com.classic.core.utils.DateUtil;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IRules;
import com.classic.wages.utils.RxUtil;
import com.classic.wages.utils.Util;
import java.util.List;
import rx.functions.Action1;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.impl
 *
 * 文件描述：默认规则
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public class DefaultRulesImpl implements IRules,
                                         CommonRecyclerAdapter.OnItemClickListener,
                                         CommonRecyclerAdapter.OnItemLongClickListener{

    private       Context     mContext;
    private       Adapter     mAdapter;
    private       WorkInfoDao mWorkInfoDao;
    private final int         mRadius;
    private final float       mHourlyWage;

    public DefaultRulesImpl(Context context, @NonNull WorkInfoDao workInfoDao){
        this.mContext = context;
        this.mWorkInfoDao = workInfoDao;
        mRadius = ConversionUtil.dp2px(context, 24);
        mHourlyWage = Float.valueOf(new SharedPreferencesUtil(context, Consts.SP_NAME)
                .getStringValue(Consts.SP_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE));
    }

    @Override public Adapter getAdapter() {
        if(null == mAdapter){
            mAdapter = new Adapter(mContext);
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnItemLongClickListener(this);
        }
        return mAdapter;
    }

    @Override public void onDataQuery(Integer year, Integer month) {
        mWorkInfoDao.query(year, month)
                    .compose(RxUtil.<List<WorkInfo>>applySchedulers(RxUtil.THREAD_ON_UI_TRANSFORMER))
                    .subscribe(getAdapter(), RxUtil.ERROR_ACTION);
    }

    @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        AddActivity.start((Activity) mContext,
                          AddActivity.TYPE_MODIFY,
                          ICalculationRules.RULES_DEFAULT,
                          mAdapter.getItem(position));
    }

    @Override
    public void onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        //TODO
        mWorkInfoDao.delete(mAdapter.getItem(position).getId());
    }

    private final class Adapter extends CommonRecyclerAdapter<WorkInfo> implements
            Action1<List<WorkInfo>>{
        private Context mContext;
        Adapter(Context context) {
            this(context, null);
        }
        Adapter(Context context, List<WorkInfo> list) {
            super(context, R.layout.fragment_list_item, list);
            mContext = context.getApplicationContext();
        }

        @Override public void onUpdate(BaseAdapterHelper helper, WorkInfo item, int position) {
            final int color = Util.getColorByWeek(item.getWeek());
            helper.setText(R.id.list_item_week, Util.formatWeek(item.getWeek()))
                  .setText(R.id.list_item_date, DateUtil.formatDate(DateUtil.FORMAT_DATE,
                                                                    item.getStartingTime()))
                  .setTextColorRes(R.id.list_item_date, color)
                  .setText(R.id.list_item_time, formatTimeBetween(item.getStartingTime(),
                                                                  item.getEndTime()))
                  .setTextColorRes(R.id.list_item_time, color)
                  .setText(R.id.list_item_wages, Util.formatWages(getDayWages(item)))
                  .setTextColorRes(R.id.list_item_wages, color)
                  .setText(R.id.list_item_hours, Util.formatHours(getDayHours(item)))
                  .setTextColorRes(R.id.list_item_hours, color);
            helper.getView(R.id.list_item_week)
                  .setBackground(Util.getCircularDrawable(Util.getColor(mContext, color), mRadius));
        }

        @Override public void call(List<WorkInfo> list) {
            replaceAll(list);
        }

        private String formatTimeBetween(long startTime, long endTime){
            return new StringBuilder(DateUtil.formatDate(DateUtil.FORMAT_TIME, startTime))
                    .append(" - ")
                    .append(DateUtil.formatDate(DateUtil.FORMAT_TIME, endTime))
                    .toString();
        }

    }

    private float getDayHours(WorkInfo workInfo){
        return Util.ms2hour(workInfo.getEndTime()-workInfo.getStartingTime());
    }

    private float getDayWages(WorkInfo workInfo){
        final float hours = getDayHours(workInfo);
        final float result = MoneyUtil.newInstance(hours).multiply(mHourlyWage)
                                      .multiply(workInfo.getMultiple() > 0f ? workInfo.getMultiple() : 1)
                                      .round(Consts.DEFAULT_SCALE).create().floatValue();
        return result;
    }
}
