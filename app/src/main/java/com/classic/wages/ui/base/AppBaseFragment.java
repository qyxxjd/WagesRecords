package com.classic.wages.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.classic.android.base.BaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class AppBaseFragment extends BaseFragment implements ICalculationRules {
    protected Context               mAppContext;
    private   CompositeSubscription mCompositeSubscription;

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        ButterKnife.bind(this, parentView);
        mAppContext = mActivity.getApplicationContext();
    }

    @Override public void unRegister() {
        if (null != mCompositeSubscription) {
            mCompositeSubscription.unsubscribe();
        }
    }

    protected void addSubscription(Subscription subscription) {
        if (null == mCompositeSubscription) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override public void onCalculationRulesChange(int rules) { }

    @Override public void onRecalculation() { }
}
