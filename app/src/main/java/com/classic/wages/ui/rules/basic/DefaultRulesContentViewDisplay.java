package com.classic.wages.ui.rules.basic;

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
public class DefaultRulesContentViewDisplay extends BaseRulesContentViewDisplay {

    private String mHourlyWage;

    public DefaultRulesContentViewDisplay(@NonNull Activity activity,
                                          @NonNull View rulesContentView,
                                          @NonNull SharedPreferencesUtil spUtil) {
        super(activity, rulesContentView, spUtil);
        mHourlyWage = MoneyUtil.replace(mSpUtil.getStringValue(
                                        Consts.SP_HOURLY_WAGE,
                                        Consts.DEFAULT_HOURLY_WAGE));
    }

    @Override public void setupRulesContent() {
        if(!checkWeakReference()) return;
        mItem1Layout.setVisibility(View.VISIBLE);
        mItem2Layout.setVisibility(View.GONE);
        mItem3Layout.setVisibility(View.GONE);
        mItem1Label.setText(R.string.setting_hourly_wage_label);
        mItem1Value.setText(formatHourlyWage(mHourlyWage));
    }

    @Override protected void onItem1LayoutClick() {
        setupHourlyWage();
    }

    private void setupHourlyWage(){
        if(!checkWeakReference()){ return; }
        //设置当前时薪
        displayInputDialog(R.string.setup_hourly_wage,
                Util.getString(mAppContext, R.string.setting_hourly_wage_label), mHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mHourlyWage = MoneyUtil.replace(input.toString());
                        mItem1Value.setText(formatHourlyWage(input.toString()));
                        mSpUtil.putStringValue(Consts.SP_HOURLY_WAGE, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
}