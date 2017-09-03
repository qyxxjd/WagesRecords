package com.classic.wages.ui.rules.kfc;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.pickerview.TimePickerView;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.rules.base.BaseSettingLogicImpl;
import com.classic.wages.utils.LogUtil;
import com.classic.wages.utils.MoneyUtil;
import com.classic.wages.utils.PickerViewUtil;
import com.classic.wages.utils.ToastUtil;
import com.classic.wages.utils.Util;

import java.util.Date;

import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述：肯德基兼职设置页面实现类
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午8:38
 */
public class KFCSettingLogicImpl extends BaseSettingLogicImpl {

    private String mHourlyWage;
    private String mNightSubsidy;

    public KFCSettingLogicImpl(@NonNull Activity activity,
                               @NonNull View rulesContentView) {
        super(activity, rulesContentView);
        mHourlyWage = MoneyUtil.replace(Util.getPreferencesString(
                                        Consts.SP_HOURLY_WAGE,
                                        Consts.DEFAULT_HOURLY_WAGE));
        mNightSubsidy = MoneyUtil.replace(Util.getPreferencesString(
                                        Consts.SP_NIGHT_SUBSIDY,
                                        Consts.DEFAULT_NIGHT_SUBSIDY));
    }

    @Override public void setupRulesContent() {
        if(!checkWeakReference()) return;
        mItem1Layout.setVisibility(View.VISIBLE);
        mItem2Layout.setVisibility(View.GONE);
        mItem3Layout.setVisibility(View.VISIBLE);
        mItem4Layout.setVisibility(View.VISIBLE);
        mItem1Label.setText(R.string.setting_hourly_wage_label);
        mItem3Label.setText(R.string.setting_night_subsidy_label);
        mItem4Label.setText(R.string.setting_night_subsidy_time_label);
        mItem1Value.setText(formatHourlyWage(mHourlyWage));
        mItem3Value.setText(formatHourlyWage(mNightSubsidy));
        mItem4Value.setText(formatNightSubsidyTime(Util.getPreferencesString(Consts.SP_NIGHT_SUBSIDY_TIME,
                                                                             Consts.DEFAULT_NIGHT_SUBSIDY_TIME)));

    }

    private void setupHourlyWage(){ //设置当前时薪
        if(!checkWeakReference()){ return; }
        displayInputDialog(R.string.setup_hourly_wage,
                Util.getString(mAppContext, R.string.setting_hourly_wage_label), mHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        final String value = String.valueOf(Util.valueOf(input.toString()));
                        mHourlyWage = MoneyUtil.replace(value);
                        mItem1Value.setText(formatHourlyWage(value));
                        Util.putPreferencesString(Consts.SP_HOURLY_WAGE, value);
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
                        final String value = String.valueOf(Util.valueOf(input.toString()));
                        mNightSubsidy = MoneyUtil.replace(value);
                        mItem3Value.setText(formatHourlyWage(value));
                        Util.putPreferencesString(Consts.SP_NIGHT_SUBSIDY, value);
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }

    @Override protected void onItem1LayoutClick() {
        setupHourlyWage();
    }

    @Override protected void onItem3LayoutClick() {
        setupNightSubsidy();
    }

    @Override protected void onItem4LayoutClick() {
        if (checkWeakReference()) {
            PickerViewUtil.createWithHourAndMinute(mActivity.get(), new TimePickerView.OnTimeSelectListener() {
                @Override public void onTimeSelect(Date date, View view) {
                    if (checkWeakReference()) {
                        final String time= Util.getNightSubsidyTime(date);
                        LogUtil.d("晚班补贴开始时间：" + time);
                        mItem4Value.setText(formatNightSubsidyTime(time));
                        Util.putPreferencesString(Consts.SP_NIGHT_SUBSIDY_TIME, time);
                    }
                }
            }).show();
        }
    }
}
