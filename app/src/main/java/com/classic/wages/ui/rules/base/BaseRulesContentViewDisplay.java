package com.classic.wages.ui.rules.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.ui.activity.MainActivity;
import com.classic.wages.ui.rules.IRulesContentViewDisplay;
import com.classic.wages.utils.Util;
import java.lang.ref.WeakReference;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/29 下午8:38
 */
public abstract class BaseRulesContentViewDisplay implements IRulesContentViewDisplay {

    private   WeakReference<Activity> mActivity;
    private   WeakReference<View>     mRootView;
    protected SharedPreferencesUtil   mSpUtil;
    protected Context                 mAppContext;

    @BindView(R.id.setting_rules_item1_layout) protected View     mItem1Layout;
    @BindView(R.id.setting_rules_item2_layout) protected View     mItem2Layout;
    @BindView(R.id.setting_rules_item3_layout) protected View     mItem3Layout;
    @BindView(R.id.setting_rules_item1_label)  protected TextView mItem1Label;
    @BindView(R.id.setting_rules_item2_label)  protected TextView mItem2Label;
    @BindView(R.id.setting_rules_item3_label)  protected TextView mItem3Label;
    @BindView(R.id.setting_rules_item1_value)  protected TextView mItem1Value;
    @BindView(R.id.setting_rules_item2_value)  protected TextView mItem2Value;
    @BindView(R.id.setting_rules_item3_value)  protected TextView mItem3Value;

    public BaseRulesContentViewDisplay(@NonNull Activity activity,
                                       @NonNull SharedPreferencesUtil spUtil) {
        mActivity = new WeakReference<>(activity);
        mSpUtil = spUtil;
        if(mActivity.get() != null){
            mAppContext = mActivity.get().getApplicationContext();
            mRootView = new WeakReference<>(
                    mActivity.get().findViewById(R.id.setting_rules_content)
            );
            ButterKnife.bind(this, mRootView.get());
        }
    }

    protected boolean checkWeakReference(){
        return mActivity.get() != null && mRootView.get() != null;
    }

    protected void displayInputDialog(int titleResId, String inputHint, String inputDefaultValue,
                                    MaterialDialog.InputCallback callback){
        new MaterialDialog.Builder(mActivity.get())
                .title(titleResId)
                .titleColorRes(R.color.primary_text)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.secondary_text)
                .inputRange(1, 8)
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(inputHint, inputDefaultValue, false, callback)
                .show();
    }

    protected String formatHourlyWage(String hourlyWage){
        return MoneyUtil.replace(hourlyWage) +
                Util.getString(mAppContext, R.string.hourly_wage_suffix);
    }

    protected void notifyRecalculation(){
        ((MainActivity)mActivity.get()).notifyRecalculation();
    }
}
