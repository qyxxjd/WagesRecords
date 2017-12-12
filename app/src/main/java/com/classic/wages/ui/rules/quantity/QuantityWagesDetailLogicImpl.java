package com.classic.wages.ui.rules.quantity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.classic.android.rx.RxTransformer;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.pop.WagesDetailPopupWindow;
import com.classic.wages.ui.rules.base.BaseWagesDetailLogicImpl;
import com.classic.wages.utils.LogUtil;
import com.classic.wages.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：计件工资详情展现实现类
 * 创 建 人：续写经典
 * 创建时间：16/11/4 下午6:19
 */
public class QuantityWagesDetailLogicImpl extends BaseWagesDetailLogicImpl<QuantityInfo> {

    private WeakReference<Activity> mWeakReference;

    @Override protected List<String> convert(List<QuantityInfo> list) {
        return toList(QuantityUtils.getTotalWages(list));
    }

    @Override public void onDisplay(@NonNull final Activity activity, @NonNull final View targetView, final List<QuantityInfo> list) {
        mWeakReference = new WeakReference<>(activity);

        Observable.create(new ObservableOnSubscribe<List<List<String>>>() {
            @Override public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<List<List<String>>> e)
                    throws Exception {
                List<List<String>> data = new ArrayList<>();
                data.add(convert(list));
                data.add(QuantityUtils.getGroupDetail(list));
                e.onNext(data);
                e.onComplete();
            }
        }).compose(RxTransformer.<List<List<String>>>applySchedulers(RxTransformer.Observable.IO_ON_UI))
                .subscribe(new Observer<List<List<String>>>() {
                    Disposable mDisposable;
                    @Override public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override public void onNext(@io.reactivex.annotations.NonNull List<List<String>> lists) {
                        if (null != mWeakReference && mWeakReference.get() != null) {
                            new WagesDetailPopupWindow(activity, lists.get(0),
                                                       lists.size() > 1 ? lists.get(1) : null).show(targetView);
                        }
                    }

                    @Override public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Util.clear(mDisposable);
                        LogUtil.e(e.getMessage());
                    }

                    @Override public void onComplete() {
                        Util.clear(mDisposable);
                    }
                });
    }

    private List<String> toList(@NonNull QuantityWagesDetailEntity entity){
        List<String> items = new ArrayList<>();
        items.add(Util.formatWageDetail("计件数量", entity.totalQuantity));
        items.add(Util.formatWageDetail("计件工资", entity.totalNormalWages));
        items.add(Util.formatWageDetail("奖金", entity.totalBonus));
        items.add(Util.formatWageDetail("补贴", entity.totalSubsidy));
        items.add(Util.formatWageDetail("扣款", entity.totalDeductions));
        items.add(Util.formatWageDetail("总工资", entity.totalWages));
        return items;
    }
}
