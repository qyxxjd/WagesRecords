package com.classic.wages.ui.rules.base;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.pop.WagesDetailPopupWindow;
import com.classic.wages.ui.rules.IWagesDetailLogic;
import com.classic.wages.utils.RxUtil;
import java.lang.ref.WeakReference;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述: TODO
 * 创 建 人: 刘宾
 * 创建时间: 2016/11/3 12:58
 */
public abstract class BaseWagesDetailLogicImpl<T extends BasicInfo> implements IWagesDetailLogic<T> {

    private WeakReference<Activity> mWeakReference;

    protected abstract List<String> convert(List<T> list);

    @Override public void onDisplay(@NonNull final Activity activity, @NonNull final View targetView, final List<T> list) {
        mWeakReference = new WeakReference<>(activity);
        Observable.just(convert(list))
                  .compose(RxUtil.<List<String>>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                  .subscribe(new Action1<List<String>>() {
                      @Override public void call(List<String> items) {
                          if(null != mWeakReference && mWeakReference.get() != null){
                            new WagesDetailPopupWindow(activity, items).show(targetView);
                          }
                      }
                  }, RxUtil.ERROR_ACTION);
    }

}
