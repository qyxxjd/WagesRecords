package com.classic.wages.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.core.permissions.AfterPermissionGranted;
import com.classic.core.permissions.EasyPermissions;
import com.classic.core.utils.IntentUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.activity.OpenSourceLicensesActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.dialog.AuthorDialog;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IRulesContentViewDisplay;
import com.classic.wages.ui.rules.basic.DefaultRulesContentViewDisplay;
import com.classic.wages.utils.PgyerUtil;
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

    @Inject SharedPreferencesUtil mSpUtil;

    private int          mRulesType;
    private AuthorDialog mAuthorDialog;
    private IRulesContentViewDisplay mRulesContent;

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
        mRulesType = mSpUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        mRulesSpinner.setSelectedIndex(mRulesType);
        refreshUIByRules(mRulesType);
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
        if(position == mRulesType) return;
        mRulesType = position;
        mSpUtil.putStringValue(Consts.SP_RULES_TYPE, String.valueOf(mRulesType));
        refreshUIByRules(mRulesType);
    }

    private void refreshUIByRules(int rules){
        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:

                break;
            case ICalculationRules.RULES_FIXED_MONTH:

                break;
            case ICalculationRules.RULES_PIZZAHUT:

                break;
            case ICalculationRules.RULES_DEFAULT:
                mRulesContent = new DefaultRulesContentViewDisplay(mActivity, mSpUtil);
                break;
        }
        mRulesContent.setupRulesContent();
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
