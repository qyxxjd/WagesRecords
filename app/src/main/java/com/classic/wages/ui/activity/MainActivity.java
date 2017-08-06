package com.classic.wages.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.classic.android.BasicProject;
import com.classic.android.permissions.AfterPermissionGranted;
import com.classic.android.permissions.AppSettingsDialog;
import com.classic.android.permissions.EasyPermissions;
import com.classic.android.utils.DoubleClickExitHelper;
import com.classic.wages.consts.Consts;
import com.classic.wages.consts.PrivateConsts;
import com.classic.wages.ui.base.AppBaseActivity;
import com.classic.wages.ui.fragment.ListFragment;
import com.classic.wages.ui.fragment.MainFragment;
import com.classic.wages.ui.fragment.SettingFragment;
import com.classic.wages.utils.BottomNavigationViewHelper;
import com.elvishew.xlog.LogLevel;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.util.List;

import butterknife.BindView;
import cn.qy.util.activity.BuildConfig;
import cn.qy.util.activity.R;


public class MainActivity extends AppBaseActivity {
    private static final int      REQUEST_CODE_STORAGE  = 101;
    private static final int      REQUEST_CODE_SETTINGS = 102;
    private static final String[] STORAGE_PERMISSIONS   = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };

    @BindView(R.id.main_bnv) BottomNavigationView mBottomNavigationView;

    private MainFragment          mMainFragment;
    private ListFragment          mListFragment;
    private SettingFragment       mSettingFragment;
    private DoubleClickExitHelper mDoubleClickExitHelper;
    private volatile boolean      isBottomNavigationViewHide;

    @Override public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle(R.string.app_name);
        mDoubleClickExitHelper = new DoubleClickExitHelper(mActivity);
        checkStoragePermissions();
        initTabBar(savedInstanceState);
    }

    @AfterPermissionGranted(REQUEST_CODE_STORAGE) private void checkStoragePermissions() {
        if (EasyPermissions.hasPermissions(this, STORAGE_PERMISSIONS)) {
            init();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permissions_storage_describe),
                    REQUEST_CODE_STORAGE, STORAGE_PERMISSIONS);
        }
    }

    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if (requestCode == REQUEST_CODE_STORAGE) {
            init();
        }
    }

    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRationale(R.string.permissions_storage_describe)
                    .setTitle(R.string.permissions_title)
                    .setRequestCode(REQUEST_CODE_SETTINGS)
                    .setPositiveButton(R.string.settings)
                    .setNegativeButton(R.string.cancel)
                    .build()
                    .show();
        }
    }

    private void init() {
        BasicProject.config(new BasicProject.Builder()
                                    .setDebug(BuildConfig.DEBUG)
                                    .setRootDirectoryName(Consts.DIR_NAME)
                                    .setExceptionHandler(mAppContext)
                                    .setLog(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE));
        initBugly();
    }

    private void initBugly() {
        // 升级检查周期设置, 60s内SDK不重复向后台请求策略, 默认为0s
        Beta.upgradeCheckPeriod = 60 * 1000;
        // 设置通知栏大图标
        // Beta.largeIconId = R.drawable.ic_launcher;
        // 设置状态栏小图标
        // Beta.smallIconId = R.drawable.ic_launcher;
        // 关闭热更新能力
        Beta.enableHotfix = false;
        Bugly.init(getApplicationContext(), PrivateConsts.BUGLY_APP_ID, BuildConfig.DEBUG);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (null == mMainFragment) {
                        mMainFragment = MainFragment.newInstance();
                    }
                    changeFragment(R.id.main_content, mMainFragment);
                    return true;
                case R.id.navigation_list:
                    if (null == mListFragment) {
                        mListFragment = ListFragment.newInstance();
                    }
                    changeFragment(R.id.main_content, mListFragment);
                    return true;
                case R.id.navigation_settings:
                    if (null == mSettingFragment) {
                        mSettingFragment = SettingFragment.newInstance();
                    }
                    changeFragment(R.id.main_content, mSettingFragment);
                    return true;
            }
            return false;
        }
    };
    private void initTabBar(Bundle savedInstanceState) {
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.enableShiftMode(mBottomNavigationView, true);
        if (null == savedInstanceState) {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    }

    public void notifyRecalculation() {
        if(null != mMainFragment){
            mMainFragment.onRecalculation();
        }
        if(null != mListFragment){
            mListFragment.onRecalculation();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onHide() {
        if (!isBottomNavigationViewHide) {
            isBottomNavigationViewHide = true;
            mBottomNavigationView.animate()
                                 .translationY(mBottomNavigationView.getHeight())
                                 .setInterpolator(new AccelerateInterpolator(2));
        }
    }

    public void onShow() {
        if (isBottomNavigationViewHide) {
            isBottomNavigationViewHide = false;
            mBottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }
}
