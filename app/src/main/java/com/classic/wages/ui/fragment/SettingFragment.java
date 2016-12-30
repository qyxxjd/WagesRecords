package com.classic.wages.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.android.permissions.AfterPermissionGranted;
import com.classic.android.permissions.EasyPermissions;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.activity.OpenSourceLicensesActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.dialog.AuthorDialog;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.ISettingLogic;
import com.classic.wages.ui.rules.basic.DefaultSettingLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDaySettingLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthSettingLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutSettingLogicImpl;
import com.classic.wages.utils.PgyUtil;
import com.classic.wages.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;

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
    @BindView(R.id.setting_rules_content) View            mRulesContentView;

    private int           mRulesType;
    private AuthorDialog  mAuthorDialog;
    private ISettingLogic mSettingLogic;

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
        mRulesType = Util.getPreferencesInt(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        mRulesSpinner.setSelectedIndex(mRulesType);
        refreshUIByRules(mRulesType);
        PgyUtil.setDialogStyle("#2196F3", "#FFFFFF");
    }

    @OnClick(R.id.setting_rules_detail) public void onRulesDetailClick(){
        new MaterialDialog.Builder(mActivity)
                .title(getRulesDetailTitle())
                .titleColorRes(R.color.primary_text)
                .backgroundColorRes(R.color.white)
                .content(Util.getString(mAppContext, R.string.setting_rules_description))
                .contentColorRes(R.color.secondary_text)
                .positiveText(R.string.confirm)
                .show();
    }
    private String getRulesDetailTitle(){
        return new StringBuilder()
                .append(mAppContext.getResources().getString(R.string.setting_rules_detail))
                .append("  v")
                .append(getVersionName(mAppContext))
                .toString();
    }

    @OnClick(R.id.setting_update) public void onUpdateClick(){
        PgyUtil.checkUpdate(mActivity, true);
    }
    @OnClick(R.id.setting_share) public void onShareClick(){
        shareText(mActivity, getString(R.string.share_title),
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
        if(position == mRulesType) return;
        mRulesType = position;
        Util.putPreferencesInt(Consts.SP_RULES_TYPE, mRulesType);
        refreshUIByRules(mRulesType);
    }

    private void refreshUIByRules(int rules){
        switch (rules) {
            case ICalculationRules.RULES_FIXED_DAY:
                mSettingLogic = new FixedDaySettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                mSettingLogic = new FixedMonthSettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                mSettingLogic = new PizzaHutSettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_DEFAULT:
                mSettingLogic = new DefaultSettingLogicImpl(mActivity, mRulesContentView);
                break;
            default:
                mRulesContentView.setVisibility(View.GONE);
                return;
        }
        mSettingLogic.setupRulesContent();
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
            PgyUtil.feedback(mActivity);
        } else {
            EasyPermissions.requestPermissions(this, Consts.FEEDBACK_PERMISSIONS_DESCRIBE,
                    REQUEST_CODE_FEEDBACK, FEEDBACK_PERMISSION);
        }
    }

    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if(requestCode == REQUEST_CODE_FEEDBACK){
            PgyUtil.feedback(mActivity);
        }
    }

    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        if(requestCode == REQUEST_CODE_FEEDBACK){
            PgyUtil.feedback(mActivity);
        }
    }

    private void shareText(@NonNull Context context, @NonNull String title, @NonNull String subject,
                           @NonNull String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, title));
    }

    private String getVersionName(@NonNull Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (null != info) {
                return info.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
