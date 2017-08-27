package com.classic.wages.ui.rules.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.android.rx.RxUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.rules.IListLogic;
import com.classic.wages.ui.widget.CircularDrawable;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.MoneyUtil;
import com.classic.wages.utils.Util;

import java.util.Calendar;
import java.util.List;

import cn.qy.util.activity.R;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.impl
 *
 * 文件描述：列表-默认规则
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public abstract class BaseListLogicImpl<T extends BasicInfo> implements IListLogic,
        CommonRecyclerAdapter.OnItemClickListener,
        CommonRecyclerAdapter.OnItemLongClickListener {

    private final Context mAppContext;
    private final Context mContext;
    private final int     mRadius;
    private       IDao<T> mDao;
    private       int     mRules;
    private       Adapter mAdapter;
    private       long    mMonthStartTime;
    private       long    mMonthEndTime;

    protected abstract int getItemLayout();

    protected abstract void onItemUpdate(BaseAdapterHelper helper, T item, int position);

    public BaseListLogicImpl(@NonNull Context context, @NonNull IDao<T> dao, int rules) {
        this.mContext = context;
        this.mAppContext = context.getApplicationContext();
        this.mDao = dao;
        this.mRules = rules;
        mRadius = Util.dp2px(context, 24);
    }

    @Override public List<T> getData() {
        return getAdapter().getData();
    }

    @Override public Adapter getAdapter() {
        if (null == mAdapter) {
            mAdapter = new Adapter(mContext, getItemLayout());
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnItemLongClickListener(this);
        }
        return mAdapter;
    }

    @Override public void onDataQuery(String year, String month) {
        // ToastUtil.showToast(mAppContext, "onDataQuery:" + year + "," + month);
        if (!Util.isEmpty(year) && !Util.isEmpty(month)) {
            // 年份、月份都不为空，按照自定义工资计算周期进行查询
            calculationMonthTime(Integer.valueOf(year), Integer.valueOf(month));
            onDataQuery(mMonthStartTime, mMonthEndTime);
            return;
        }
        // 普通查询
        Observable<List<T>> observable = mDao.query(year, formatMonth(month));
        if (null != observable) {
            observable.compose(RxUtil.<List<T>>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                      .subscribe(getAdapter(), Util.ERROR_ACTION);
        }
    }

    @Override public void onDataQuery(long startTime, long endTime) {
        // ToastUtil.showToast(mAppContext, "onDataQuery:" + DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, startTime) +
        //                                  "," + DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, endTime));
        Observable<List<T>> observable = mDao.query(startTime, endTime);
        if (null != observable) {
            observable.compose(RxUtil.<List<T>>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                      .subscribe(getAdapter(), Util.ERROR_ACTION);
        }
    }

    @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        AddActivity.start((Activity) mContext, AddActivity.TYPE_MODIFY, mRules,
                mAdapter.getItem(position));
    }

    @Override
    public boolean onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, final int position) {
        new MaterialDialog.Builder(mContext).title(R.string.delete)
                                            .titleColorRes(R.color.primary_text)
                                            .backgroundColorRes(R.color.white)
                                            .content(R.string.delete_data_hint)
                                            .contentColorRes(R.color.primary_light)
                                            .positiveText(R.string.delete)
                                            .negativeText(R.string.cancel)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    mDao.delete(mAdapter.getItem(position).getId());
                                                }
                                            })
                                            .show();
        return true;
    }

    private final class Adapter extends CommonRecyclerAdapter<T> implements Consumer<List<T>> {

        Adapter(Context context, int layoutResId) {
            super(context, layoutResId);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, T item, int position) {
            onItemUpdate(helper, item, position);
        }

        @Override public void accept(@io.reactivex.annotations.NonNull List<T> list) throws Exception {
            if (DataUtil.isEmpty(list)) {
                clear();
            } else {
                replaceAll(list);
            }
        }
    }

    protected CircularDrawable getCircularDrawable(int color) {
        return Util.getCircularDrawable(Util.getColor(mAppContext, color), mRadius);
    }

    protected String formatWages(Number number) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder("￥").append(MoneyUtil.replace(number, Consts.DEFAULT_SCALE))
                                     .toString();
    }

    private String formatMonth(String month) {
        if (Util.isEmpty(month)) {
            return null;
        }
        return month.length() == 1 ? ("0" + month) : month;
    }

    /**
     * 计算月工资计算周期的开始、结束时间
     */
    private void calculationMonthTime(int year, int month) {
        // Calendar 对应的月份从0开始，需要减一
        Calendar calendar = DateUtil.getTimeInMillis(year, month - 1);
        mMonthStartTime = calendar.getTimeInMillis();
        // LogUtil.d("计算出来的工资计算周期 <-- " + DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, mMonthStartTime));
        calendar.add(Calendar.MONTH, 1);
        mMonthEndTime = calendar.getTimeInMillis();
        // LogUtil.d("计算出来的工资计算周期 --> " + DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, mMonthEndTime));

    }
}
