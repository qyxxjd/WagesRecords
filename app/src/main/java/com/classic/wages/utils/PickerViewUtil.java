package com.classic.wages.utils;

import android.app.Activity;

import com.bigkoo.pickerview.TimePickerView;

import java.util.Calendar;

public final class PickerViewUtil {

    public static TimePickerView createWithHourAndMinute(Activity activity, TimePickerView.OnTimeSelectListener listener) {
        boolean[] type = {false, false, false, true, true, false};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return new TimePickerView.Builder(activity, listener).isCyclic(false)
                                                             .setOutSideCancelable(false)
                                                             .setType(type)
                                                             .setDate(calendar)
                                                             .build();
    }


}
