package com.classic.wages.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.classic.android.base.RxActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.qy.util.activity.R;

public abstract class AppBaseActivity extends RxActivity {
    @BindView(R.id.toolbar) Toolbar mToolbar;

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override public void initView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        mAppContext = getApplicationContext();
        if (mToolbar == null) {
            throw new IllegalStateException("No Toolbar");
        }

        setSupportActionBar(mToolbar);

        if (canBack()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected boolean canBack() {
        return false;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
