package com.classic.wages.ui.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IMainLogic;
import com.classic.wages.ui.rules.basic.DefaultMainLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDayMainLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthMainLogicImpl;
import com.classic.wages.ui.rules.monthly.MonthlyMainLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutMainLogicImpl;
import com.classic.wages.ui.rules.quantity.QuantityMainLogicImpl;
import com.classic.wages.utils.Util;
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

    @Inject WorkInfoDao     mWorkInfoDao;
    @Inject MonthlyInfoDao  mMonthlyInfoDao;
    @Inject QuantityInfoDao mQuantityInfoDao;
    private IMainLogic      mMainLogic;
    private int             mRulesType = -1;

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
                mMainLogic = new FixedDayMainLogicImpl(mWorkInfoDao);
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                mMainLogic = new FixedMonthMainLogicImpl(mWorkInfoDao);
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                mMainLogic = new PizzaHutMainLogicImpl(mWorkInfoDao);
                break;
            case ICalculationRules.RULES_MONTHLY:
                mMainLogic = new MonthlyMainLogicImpl(mMonthlyInfoDao);
                break;
            case ICalculationRules.RULES_QUANTITY:
                mMainLogic = new QuantityMainLogicImpl(mQuantityInfoDao);
                break;
            case ICalculationRules.RULES_DEFAULT:
            default:
                mMainLogic = new DefaultMainLogicImpl(mWorkInfoDao);
                break;
        }
        mMainLogic.calculationCurrentMonthWages(mMonthWages);
        mMainLogic.calculationCurrentYearWages(mYearWages);
        mMainLogic.calculationTotalWages(mTotalWages);
    }

    @Override public void onRecalculation() {
        int rulesType = mRulesType;
        mRulesType = -1;
        onCalculationRulesChange(rulesType);
    }

    @Override public void onFragmentShow() {
        super.onFragmentShow();
        final int rules = Util.getPreferencesInt(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        if(mRulesType != rules){
            mRulesType = rules;
            onCalculationRulesChange(rules);
        }
    }
}
