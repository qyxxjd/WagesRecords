package com.classic.wages.ui.rules.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.core.utils.MoneyUtil;
import com.classic.wages.ui.activity.MainActivity;
import com.classic.wages.ui.rules.ISettingLogic;
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
public abstract class BaseSettingLogicImpl implements ISettingLogic,
        View.OnClickListener{

    private   WeakReference<Activity> mActivity;
    private   View                    mRootView;
    protected Context                 mAppContext;

    protected View     mItem1Layout;
    protected View     mItem2Layout;
    protected View     mItem3Layout;
    protected TextView mItem1Label;
    protected TextView mItem2Label;
    protected TextView mItem3Label;
    protected TextView mItem1Value;
    protected TextView mItem2Value;
    protected TextView mItem3Value;

    public BaseSettingLogicImpl(@NonNull Activity activity,
                                @NonNull View rulesContentView) {
        mActivity = new WeakReference<>(activity);
        mRootView = rulesContentView;
        mAppContext = mActivity.get().getApplicationContext();
        initView();
    }

    private void initView(){
        final Activity activity = mActivity.get();
        if(null == activity) return;
        mRootView.setVisibility(View.VISIBLE);
        mItem1Layout = mRootView.findViewById(R.id.setting_rules_item1_layout);
        mItem2Layout = mRootView.findViewById(R.id.setting_rules_item2_layout);
        mItem3Layout = mRootView.findViewById(R.id.setting_rules_item3_layout);
        mItem1Label = (TextView) mRootView.findViewById(R.id.setting_rules_item1_label);
        mItem2Label = (TextView) mRootView.findViewById(R.id.setting_rules_item2_label);
        mItem3Label = (TextView) mRootView.findViewById(R.id.setting_rules_item3_label);
        mItem1Value = (TextView) mRootView.findViewById(R.id.setting_rules_item1_value);
        mItem2Value = (TextView) mRootView.findViewById(R.id.setting_rules_item2_value);
        mItem3Value = (TextView) mRootView.findViewById(R.id.setting_rules_item3_value);
        mItem1Layout.setOnClickListener(this);
        mItem2Layout.setOnClickListener(this);
        mItem3Layout.setOnClickListener(this);
    }

    protected void onItem1LayoutClick(){ }
    protected void onItem2LayoutClick(){ }
    protected void onItem3LayoutClick(){ }
    @Override public void onClick(View view) {
        if(!checkWeakReference()) return;
        switch (view.getId()){
            case R.id.setting_rules_item1_layout:
                onItem1LayoutClick();
                break;
            case R.id.setting_rules_item2_layout:
                onItem2LayoutClick();
                break;
            case R.id.setting_rules_item3_layout:
                onItem3LayoutClick();
                break;
            default:
                break;
        }
    }

    protected boolean checkWeakReference(){
        return mActivity.get() != null;
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
        return Util.getString(mAppContext, R.string.hourly_wage_suffix, MoneyUtil.replace(hourlyWage));
    }

    protected String formatHours(String hours){
        return Util.getString(mAppContext, R.string.hours_suffix, MoneyUtil.replace(hours));
    }

    protected void notifyRecalculation(){
        if(null != mActivity.get()){
            ((MainActivity)mActivity.get()).notifyRecalculation();
        }
    }
}
