package com.classic.wages.ui.rules.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;
import cn.qy.util.activity.R;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.core.utils.ConversionUtil;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.IRules;
import com.classic.wages.ui.widget.CircularDrawable;
import java.util.ArrayList;
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
public class DefaultRulesImpl implements IRules {

    private Context mContext;
    private Adapter mAdapter;
    private final int mRadius;
    public DefaultRulesImpl(Context context){
        this.mContext = context;
        mRadius = ConversionUtil.dp2px(context, 24);
    }

    @Override public Adapter getAdapter() {
        if(null == mAdapter){
            mAdapter = new Adapter(mContext);
        }
        return mAdapter;
    }

    @Override public void onDataQuery(@NonNull IDao dao, Integer year, Integer month) {
        if(dao instanceof WorkInfoDao){
            //final WorkInfoDao workInfoDao = (WorkInfoDao) dao;
            //workInfoDao.query(year, month)
            //        .subscribe(getAdapter());

            final ArrayList<WorkInfo> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                list.add(new WorkInfo());
            }
            getAdapter().replaceAll(list);
        }
    }


    public final class Adapter extends CommonRecyclerAdapter<WorkInfo> implements Action1<List<WorkInfo>>{

        public Adapter(Context context) {
            this(context, null);
        }
        public Adapter(Context context, List<WorkInfo> list) {
            super(context, R.layout.fragment_list_item, list);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, WorkInfo item, int position) {
            final Context context = helper.getView().getContext();
            helper.setText(R.id.list_item_week, "日")
                    .setText(R.id.list_item_date, "2016-07-16")
                  .setTextColorRes(R.id.list_item_date, R.color.week0)
                    .setText(R.id.list_item_time, "20:30 - 22:30")
                  .setTextColorRes(R.id.list_item_time, R.color.week0)
                    .setText(R.id.list_item_wages, "￥ 88.88")
                  .setTextColorRes(R.id.list_item_wages, R.color.week0)
                    .setText(R.id.list_item_hours, "2.25 H")
                  .setTextColorRes(R.id.list_item_hours, R.color.week0);
            TextView tv = helper.getView(R.id.list_item_week);
            tv.setBackground(new CircularDrawable(context.getResources().getColor(R.color.week0),
                                                  mRadius));
        }

        @Override public void call(List<WorkInfo> list) {
            replaceAll(list);
        }
    }
}
