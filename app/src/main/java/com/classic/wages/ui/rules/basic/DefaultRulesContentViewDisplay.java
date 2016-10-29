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

    private String mCurrentHourlyWage;

    public DefaultRulesContentViewDisplay(@NonNull Activity activity,
                                          @NonNull SharedPreferencesUtil spUtil) {
        super(activity, spUtil);
    }

    @Override public void setupRulesContent() {
        if(!checkWeakReference()) return;
        mItem1Layout.setVisibility(View.VISIBLE);
        mItem2Layout.setVisibility(View.GONE);
        mItem3Layout.setVisibility(View.GONE);
        mItem1Label.setText(R.string.setting_hourly_wage_label);

        mCurrentHourlyWage = MoneyUtil.replace(mSpUtil.getStringValue(
                Consts.SP_HOURLY_WAGE,
                Consts.DEFAULT_HOURLY_WAGE));
        mItem1Value.setText(formatHourlyWage(mCurrentHourlyWage));
        mItem1Layout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                setupHourlyWage();
            }
        });
    }

    private void setupHourlyWage(){
        if(!checkWeakReference()){ return; }
        //设置当前时薪
        displayInputDialog(R.string.setup_hourly_wage,
                Util.getString(mAppContext, R.string.setting_hourly_wage_label),
                mCurrentHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!checkWeakReference()){ return; }
                        mItem1Value.setText(formatHourlyWage(input.toString()));
                        mSpUtil.putStringValue(Consts.SP_HOURLY_WAGE, input.toString());
                        ToastUtil.showToast(mAppContext, R.string.setup_success);
                        notifyRecalculation();
                    }
                });
    }
}
