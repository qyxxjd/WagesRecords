package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.core.utils.DataUtil;
import com.classic.core.utils.DateUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.activity.MainActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IListLogic;
import com.classic.wages.ui.rules.IWagesDetailLogic;
import com.classic.wages.ui.rules.basic.DefaultListLogicImpl;
import com.classic.wages.ui.rules.basic.DefaultWagesDetailLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDayListLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDayWagesDetailLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthListLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthWagesDetailLogicImpl;
import com.classic.wages.ui.rules.monthly.MonthlyListLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutListLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutWagesDetailLogicImpl;
import com.classic.wages.ui.rules.quantity.QuantityListLogicImpl;
import com.classic.wages.ui.rules.quantity.QuantityWagesDetailLogicImpl;
import com.classic.wages.utils.HidingScrollListener;
import com.classic.wages.utils.RxUtil;
import com.classic.wages.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.List;
import javax.inject.Inject;
import rx.functions.Action1;

import static com.classic.wages.ui.activity.AddActivity.TYPE_ADD;

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

    private IListLogic        mListLogic;
    private IWagesDetailLogic mWagesDetailLogic;
    private String            mFilterYear;
    private String            mFilterMonth;
    private int               mRulesType = -1;

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

        mRulesType = WagesApplication.getPreferencesUtil()
                                     .getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.wages_detail_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(mRulesType == ICalculationRules.RULES_MONTHLY) return false;
        if(mWagesDetailLogic != null && mListLogic != null){
            mWagesDetailLogic.onDisplay(mActivity, ((MainActivity)mActivity).getToolbar(),
                    mListLogic.getData());
        }
        return true;
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
        final boolean hasMonthlyRules = rules == ICalculationRules.RULES_MONTHLY;
        mSpinnerLayout.setVisibility(hasMonthlyRules ? View.GONE : View.VISIBLE);
        setHasOptionsMenu(!hasMonthlyRules);
        if(hasMonthlyRules){
            mListLogic = new MonthlyListLogicImpl(mActivity, mMonthlyInfoDao);
            mRecyclerView.setAdapter(mListLogic.getAdapter());
            mListLogic.onDataQuery(null, null);
            return;
        }

        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:
                mListLogic = new FixedDayListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new FixedDayWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                mListLogic = new FixedMonthListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new FixedMonthWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                mListLogic = new PizzaHutListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new PizzaHutWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_QUANTITY:
                mListLogic = new QuantityListLogicImpl(mActivity, mQuantityInfoDao);
                mWagesDetailLogic = new QuantityWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_DEFAULT:
            default:
                mListLogic = new DefaultListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new DefaultWagesDetailLogicImpl();
                break;
        }
        mRecyclerView.setAdapter(mListLogic.getAdapter());
        mListLogic.onDataQuery(mFilterYear, mFilterMonth);
    }

    @Override public void onRecalculation() {
        int rulesType = mRulesType;
        mRulesType = -1;
        onCalculationRulesChange(rulesType);
    }

    @OnClick(R.id.fab) public void onFabClick(){
        AddActivity.start(mActivity, TYPE_ADD, mRulesType, null);
    }

    @Override public void onFragmentShow() {
        super.onFragmentShow();
        final int rules = Util.getPreferencesInt(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
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
            mListLogic.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
    private final MaterialSpinner.OnItemSelectedListener<String> mMonthSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterMonth = formatMonth(item);
            mListLogic.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
    private String formatMonth(String month){
        return month.length() == 1 ? ("0" + month) : month;
    }
}
