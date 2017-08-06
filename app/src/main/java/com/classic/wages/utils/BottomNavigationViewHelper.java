package com.classic.wages.utils;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.utils
 * <p>
 * 文件描述: BottomNavigationView工具类
 * 创 建 人: 续写经典
 * 创建时间: 2017/7/30 下午6:51
 */
public final class BottomNavigationViewHelper {

    private static final String TAG = BottomNavigationViewHelper.class.getSimpleName();

    public static void enableShiftMode(BottomNavigationView view, boolean enable) {
        // TODO Android O(8.0)以下可使用下面的方法，进行反射处理。以后的版本需要查看源码，进行适配处理
        if (android.os.Build.VERSION.SDK_INT > 26) {
            return;
        }
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, enable);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(enable);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Unable to change value of shift mode", e);
        }
    }
}
