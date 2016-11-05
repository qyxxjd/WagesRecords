package com.classic.wages.ui.rules.pizzahut;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.ToastUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.rules.base.BaseSettingLogicImpl;
import com.classic.wages.utils.Util;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午8:38
 */
public class PizzaHutSettingLogicImpl extends BaseSettingLogicImpl {

    private String mHourlyWage;
    private String mRestHourlyWage;
    private String mNightSubsidy;

    public PizzaHutSettingLogicImpl(@NonNull Activity activity,
                                    @NonNull View rulesContentView) {
        super(activity, rulesContentView);
        mHourlyWage = MoneyUtil.replace(Util.getPreferencesString(
                                        Consts.SP_PIZZA_HUT_HOURLY_WAGE,
                                        Consts.DEFAULT_HOURLY_WAGE));
        mRestHourlyWage = MoneyUtil.replace(Util.getPreferencesString(
                                        Consts.SP_PIZZA_HUT_REST_HOURLY_WAGE,
                                        Consts.DEFAULT_HOURLY_WAGE));
        mNightSubsidy = MoneyUtil.replace(Util.getPreferencesString(
                                        Consts.SP_PIZZA_HUT_NIGHT_SUBSIDY,
                                        Consts.DEFAULT_NIGHT_SUBSIDY));
    }

    @Override public void setupRulesContent() {
        if(!checkWeakReference()) return;
        mItem1Layout.setVisibility(View.VISIBLE);
        mItem2Layout.setVisibility(View.VISIBLE);
        mItem3Layout.setVisibility(View.VISIBLE);
        mItem1Label.setText(R.string.setting_hourly_wage_label);
        mItem2Label.setText(R.string.setting_rest_hourly_wage_label);
        mItem3Label.setText(R.string.setting_night_subsidy_label);
        mItem1Value.setText(formatHourlyWage(mHourlyWage));
        mItem2Value.setText(formatHourlyWage(mRestHourlyWage));
        mItem3Value.setText(formatHourlyWage(mNightSubsidy));
    }

    private void setupHourlyWage(){ //设置当前时薪
        if(!checkWeakReference()){ return; }
        displayInputDialog(R.string.setup_hourly_wage,
                Util.getString(mAppContext, R.string.setting_hourly_wage_label), mHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mHourlyWage = MoneyUtil.replace(input.toString());
                        mItem1Value.setText(formatHourlyWage(input.toString()));
                        Util.putPreferencesString(Consts.SP_PIZZA_HUT_HOURLY_WAGE, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
    private void setupRestHourlyWage(){ //设置带薪休息时薪
        if(!checkWeakReference()){ return; }
        displayInputDialog(R.string.setup_rest_hourly_wage,
                Util.getString(mAppContext, R.string.setting_rest_hourly_wage_label), mRestHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mRestHourlyWage = MoneyUtil.replace(input.toString());
                        mItem2Value.setText(formatHourlyWage(input.toString()));
                        Util.putPreferencesString(Consts.SP_PIZZA_HUT_REST_HOURLY_WAGE, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
    private void setupNightSubsidy(){ //设置晚班补贴
        if(!checkWeakReference()){ return; }
        displayInputDialog(R.string.setup_night_subsidy,
                Util.getString(mAppContext, R.string.setting_night_subsidy_label), mNightSubsidy,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mNightSubsidy = MoneyUtil.replace(input.toString());
                        mItem3Value.setText(formatHourlyWage(input.toString()));
                        Util.putPreferencesString(Consts.SP_PIZZA_HUT_NIGHT_SUBSIDY, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }

    @Override protected void onItem1LayoutClick() {
        setupHourlyWage();
    }

    @Override protected void onItem2LayoutClick() {
        setupRestHourlyWage();
    }

    @Override protected void onItem3LayoutClick() {
        setupNightSubsidy();
    }

}
