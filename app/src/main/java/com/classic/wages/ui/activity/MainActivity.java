package com.classic.wages.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.KeyEvent;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.core.utils.DoubleClickExitHelper;
import com.classic.wages.ui.base.AppBaseActivity;
import com.classic.wages.ui.fragment.ListFragment;
import com.classic.wages.ui.fragment.MainFragment;
import com.classic.wages.ui.fragment.SettingFragment;
import com.classic.wages.utils.PgyerUtil;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import java.util.ArrayList;

public class MainActivity extends AppBaseActivity {
    private static final int    TAB_MAIN      = 0;
    private static final int    TAB_LIST      = 1;
    private static final int    TAB_SETTING   = 2;
    private static final String TITLE_MAIN    = "首页";
    private static final String TITLE_LIST    = "列表";
    private static final String TITLE_SETTING = "设置";

    @BindView(R.id.main_ntb) NavigationTabBar navigationTabBar;

    private MainFragment    mMainFragment;
    private ListFragment    mListFragment;
    private SettingFragment mSettingFragment;

    private DoubleClickExitHelper mDoubleClickExitHelper;

    @Override public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle(R.string.app_name);
        mDoubleClickExitHelper = new DoubleClickExitHelper(mActivity);

        initTabBar();
        PgyerUtil.register(mAppContext);
        PgyerUtil.checkUpdate(mActivity, false);
    }

    //private void init(){
    //    if(BuildConfig.DEBUG){
    //        BasicConfig.getInstance(mAppContext).initDir().initLog(true);
    //    }else {
    //        BasicConfig.getInstance(mAppContext).init();
    //    }
    //    MobclickAgent.onProfileSignIn(DeviceUtil.getInstance(mAppContext).getID());
    //}

    private void initTabBar(){
        final int color = Color.parseColor("#FF4081");
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model.Builder(
                        getIcon(R.drawable.ic_main), color)
                        .title(TITLE_MAIN)
                        .build());
        models.add(new NavigationTabBar.Model.Builder(
                        getIcon(R.drawable.ic_list), color)
                        .title(TITLE_LIST)
                        .build());
        models.add(new NavigationTabBar.Model.Builder(
                        getIcon(R.drawable.ic_setting), color)
                        .title(TITLE_SETTING)
                        .build());

        navigationTabBar.setModels(models);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                onTabSelected(index);
            }
        });
        navigationTabBar.setModelIndex(TAB_MAIN);
    }

    private Drawable getIcon(@DrawableRes int resId){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(resId, getTheme());
        }
        return getDrawable(resId);
    }

    @Override public void unRegister() {
        super.unRegister();
        PgyerUtil.destroy();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    }

    private void onTabSelected(int index){
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
