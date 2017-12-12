package com.classic.wages.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.android.consts.MIME;
import com.classic.android.rx.RxTransformer;
import com.classic.android.utils.SDCardUtil;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.activity.MainActivity;
import com.classic.wages.ui.activity.OpenSourceLicensesActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.dialog.AuthorDialog;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.ISettingLogic;
import com.classic.wages.ui.rules.basic.DefaultSettingLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDaySettingLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthSettingLogicImpl;
import com.classic.wages.ui.rules.kfc.KFCSettingLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutSettingLogicImpl;
import com.classic.wages.utils.ToastUtil;
import com.classic.wages.utils.UriUtil;
import com.classic.wages.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tencent.bugly.beta.Beta;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：设置页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class SettingFragment extends AppBaseFragment implements MaterialSpinner.OnItemSelectedListener<String>{
    private static final int FILE_CHOOSER_CODE      = 1001;

    @BindView(R.id.setting_rules_spinner) MaterialSpinner mRulesSpinner;
    @BindView(R.id.setting_rules_content) View            mRulesContentView;
    @BindView(R.id.setting_cycle_value)   TextView        mCycleValue;

    @Inject WorkInfoDao     mWorkInfoDao;
    @Inject MonthlyInfoDao  mMonthlyInfoDao;
    @Inject QuantityInfoDao mQuantityInfoDao;

    private int           mRulesType;
    private AuthorDialog  mAuthorDialog;

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
        resetCycleValue(null);
    }

    private void resetCycleValue(String value) {
        if (Util.isEmpty(value)) {
            value = Util.getPreferencesString(Consts.SP_CYCLE_VALUE, Consts.DEFAULT_CYCLE);
        } else {
            Util.putPreferencesString(Consts.SP_CYCLE_VALUE, value);
            // 通知其它页面刷新数据
            ((MainActivity)mActivity).notifyRecalculation();
        }
        mCycleValue.setText(getString(R.string.setting_cycle_format, value));
    }

    @OnClick(R.id.setting_cycle_value) public void onCycleClick() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.setting_cycle_title)
                .titleColorRes(R.color.primary_text)
                .items(Consts.DAYS)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        resetCycleValue(charSequence.toString());
                    }
                })
                .backgroundColorRes(R.color.white)
                .content(R.string.setting_cycle_desc)
                .contentColorRes(R.color.secondary_text)
                .show();
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
        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append(mAppContext.getResources().getString(R.string.setting_rules_detail))
                .append("  v")
                .append(Util.getVersionName(mAppContext))
                .toString();
    }

    @OnClick(R.id.setting_backup) public void onBackup() {
        final File file = new File(SDCardUtil.getFileDirPath(), Util.createBackupFileName());
        if (!file.exists()) {
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
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override public void subscribe(@io.reactivex.annotations.NonNull final ObservableEmitter<Boolean> emitter)
                    throws Exception {
                boolean isMonthlySuccess = mMonthlyInfoDao.backup(file);
                boolean isQuantitySuccess = mQuantityInfoDao.backup(file);
                boolean isWorkInfoSuccess = mWorkInfoDao.backup(file);
                SystemClock.sleep(1000);
                emitter.onNext(isMonthlySuccess && isQuantitySuccess && isWorkInfoSuccess);
                emitter.onComplete();
            }
        }).compose(RxTransformer.<Boolean>applySchedulers(RxTransformer.Observable.IO_ON_UI)).subscribe(new Observer<Boolean>() {
            Disposable mDisposable;
            @Override public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                ToastUtil.showLongToast(mAppContext, getString(R.string.data_backup_success, file.getAbsolutePath()));
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("*/txt");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, file);
                startActivity(Intent.createChooser(sharingIntent, "share file with"));
            }

            @Override public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                dialog.dismiss();
                ToastUtil.showToast(mAppContext, R.string.data_backup_failure);
                Util.clear(mDisposable);
            }

            @Override public void onComplete() {
                dialog.dismiss();
                Util.clear(mDisposable);
            }
        });
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

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Boolean> emitter)
                    throws Exception {
                boolean isMonthlySuccess = mMonthlyInfoDao.restore(file);
                boolean isQuantitySuccess = mQuantityInfoDao.restore(file);
                boolean isWorkInfoSuccess = mWorkInfoDao.restore(file);
                SystemClock.sleep(1000);
                emitter.onNext(isMonthlySuccess && isQuantitySuccess && isWorkInfoSuccess);
                emitter.onComplete();
            }
        }).compose(RxTransformer.<Boolean>applySchedulers(RxTransformer.Observable.IO_ON_UI)).subscribe(new Observer<Boolean>() {
            Disposable mDisposable;
            @Override public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                ToastUtil.showToast(mAppContext, R.string.data_restore_success);
            }

            @Override public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                dialog.dismiss();
                ToastUtil.showToast(mAppContext, R.string.data_restore_failure);
                Util.clear(mDisposable);
            }

            @Override public void onComplete() {
                dialog.dismiss();
                Util.clear(mDisposable);
            }
        });
    }

    @OnClick(R.id.setting_restore) public void onRestore() {
        Util.showFileChooser(this, MIME.FILE,
                Util.getString(mAppContext, R.string.select_backup_file_hint), FILE_CHOOSER_CODE,
                Util.getString(mAppContext, R.string.not_found_file_manager_hint));
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == FILE_CHOOSER_CODE) {
            String path = UriUtil.toAbsolutePath(mAppContext, data.getData());
            if(!Util.isEmpty(path) && path.endsWith(Consts.BACKUP_SUFFIX)) {
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
        Beta.checkUpgrade(true,false);
    }

    @OnClick(R.id.setting_share) public void onShareClick() {
        Util.shareText(mActivity, getString(R.string.share_title), getString(R.string.share_subject),
                       getString(R.string.share_content));
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
        ISettingLogic settingLogic;
        switch (rules) {
            case ICalculationRules.RULES_FIXED_DAY:
                settingLogic = new FixedDaySettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                settingLogic = new FixedMonthSettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                settingLogic = new PizzaHutSettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_DEFAULT:
                settingLogic = new DefaultSettingLogicImpl(mActivity, mRulesContentView);
                break;
            case ICalculationRules.RULES_KFC:
                settingLogic = new KFCSettingLogicImpl(mActivity, mRulesContentView);
                break;
            default:
                mRulesContentView.setVisibility(View.GONE);
                return;
        }
        //noinspection ConstantConditions
        if (null != settingLogic) {
            settingLogic.setupRulesContent();
        }
    }

    @Override public void onPause() {
        super.onPause();
        if(null != mAuthorDialog && mAuthorDialog.isShowing()){
            mAuthorDialog.dismiss();
        }
    }
}
