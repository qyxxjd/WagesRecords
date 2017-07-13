package com.classic.wages.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.classic.android.base.RxFragment;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.rules.ICalculationRules;

import java.util.Calendar;

import butterknife.ButterKnife;

public abstract class AppBaseFragment extends RxFragment implements ICalculationRules {

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        ButterKnife.bind(this, parentView);
        mAppContext = mActivity.getApplicationContext();
    }

    @Override public void onCalculationRulesChange(int rules) { }

    @Override public void onRecalculation() { }

    protected TimePickerView createTimePickerView(Activity activity, TimePickerView.OnTimeSelectListener listener,
                                                  long time) {
        return createPickerView(activity, listener, time, true);
    }

    protected TimePickerView createPickerView(Activity activity, TimePickerView.OnTimeSelectListener listener,
                                              long time, boolean useTime) {
        boolean[] type = useTime ? new boolean[]{true, true, true, true, true, true} :
                new boolean[]{true, true, true, false, false, false};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return new TimePickerView.Builder(activity, listener).isCyclic(false)
                                                             .setOutSideCancelable(false)
                                                             .setRange(Consts.MIN_YEAR, Consts.MAX_YEAR)
                                                             .setType(type)
                                                             .setDate(calendar)
                                                             .build();
    }
}
