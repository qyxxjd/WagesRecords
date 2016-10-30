package com.classic.wages.ui.rules.fixed;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.core.utils.ToastUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.rules.base.BaseRulesContentViewDisplay;
import com.classic.wages.utils.Util;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午8:38
 */
public class FixedDayRulesContentViewDisplay extends BaseRulesContentViewDisplay {

    private String mHourlyWage;
    private String mFixedHours;
    private String mOvertimeHourlyWage;

    public FixedDayRulesContentViewDisplay(@NonNull Activity activity,
                                           @NonNull View rulesContentView,
                                           @NonNull SharedPreferencesUtil spUtil) {
        super(activity, rulesContentView, spUtil);
        mHourlyWage = MoneyUtil.replace(mSpUtil.getStringValue(
                                        Consts.SP_FIXED_DAY_HOURLY_WAGE,
                                        Consts.DEFAULT_HOURLY_WAGE));
        mFixedHours = MoneyUtil.replace(mSpUtil.getStringValue(
                                        Consts.SP_FIXED_DAY_FIXED_HOURS,
                                        Consts.DEFAULT_DAY_FIXED_HOURS));
        mOvertimeHourlyWage = MoneyUtil.replace(mSpUtil.getStringValue(
                                        Consts.SP_FIXED_DAY_OVERTIME_HOURLY_WAGE,
                                        Consts.DEFAULT_HOURLY_WAGE));
    }

    @Override public void setupRulesContent() {
        if(!checkWeakReference()) return;
        mItem1Layout.setVisibility(View.VISIBLE);
        mItem2Layout.setVisibility(View.VISIBLE);
        mItem3Layout.setVisibility(View.VISIBLE);
        mItem1Label.setText(R.string.setting_hourly_wage_label);
        mItem2Label.setText(R.string.setting_fixed_hours_label);
        mItem3Label.setText(R.string.setting_overtime_hourly_wage_label);
        mItem1Value.setText(formatHourlyWage(mHourlyWage));
        mItem2Value.setText(formatHours(mFixedHours));
        mItem3Value.setText(formatHourlyWage(mOvertimeHourlyWage));
    }

    @Override protected void onItem1LayoutClick() {
        setupHourlyWage();
    }

    @Override protected void onItem2LayoutClick() {
        setupFixedHours();
    }

    @Override protected void onItem3LayoutClick() {
        setupOvertimeHourlyWage();
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
                        mSpUtil.putStringValue(Consts.SP_FIXED_DAY_HOURLY_WAGE, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
    private void setupFixedHours(){ //设置固定时长
        if(!checkWeakReference()){ return; }
        displayInputDialog(R.string.setup_fixed_hours,
                Util.getString(mAppContext, R.string.setting_fixed_hours_label), mFixedHours,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mFixedHours = MoneyUtil.replace(input.toString());
                        mItem2Value.setText(formatHours(input.toString()));
                        mSpUtil.putStringValue(Consts.SP_FIXED_DAY_FIXED_HOURS, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
    private void setupOvertimeHourlyWage(){ //设置加班时薪
        if(!checkWeakReference()){ return; }
        displayInputDialog(R.string.setup_overtime_hourly_wage,
                Util.getString(mAppContext, R.string.setting_overtime_hourly_wage_label),
                mOvertimeHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mOvertimeHourlyWage = MoneyUtil.replace(input.toString());
                        mItem3Value.setText(formatHourlyWage(input.toString()));
                        mSpUtil.putStringValue(Consts.SP_FIXED_DAY_OVERTIME_HOURLY_WAGE, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
}
