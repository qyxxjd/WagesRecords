package com.classic.wages.ui.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
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
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppBaseActivity {

    @BindView(R.id.main_bottombar) BottomBar mBottomBar;

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

        initBottomBar();
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

    private void initBottomBar(){
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_main:
                        if (null == mMainFragment) {
                            mMainFragment = MainFragment.newInstance();
                        }
                        changeFragment(R.id.main_content, mMainFragment);
                        break;
                    case R.id.tab_list:
                        if (null == mListFragment) {
                            mListFragment = ListFragment.newInstance();
                        }
                        changeFragment(R.id.main_content, mListFragment);
                        break;
                    case R.id.tab_query:
                        if (null == mQueryFragment) {
                            mQueryFragment = QueryFragment.newInstance();
                        }
                        changeFragment(R.id.main_content, mQueryFragment);
                        break;
                    case R.id.tab_setting:
                        if (null == mSettingFragment) {
                            mSettingFragment = SettingFragment.newInstance();
                        }
                        changeFragment(R.id.main_content, mSettingFragment);
                        break;
                }
            }
        });
    }

    @Override public void unRegister() {
        super.unRegister();
        PgyerUtil.destroy();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    }

}
