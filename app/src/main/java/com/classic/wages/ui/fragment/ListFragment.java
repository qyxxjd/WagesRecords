package com.classic.wages.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IRules;
import com.classic.wages.ui.rules.impl.DefaultRulesImpl;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.melnykov.fab.FloatingActionButton;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：列表、查询页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class ListFragment extends AppBaseFragment {

    @BindView(R.id.query_months_spinner) MaterialSpinner      mMonthsSpinner;
    @BindView(R.id.query_years_spinner)  MaterialSpinner      mYearsSpinner;
    @BindView(R.id.recycler_view)        RecyclerView         mRecyclerView;
    @BindView(R.id.fab)                  FloatingActionButton mFab;

    @Inject WorkInfoDao mWorkInfoDao;
    private IRules      mRules;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        ((WagesApplication)mAppContext).getAppComponent().inject(this);
        final ArrayList<String> temp = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            temp.add(String.valueOf(i));
        }
        mMonthsSpinner.setItems(temp);
        mYearsSpinner.setItems(temp);

        mFab.attachToRecyclerView(mRecyclerView);
        mRules = new DefaultRulesImpl(mActivity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRules.getAdapter());
        mRules.onDataQuery(mWorkInfoDao, null, null);


    }

    @Override public void onCalculationRulesChange(int rules) {
        mRules = new DefaultRulesImpl(mActivity);
    }

    @OnClick(R.id.fab) public void onFabClick(){
        AddActivity.start(mActivity, AddActivity.TYPE_ADD, ICalculationRules.RULES_DEFAULT, new BasicInfo());
    }
}
