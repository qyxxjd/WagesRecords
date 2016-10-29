package com.classic.wages.ui.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IWagesCalculation;
import com.classic.wages.ui.rules.basic.DefaultWagesCalculationImpl;
import com.classic.wages.ui.rules.fixed.FixedDayWagesCalculationImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthWagesCalculationImpl;
import com.classic.wages.ui.rules.monthly.MonthlyWagesCalculationImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutWagesCalculationImpl;
import com.classic.wages.ui.rules.quantity.QuantityWagesCalculationImpl;
import javax.inject.Inject;

/**
 * 文件描述：首页
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午2:21
 */
public class MainFragment extends AppBaseFragment {

    @BindView(R.id.main_month_wages) TextView mMonthWages;
    @BindView(R.id.main_year_wages)  TextView mYearWages;
    @BindView(R.id.main_total_wages) TextView mTotalWages;

    @Inject WorkInfoDao           mWorkInfoDao;
    @Inject MonthlyInfoDao        mMonthlyInfoDao;
    @Inject QuantityInfoDao       mQuantityInfoDao;
    @Inject SharedPreferencesUtil mSpUtil;
    private IWagesCalculation     mIWagesCalculation;
    private int                   mRulesType = -1;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_main;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        ((WagesApplication) mActivity.getApplicationContext()).getAppComponent().inject(this);
    }

    @TargetApi(Build.VERSION_CODES.DONUT) @Override
    public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);

        onFragmentShow();
    }

    @Override public void onCalculationRulesChange(int rules) {
        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:
                mIWagesCalculation = new FixedDayWagesCalculationImpl(mWorkInfoDao, mSpUtil);
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                mIWagesCalculation = new FixedMonthWagesCalculationImpl(mWorkInfoDao, mSpUtil);
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                mIWagesCalculation = new PizzaHutWagesCalculationImpl(mWorkInfoDao, mSpUtil);
                break;
            case ICalculationRules.RULES_MONTHLY:
                mIWagesCalculation = new MonthlyWagesCalculationImpl(mMonthlyInfoDao);
                break;
            case ICalculationRules.RULES_QUANTITY:
                mIWagesCalculation = new QuantityWagesCalculationImpl(mQuantityInfoDao);
                break;
            case ICalculationRules.RULES_DEFAULT:
            default:
                mIWagesCalculation = new DefaultWagesCalculationImpl(mWorkInfoDao, mSpUtil);
                break;
        }
        mIWagesCalculation.calculationCurrentMonthWages(mMonthWages);
        mIWagesCalculation.calculationCurrentYearWages(mYearWages);
        mIWagesCalculation.calculationTotalWages(mTotalWages);
    }

    @Override public void onRecalculation() {
        int rulesType = mRulesType;
        mRulesType = -1;
        onCalculationRulesChange(rulesType);
    }

    @Override public void onFragmentShow() {
        super.onFragmentShow();
        final int rules = mSpUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        if(mRulesType != rules){
            mRulesType = rules;
            onCalculationRulesChange(rules);
        }
    }
}
