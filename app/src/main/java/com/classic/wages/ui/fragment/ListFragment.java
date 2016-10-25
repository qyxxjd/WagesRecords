package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.core.utils.DateUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IRules;
import com.classic.wages.ui.rules.basic.DefaultRulesImpl;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.melnykov.fab.FloatingActionButton;
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

    @Inject WorkInfoDao           mWorkInfoDao;
    @Inject SharedPreferencesUtil mPreferencesUtil;

    private IRules  mRules;
    private Integer mFilterYear;
    private Integer mFilterMonth;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        ((WagesApplication) mActivity.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        mYearsSpinner.setItems(Consts.YEARS);
        mMonthsSpinner.setItems(Consts.MONTHS);
        mYearsSpinner.setOnItemSelectedListener(mYearSelectedListener);
        mMonthsSpinner.setOnItemSelectedListener(mMonthSelectedListener);
        mYearsSpinner.setSelectedIndex(DateUtil.getYear() - Consts.MIN_YEAR);
        mMonthsSpinner.setSelectedIndex(DateUtil.getMonth());
        mFilterYear = Integer.valueOf(mYearsSpinner.getText().toString());
        mFilterMonth = Integer.valueOf(mMonthsSpinner.getText().toString());

        mFab.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final int rules = mPreferencesUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        onCalculationRulesChange(rules);
    }

    @Override public void onCalculationRulesChange(int rules) {
        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:

                break;
            case ICalculationRules.RULES_FIXED_MONTH:

                break;
            case ICalculationRules.RULES_PIZZAHUT:

                break;
            case ICalculationRules.RULES_MONTHLY:

                break;
            case ICalculationRules.RULES_QUANTITY:

                break;
            case ICalculationRules.RULES_DEFAULT:
            default:
                mRules = new DefaultRulesImpl(mActivity, mWorkInfoDao);
                break;
        }
        mRecyclerView.setAdapter(mRules.getAdapter());
        mRules.onDataQuery(mFilterYear, mFilterMonth);
    }

    @OnClick(R.id.fab) public void onFabClick(){
        AddActivity.start(mActivity, AddActivity.TYPE_ADD, ICalculationRules.RULES_DEFAULT, new BasicInfo());
    }

    private final MaterialSpinner.OnItemSelectedListener<String> mYearSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterYear = Integer.valueOf(item);
            mRules.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
    private final MaterialSpinner.OnItemSelectedListener<String> mMonthSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterMonth = Integer.valueOf(item);
            mRules.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
}
