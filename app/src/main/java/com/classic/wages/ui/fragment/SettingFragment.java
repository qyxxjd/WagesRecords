package com.classic.wages.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.core.permissions.AfterPermissionGranted;
import com.classic.core.permissions.EasyPermissions;
import com.classic.core.utils.IntentUtil;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.core.utils.ToastUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.activity.MainActivity;
import com.classic.wages.ui.activity.OpenSourceLicensesActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.dialog.AuthorDialog;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.utils.PgyerUtil;
import com.classic.wages.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.List;
import javax.inject.Inject;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：设置页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class SettingFragment extends AppBaseFragment implements MaterialSpinner.OnItemSelectedListener<String>{
    private static final int REQUEST_CODE_FEEDBACK  = 201;
    private static final String FEEDBACK_PERMISSION = Manifest.permission.RECORD_AUDIO;

    @BindView(R.id.setting_rules_spinner) MaterialSpinner mRulesSpinner;

    @Inject SharedPreferencesUtil mPreferencesUtil;

    private int          mCurrentRules;
    private AuthorDialog mAuthorDialog;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_setting;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        ((WagesApplication) mActivity.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        mRulesSpinner.setItems(Consts.RULES_LIST);
        mRulesSpinner.setOnItemSelectedListener(this);
        mCurrentRules = mPreferencesUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        mRulesSpinner.setSelectedIndex(mCurrentRules);
        refreshUIByRules(mCurrentRules);
        PgyerUtil.setDialogStyle("#2196F3", "#FFFFFF");
    }

    @OnClick(R.id.setting_rules_detail) public void onRulesDetailClick(){
        //TODO
    }
    @OnClick(R.id.setting_update) public void onUpdateClick(){
        PgyerUtil.checkUpdate(mActivity, true);
    }
    @OnClick(R.id.setting_share) public void onShareClick(){
        IntentUtil.shareText(mActivity, getString(R.string.share_title),
                getString(R.string.share_subject), getString(R.string.share_content));
    }
    @OnClick(R.id.setting_feedback) public void onFeedbackClick(){
        checkRecordAudioPermissions();
    }
    @OnClick(R.id.setting_author) public void onAuthorClick(){
        if (null == mAuthorDialog) {
            mAuthorDialog = new AuthorDialog(mActivity);
        }
        mAuthorDialog.show();
    }
    @OnClick(R.id.setting_licenses) public void onThanksClick(){
        OpenSourceLicensesActivity.start(mActivity);
    }

    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
        if(position == mCurrentRules) return;
        mCurrentRules = position;
        refreshUIByRules(mCurrentRules);
        recalculate();
    }

    @BindView(R.id.setting_rules_default_layout) FrameLayout mDefaultRulesLayout;
    @BindView(R.id.setting_hourly_wage)          TextView    mDefaultRulesHourlyWage;
    private void refreshUIByRules(int rules){
        //TODO
        mDefaultRulesLayout.setVisibility(rules==ICalculationRules.RULES_DEFAULT ?
                                            View.VISIBLE : View.GONE);
        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:

                break;
            case ICalculationRules.RULES_FIXED_MONTH:

                break;
            case ICalculationRules.RULES_PIZZAHUT:

                break;
            case ICalculationRules.RULES_MONTHLY:

                break;
            case ICalculationRules.RULES_QUANTITY:

                break;
            case ICalculationRules.RULES_DEFAULT:
                mCurrentHourlyWage = MoneyUtil.replace(mPreferencesUtil.getStringValue(
                        Consts.SP_HOURLY_WAGE,
                        Consts.DEFAULT_HOURLY_WAGE));
                mDefaultRulesHourlyWage.setText(formatHourlyWage(mCurrentHourlyWage));
                break;
        }
    }

    private String mCurrentHourlyWage;
    @OnClick(R.id.setting_rules_default_layout) void onDefaultRulesLayoutClick(){
        //设置当前时薪
        displayInputDialog(R.string.setup_hourly_wage,
                Util.getString(mAppContext, R.string.setting_hourly_wage_label),
                mCurrentHourlyWage,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(TextUtils.isEmpty(input)){
                            ToastUtil.showToast(mAppContext, R.string.setup_hourly_wage_empty);
                        }else{
                            mDefaultRulesHourlyWage.setText(formatHourlyWage(input.toString()));
                            mPreferencesUtil.putStringValue(Consts.SP_HOURLY_WAGE, input.toString());
                            ToastUtil.showToast(mAppContext, R.string.setup_success);
                            recalculate();
                        }
                    }
                });
    }

    private void displayInputDialog(int titleResId, String inputHint, String inputDefaultValue,
                                   MaterialDialog.InputCallback callback){
        new MaterialDialog.Builder(mActivity)
                .title(titleResId)
                .titleColorRes(R.color.primary_text)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.secondary_text)
                .inputRange(1, 8)
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(inputHint, inputDefaultValue, false, callback)
                .show();
    }
    private String formatHourlyWage(String hourlyWage){
        return MoneyUtil.replace(hourlyWage) +
                Util.getString(mAppContext, R.string.hourly_wage_suffix);
    }
    private void recalculate(){
        ((MainActivity)mActivity).notifyCalculationRulesChange(mCurrentRules);
    }

    @Override public void onPause() {
        super.onPause();
        if(null != mAuthorDialog && mAuthorDialog.isShowing()){
            mAuthorDialog.dismiss();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_FEEDBACK)
    private void checkRecordAudioPermissions(){
        if (EasyPermissions.hasPermissions(mAppContext, FEEDBACK_PERMISSION)) {
            PgyerUtil.feedback(mActivity);
        } else {
            EasyPermissions.requestPermissions(this, Consts.FEEDBACK_PERMISSIONS_DESCRIBE,
                    REQUEST_CODE_FEEDBACK, FEEDBACK_PERMISSION);
        }
    }

    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if(requestCode == REQUEST_CODE_FEEDBACK){
            PgyerUtil.feedback(mActivity);
        }
    }

    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        if(requestCode == REQUEST_CODE_FEEDBACK){
            PgyerUtil.feedback(mActivity);
        }
    }
}
