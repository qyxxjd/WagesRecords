package com.classic.wages.ui.rules.basic;

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
 * 文件描述：列表-默认规则
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public class DefaultViewDisplayImpl extends BaseViewDisplayImpl<WorkInfo> {

    private final float mHourlyWage;

    public DefaultViewDisplayImpl(@NonNull Context context, @NonNull IDao<WorkInfo> dao,
                                  @NonNull SharedPreferencesUtil spUtil) {
        super(context, dao, ICalculationRules.RULES_DEFAULT);
        mHourlyWage = Float.valueOf(
                spUtil.getStringValue(Consts.SP_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE));
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
                       Util.formatWages(DefaultUtil.getDayWages(item, mHourlyWage)))
              .setTextColorRes(R.id.list_item_wages, color)
              .setText(R.id.list_item_hours, Util.formatHours(DefaultUtil.getDayHours(item)))
              .setTextColorRes(R.id.list_item_hours, color);
        helper.getView(R.id.list_item_week)
              .setBackground(getCircularDrawable(color));
    }
}


//public class DefaultViewDisplayImpl implements IViewDisplay,
//                                         CommonRecyclerAdapter.OnItemClickListener,
//                                         CommonRecyclerAdapter.OnItemLongClickListener{
//
//    private       Context     mContext;
//    private       Adapter     mAdapter;
//    private       WorkInfoDao mWorkInfoDao;
//    private final int         mRadius;
//    private final float       mHourlyWage;
//
//    public DefaultViewDisplayImpl(Context context, @NonNull WorkInfoDao workInfoDao){
//        this.mContext = context;
//        this.mWorkInfoDao = workInfoDao;
//        mRadius = ConversionUtil.dp2px(context, 24);
//        mHourlyWage = Float.valueOf(new SharedPreferencesUtil(context, Consts.SP_NAME)
//                .getStringValue(Consts.SP_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE));
//    }
//
//    @Override public Adapter getAdapter() {
//        if(null == mAdapter){
//            mAdapter = new Adapter(mContext);
//            mAdapter.setOnItemClickListener(this);
//            mAdapter.setOnItemLongClickListener(this);
//        }
//        return mAdapter;
//    }
//
//    @Override public void onDataQuery(Integer year, Integer month) {
//        Logger.d("onDataQuery:"+year+","+month);
//        mWorkInfoDao.query(year, month)
//                    .compose(RxUtil.<List<WorkInfo>>applySchedulers(RxUtil.THREAD_ON_UI_TRANSFORMER))
//                    .subscribe(getAdapter(), RxUtil.ERROR_ACTION);
//    }
//
//    @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
//        AddActivity.start((Activity) mContext,
//                          AddActivity.TYPE_MODIFY,
//                          ICalculationRules.RULES_DEFAULT,
//                          mAdapter.getItem(position));
//    }
//
//    @Override
//    public void onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, final int position) {
//        new MaterialDialog.Builder(mContext)
//                .title(R.string.delete)
//                .titleColorRes(R.color.primary_text)
//                .backgroundColorRes(R.color.white)
//                .content(R.string.delete_data_hint)
//                .contentColorRes(R.color.primary_light)
//                .positiveText(R.string.delete)
//                .negativeText(R.string.cancel)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override public void onClick(MaterialDialog dialog, DialogAction which) {
//                        mWorkInfoDao.delete(mAdapter.getItem(position).getId());
//                    }
//                }).show();
//    }
//
//    private final class Adapter extends CommonRecyclerAdapter<WorkInfo> implements
//            Action1<List<WorkInfo>>{
//        private Context mContext;
//        Adapter(Context context) {
//            this(context, null);
//        }
//        Adapter(Context context, List<WorkInfo> list) {
//            super(context, R.layout.item_default, list);
//            mContext = context.getApplicationContext();
//        }
//
//        @Override public void onUpdate(BaseAdapterHelper helper, WorkInfo item, int position) {
//            final int color = Util.getColorByWeek(item.getWeek());
//            helper.setText(R.id.list_item_week, Util.formatWeek(item.getWeek()))
//                  .setText(R.id.list_item_date,
//                           DateUtil.formatDate(DateUtil.FORMAT_DATE, item.getStartingTime()))
//                  .setTextColorRes(R.id.list_item_date, color)
//                  .setText(R.id.list_item_time, DefaultUtil.formatTimeBetween(
//                           item.getStartingTime(), item.getEndTime()))
//                  .setTextColorRes(R.id.list_item_time, color)
//                  .setText(R.id.list_item_wages,
//                           Util.formatWages(DefaultUtil.getDayWages(item, mHourlyWage)))
//                  .setTextColorRes(R.id.list_item_wages, color)
//                  .setText(R.id.list_item_hours, Util.formatHours(DefaultUtil.getDayHours(item)))
//                  .setTextColorRes(R.id.list_item_hours, color);
//            helper.getView(R.id.list_item_week)
//                  .setBackground(Util.getCircularDrawable(Util.getColor(mContext, color), mRadius));
//        }
//
//        @Override public void call(List<WorkInfo> list) {
//            if(DataUtil.isEmpty(list)){
//                clear();
//            } else {
//                replaceAll(list);
//            }
//        }
//    }
//}
