package com.classic.wages.ui.activity;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import butterknife.BindView;
import cn.qy.util.activity.BuildConfig;
import cn.qy.util.activity.R;
import com.classic.core.BasicConfig;
import com.classic.core.permissions.AfterPermissionGranted;
import com.classic.core.permissions.AppSettingsDialog;
import com.classic.core.permissions.EasyPermissions;
import com.classic.core.utils.DoubleClickExitHelper;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.base.AppBaseActivity;
import com.classic.wages.ui.fragment.ListFragment;
import com.classic.wages.ui.fragment.MainFragment;
import com.classic.wages.ui.fragment.SettingFragment;
import com.classic.wages.utils.PgyerUtil;
import com.classic.wages.utils.Util;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppBaseActivity {
    private static final int    TAB_MAIN      = 0;
    private static final int    TAB_LIST      = 1;
    private static final int    TAB_SETTING   = 2;
    private static final String TITLE_MAIN    = "首页";
    private static final String TITLE_LIST    = "列表";
    private static final String TITLE_SETTING = "设置";

    private static final int      REQUEST_CODE_STORAGE  = 101;
    private static final int      REQUEST_CODE_SETTINGS = 102;
    private static final String[] STORAGE_PERMISSIONS   = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };

    @BindView(R.id.main_ntb) NavigationTabBar navigationTabBar;

    private MainFragment          mMainFragment;
    private ListFragment          mListFragment;
    private SettingFragment       mSettingFragment;
    private DoubleClickExitHelper mDoubleClickExitHelper;

    @Override public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle(R.string.app_name);
        mDoubleClickExitHelper = new DoubleClickExitHelper(mActivity);
        checkStoragePermissions();
        initTabBar(savedInstanceState);
        PgyerUtil.register(mAppContext);
        PgyerUtil.checkUpdate(mActivity, false);
    }

    @AfterPermissionGranted(REQUEST_CODE_STORAGE) private void checkStoragePermissions() {
        if (EasyPermissions.hasPermissions(this, STORAGE_PERMISSIONS)) {
            init();
        } else {
            EasyPermissions.requestPermissions(this, Consts.STORAGE_PERMISSIONS_DESCRIBE,
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
            new AppSettingsDialog.Builder(this, Consts.STORAGE_PERMISSIONS_DESCRIBE)
                                 .setTitle(Consts.APPLY_FOR_PERMISSIONS)
                                 .setPositiveButton(Consts.SETUP)
                                 .setNegativeButton(Consts.CANCEL, null)
                                 .setRequestCode(REQUEST_CODE_SETTINGS)
                                 .build()
                                 .show();
        }
    }

    private void init() {
        if (BuildConfig.DEBUG) {
            BasicConfig.getInstance(mAppContext).initDir().initLog(true);
        } else {
            BasicConfig.getInstance(mAppContext).init();
        }
    }

    private void initTabBar(Bundle savedInstanceState) {
        final int color = Color.parseColor("#FF4081");
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model.Builder(Util.getDrawable(mAppContext, R.drawable.ic_main), color)
                           .title(TITLE_MAIN).build());
        models.add(new NavigationTabBar.Model.Builder(Util.getDrawable(mAppContext, R.drawable.ic_list), color)
                           .title(TITLE_LIST).build());
        models.add(new NavigationTabBar.Model.Builder(Util.getDrawable(mAppContext, R.drawable.ic_setting), color)
                           .title(TITLE_SETTING).build());

        navigationTabBar.setModels(models);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnTabBarSelectedIndexListener(
                new NavigationTabBar.OnTabBarSelectedIndexListener() {
                    @Override
                    public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

                    }

                    @Override
                    public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                        onTabSelected(index);
                    }
                });
        if(null == savedInstanceState){
            navigationTabBar.setModelIndex(TAB_MAIN);
        }
    }

    @Override public void unRegister() {
        super.unRegister();
        PgyerUtil.destroy();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    }

    private void onTabSelected(int index) {
        switch (index) {
            case TAB_MAIN:
                if (null == mMainFragment) {
                    mMainFragment = MainFragment.newInstance();
                }
                changeFragment(R.id.main_content, mMainFragment);
                break;
            case TAB_LIST:
                if (null == mListFragment) {
                    mListFragment = ListFragment.newInstance();
                }
                changeFragment(R.id.main_content, mListFragment);
                break;
            case TAB_SETTING:
                if (null == mSettingFragment) {
                    mSettingFragment = SettingFragment.newInstance();
                }
                changeFragment(R.id.main_content, mSettingFragment);
                break;
        }
    }
}
