package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.core.utils.DataUtil;
import com.classic.core.utils.DateUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IViewDisplay;
import com.classic.wages.ui.rules.basic.DefaultViewDisplayImpl;
import com.classic.wages.ui.rules.fixed.FixedDayViewDisplayImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthViewDisplayImpl;
import com.classic.wages.ui.rules.monthly.MonthlyViewDisplayImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutViewDisplayImpl;
import com.classic.wages.ui.rules.quantity.QuantityViewDisplayImpl;
import com.classic.wages.utils.HidingScrollListener;
import com.classic.wages.utils.RxUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.List;
import javax.inject.Inject;
import rx.functions.Action1;

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
    @BindView(R.id.query_spinner_layout) LinearLayout         mSpinnerLayout;
    @BindView(R.id.recycler_view)        RecyclerView         mRecyclerView;
    @BindView(R.id.fab)                  FloatingActionButton mFab;

    @Inject WorkInfoDao           mWorkInfoDao;
    @Inject MonthlyInfoDao        mMonthlyInfoDao;
    @Inject QuantityInfoDao       mQuantityInfoDao;
    @Inject SharedPreferencesUtil mSpUtil;

    private IViewDisplay mViewDisplay;
    private String       mFilterYear;
    private String       mFilterMonth;
    private int          mRulesType = -1;

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
        mFilterYear = mYearsSpinner.getText().toString();
        mFilterMonth = mMonthsSpinner.getText().toString();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override public void onHide() {
                mFab.animate().translationY(mFab.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            }

            @Override public void onShow() {
                mFab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

        mRulesType = mSpUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        if(mRulesType == ICalculationRules.RULES_MONTHLY){
            onCalculationRulesChange(mRulesType);
            return;
        }
        mWorkInfoDao.queryYears()
                    .compose(RxUtil.<List<String>>applySchedulers(RxUtil.THREAD_ON_UI_TRANSFORMER))
                    .subscribe(new Action1<List<String>>() {
                        @Override public void call(List<String> strings) {
                            spinnerDataChange(strings);
                        }
                    }, new Action1<Throwable>() {
                        @Override public void call(Throwable throwable) {
                            spinnerDataChange(null);
                        }
                    });
    }

    private void spinnerDataChange(List<String> items){
        if(DataUtil.isEmpty(items)){
            onCalculationRulesChange(mRulesType);
            return;
        }
        mYearsSpinner.setItems(items);
        mYearsSpinner.setSelectedIndex(0);
        mFilterYear = mYearsSpinner.getText().toString();
        onCalculationRulesChange(mRulesType);
    }

    @Override public void onCalculationRulesChange(int rules) {
        mSpinnerLayout.setVisibility(rules == ICalculationRules.RULES_MONTHLY ? View.GONE : View.VISIBLE);
        if(rules == ICalculationRules.RULES_MONTHLY){
            mViewDisplay = new MonthlyViewDisplayImpl(mActivity, mMonthlyInfoDao);
            mRecyclerView.setAdapter(mViewDisplay.getAdapter());
            mViewDisplay.onDataQuery(null, null);
            return;
        }

        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:
                mViewDisplay = new FixedDayViewDisplayImpl(mActivity, mWorkInfoDao, mSpUtil);
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                mViewDisplay = new FixedMonthViewDisplayImpl(mActivity, mWorkInfoDao, mSpUtil);
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                mViewDisplay = new PizzaHutViewDisplayImpl(mActivity, mWorkInfoDao, mSpUtil);
                break;
            case ICalculationRules.RULES_QUANTITY:
                mViewDisplay = new QuantityViewDisplayImpl(mActivity, mQuantityInfoDao);
                break;
            case ICalculationRules.RULES_DEFAULT:
            default:
                mViewDisplay = new DefaultViewDisplayImpl(mActivity, mWorkInfoDao, mSpUtil);
                break;
        }
        mRecyclerView.setAdapter(mViewDisplay.getAdapter());
        mViewDisplay.onDataQuery(mFilterYear, mFilterMonth);
    }

    @Override public void onRecalculation() {
        int rulesType = mRulesType;
        mRulesType = -1;
        onCalculationRulesChange(rulesType);
    }

    @OnClick(R.id.fab) public void onFabClick(){
        AddActivity.start(mActivity, AddActivity.TYPE_ADD, mRulesType, null);
    }

    @Override public void onFragmentShow() {
        super.onFragmentShow();
        final int rules = mSpUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        if(mRulesType != rules){
            mRulesType = rules;
            onCalculationRulesChange(rules);
        }
    }

    private final MaterialSpinner.OnItemSelectedListener<String> mYearSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterYear = item;
            mViewDisplay.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
    private final MaterialSpinner.OnItemSelectedListener<String> mMonthSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterMonth = formatMonth(item);
            mViewDisplay.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
    private String formatMonth(String month){
        return month.length() == 1 ? ("0" + month) : month;
    }
}
