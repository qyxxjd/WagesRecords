package com.classic.wages.ui.rules.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.core.utils.ConversionUtil;
import com.classic.core.utils.DataUtil;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.rules.IViewDisplay;
import com.classic.wages.ui.widget.CircularDrawable;
import com.classic.wages.utils.RxUtil;
import com.classic.wages.utils.Util;
import com.orhanobut.logger.Logger;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.impl
 *
 * 文件描述：列表-默认规则
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public abstract class BaseViewDisplayImpl<T extends BasicInfo> implements IViewDisplay,
                                         CommonRecyclerAdapter.OnItemClickListener,
                                         CommonRecyclerAdapter.OnItemLongClickListener{

    private final Context mAppContext;
    private final Context mContext;
    private final int     mRadius;
    private       IDao<T> mDao;
    private       int     mRules;
    private       Adapter mAdapter;

    protected abstract int  getItemLayout();
    protected abstract void onItemUpdate(BaseAdapterHelper helper, T item, int position);

    public BaseViewDisplayImpl(@NonNull Context context, @NonNull IDao<T> dao, int rules){
        this.mContext = context;
        this.mAppContext = context.getApplicationContext();
        this.mDao = dao;
        this.mRules = rules;
        mRadius = ConversionUtil.dp2px(context, 24);
    }

    @Override public Adapter getAdapter() {
        if(null == mAdapter){
            mAdapter = new Adapter(mContext, getItemLayout());
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnItemLongClickListener(this);
        }
        return mAdapter;
    }

    @Override public void onDataQuery(Integer year, Integer month) {
        Logger.d("onDataQuery:"+year+","+month);
        Observable<List<T>> observable = mDao.query(year, month);
        if(null != observable){
            observable.compose(RxUtil.<List<T>>applySchedulers(RxUtil.THREAD_ON_UI_TRANSFORMER))
                      .subscribe(getAdapter(), RxUtil.ERROR_ACTION);
        }
    }

    @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        AddActivity.start((Activity) mContext,
                          AddActivity.TYPE_MODIFY,
                          mRules,
                          mAdapter.getItem(position));
    }

    @Override
    public void onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, final int position) {
        new MaterialDialog.Builder(mContext)
                .title(R.string.delete)
                .titleColorRes(R.color.primary_text)
                .backgroundColorRes(R.color.white)
                .content(R.string.delete_data_hint)
                .contentColorRes(R.color.primary_light)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override public void onClick(MaterialDialog dialog, DialogAction which) {
                        mDao.delete(mAdapter.getItem(position).getId());
                    }
                }).show();
    }

    private final class Adapter extends CommonRecyclerAdapter<T> 
                                implements Action1<List<T>>{

        Adapter(Context context, int layoutResId) {
            super(context, layoutResId);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, T item, int position) {
            onItemUpdate(helper, item, position);
        }

        @Override public void call(List<T> list) {
            if(DataUtil.isEmpty(list)){
                clear();
            } else {
                replaceAll(list);
            }
        }
    }

    protected CircularDrawable getCircularDrawable(int color){
        return Util.getCircularDrawable(Util.getColor(mAppContext, color), mRadius);
    }
}
