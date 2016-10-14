package com.classic.wages.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.core.utils.DoubleClickExitHelper;
import com.classic.wages.app.AppBaseActivity;
import com.classic.wages.ui.fragment.ListFragment;
import com.classic.wages.ui.fragment.MainFragment;
import com.classic.wages.ui.fragment.QueryFragment;
import com.classic.wages.ui.fragment.SettingFragment;
import com.classic.wages.utils.PgyerUtil;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import java.util.ArrayList;

public class MainActivity extends AppBaseActivity {
    private static final int TAB_MAIN    = 0;
    private static final int TAB_LIST    = 1;
    private static final int TAB_QUERY   = 2;
    private static final int TAB_SETTING = 3;

    @BindView(R.id.main_ntb) NavigationTabBar navigationTabBar;

    private MainFragment    mMainFragment;
    private ListFragment    mListFragment;
    private QueryFragment   mQueryFragment;
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
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main), color)
                        .title("首页")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_list), color)
                        .title("列表")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_query), color)
                        .title("查询")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_about), color)
                        .title("关于")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                //ToastUtil.showToast(getApplicationContext(), "onEndTabSelected:"+index);
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
                    case TAB_QUERY:
                        if (null == mQueryFragment) {
                            mQueryFragment = QueryFragment.newInstance();
                        }
                        changeFragment(R.id.main_content, mQueryFragment);
                        break;
                    case TAB_SETTING:
                        if (null == mSettingFragment) {
                            mSettingFragment = SettingFragment.newInstance();
                        }
                        changeFragment(R.id.main_content, mSettingFragment);
                        break;
                }
            }
        });
        navigationTabBar.setModelIndex(TAB_MAIN);
    }

    @Override public void unRegister() {
        super.unRegister();
        PgyerUtil.destroy();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    }

}
