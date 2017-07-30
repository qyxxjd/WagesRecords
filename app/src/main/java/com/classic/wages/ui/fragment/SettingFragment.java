package com.classic.wages.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.android.consts.MIME;
import com.classic.android.permissions.AfterPermissionGranted;
import com.classic.android.permissions.EasyPermissions;
import com.classic.android.utils.SDCardUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.activity.OpenSourceLicensesActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.dialog.AuthorDialog;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.ISettingLogic;
import com.classic.wages.ui.rules.basic.DefaultSettingLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDaySettingLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthSettingLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutSettingLogicImpl;
import com.classic.wages.utils.ToastUtil;
import com.classic.wages.utils.UriUtil;
import com.classic.wages.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tencent.bugly.beta.Beta;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

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
    private static final int FILE_CHOOSER_CODE      = 1001;

    @BindView(R.id.setting_rules_spinner) MaterialSpinner mRulesSpinner;
    @BindView(R.id.setting_rules_content) View            mRulesContentView;

    @Inject WorkInfoDao     mWorkInfoDao;
    @Inject MonthlyInfoDao  mMonthlyInfoDao;
    @Inject QuantityInfoDao mQuantityInfoDao;

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
//        PgyUtil.setDialogStyle("#2196F3", "#FFFFFF");
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

    @OnClick(R.id.setting_backup) public void onBackup() {
        final File file = new File(SDCardUtil.getFileDirPath(), createBackupFileName());
        if(!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .cancelable(false)
                .title(R.string.setting_backup)
                .titleColorRes(R.color.primary_text)
                .backgroundColorRes(R.color.white)
                .content(R.string.setting_backup_hint)
                .contentColorRes(R.color.secondary_text)
                .progress(true, 0)
                .show();
        // TODO: 2017/7/11
        // Observable.create(new Observable.OnSubscribe<Boolean>() {
        //     @Override public void call(final Subscriber<? super Boolean> subscriber) {
        //         final IBackup.Listener listener = new IBackup.Listener() {
        //             @Override public void onComplete() { }
        //
        //             @Override public void onError(Throwable throwable) {
        //                 subscriber.onError(throwable);
        //             }
        //
        //             @Override public void onProgress(long currentCount, long totalCount) { }
        //         };
        //         mMonthlyInfoDao.backup(file, listener);
        //         mQuantityInfoDao.backup(file, listener);
        //         mWorkInfoDao.backup(file, listener);
        //         SystemClock.sleep(1000);
        //         subscriber.onNext(true);
        //     }
        // }).compose(RxUtil.<Boolean>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
        //           .subscribe(new Action1<Boolean>() {
        //     @Override public void call(Boolean aBoolean) {
        //         dialog.dismiss();
        //         ToastUtil.showLongToast(mAppContext,
        //                 getString(R.string.data_backup_success, file.getAbsolutePath()));
        //         //打开文件夹
        //         //Util.showDirectory(mActivity, file.getParentFile().getAbsolutePath(), MIME.FILE,
        //         //        Util.getString(mAppContext, R.string.setting_backup_directory));
        //     }
        // }, new Action1<Throwable>() {
        //     @Override public void call(Throwable throwable) {
        //         dialog.dismiss();
        //         ToastUtil.showToast(mAppContext, R.string.data_backup_failure);
        //     }
        // });
    }

    private void restore(@NonNull String path) {
        final File file = new File(path);
        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .cancelable(false)
                .title(R.string.setting_restore)
                .titleColorRes(R.color.primary_text)
                .backgroundColorRes(R.color.white)
                .content(R.string.setting_restore_hint)
                .contentColorRes(R.color.secondary_text)
                .progress(true, 0)
                .show();
        // TODO: 2017/7/11  
        // Observable.create(new Observable.OnSubscribe<Boolean>() {
        //     @Override public void call(final Subscriber<? super Boolean> subscriber) {
        //         final IBackup.Listener listener = new IBackup.Listener() {
        //             @Override public void onComplete() { }
        //
        //             @Override public void onError(Throwable throwable) {
        //                 subscriber.onError(throwable);
        //             }
        //
        //             @Override public void onProgress(long currentCount, long totalCount) { }
        //         };
        //         mMonthlyInfoDao.restore(file, listener);
        //         mQuantityInfoDao.restore(file, listener);
        //         mWorkInfoDao.restore(file, listener);
        //         SystemClock.sleep(1000);
        //         subscriber.onNext(true);
        //     }
        // }).compose(RxUtil.<Boolean>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
        //           .subscribe(new Action1<Boolean>() {
        //               @Override public void call(Boolean aBoolean) {
        //                   dialog.dismiss();
        //                   ToastUtil.showToast(mAppContext, R.string.data_restore_success);
        //               }
        //           }, new Action1<Throwable>() {
        //               @Override public void call(Throwable throwable) {
        //                   dialog.dismiss();
        //                   ToastUtil.showToast(mAppContext, R.string.data_restore_failure);
        //               }
        //           });
    }

    @OnClick(R.id.setting_restore) public void onRestore() {
        Util.showFileChooser(this, MIME.FILE,
                Util.getString(mAppContext, R.string.select_backup_file_hint), FILE_CHOOSER_CODE,
                Util.getString(mAppContext, R.string.not_found_file_manager_hint));
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == FILE_CHOOSER_CODE) {
            String path = UriUtil.toAbsolutePath(mAppContext, data.getData());
            if(!TextUtils.isEmpty(path) && path.endsWith(Consts.BACKUP_SUFFIX)) {
                //ToastUtil.showToast(mAppContext, "select file："+path);
                restore(path);
            } else {
                ToastUtil.showToast(mAppContext, R.string.invalid_backup_file);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.setting_update) public void onUpdateClick(){
//        PgyUtil.checkUpdate(mActivity, true);
        Beta.checkUpgrade(true,false);
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
//            PgyUtil.feedback(mActivity);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permissions_feedback_describe),
                    REQUEST_CODE_FEEDBACK, FEEDBACK_PERMISSION);
        }
    }

    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if(requestCode == REQUEST_CODE_FEEDBACK){
//            PgyUtil.feedback(mActivity);
        }
    }

    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        if(requestCode == REQUEST_CODE_FEEDBACK){
//            PgyUtil.feedback(mActivity);
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

    private String createBackupFileName() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(Consts.BACKUP_PREFIX)
                .append("_")
                .append(sdf.format(new Date(System.currentTimeMillis())))
                .append(Consts.BACKUP_SUFFIX)
                .toString();
    }
}
