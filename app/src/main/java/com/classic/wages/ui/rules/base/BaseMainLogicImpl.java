package com.classic.wages.ui.rules.base;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.classic.android.rx.RxUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.ui.rules.IMainLogic;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.LogUtil;
import com.classic.wages.utils.MoneyUtil;
import com.classic.wages.utils.Util;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public abstract class BaseMainLogicImpl<T> implements IMainLogic {

    protected long    mMonthStartTime;
    protected long    mMonthEndTime;
    protected IDao<T> mDao;

    protected abstract float getTotalWages(List<T> list);

    public BaseMainLogicImpl(@NonNull IDao<T> dao) {
        this.mDao = dao;
    }

    @Override public void calculationCurrentMonthWages(TextView tv) {

        calculationMonthTime();
        calculation(mDao.query(mMonthStartTime, mMonthEndTime), tv);
    }

    @Override public void calculationLastMonthWages(TextView tv) {
        calculationMonthTime();
        calculation(mDao.query(DateUtil.getAddMonth(mMonthStartTime, -1), DateUtil.getAddMonth(mMonthEndTime, -1)), tv);
    }

    @Override public void calculationCurrentYearWages(TextView tv) {
        calculation(mDao.queryCurrentYear(), tv);
    }

    @Override public void calculationTotalWages(TextView tv) {
        calculation(mDao.queryAll(), tv);
    }

    protected void calculation(Observable<List<T>> observable, TextView tv){
        if (null == observable) {
            return;
        }
        final WeakReference<TextView> weakReference = new WeakReference<>(tv);

        observable.flatMap(new Function<List<T>, ObservableSource<Float>>() {
            @Override public ObservableSource<Float> apply(@io.reactivex.annotations.NonNull List<T> list)
                    throws Exception {
                return Observable.just(DataUtil.isEmpty(list) ? 0f : getTotalWages(list));
            }
        }).compose(RxUtil.<Float>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER)).subscribe(new Observer<Float>() {
            Disposable mDisposable;
            @Override public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override public void onNext(@io.reactivex.annotations.NonNull Float wages) {
                if (weakReference.get() != null) {
                    weakReference.get().setText(MoneyUtil.replace(wages, Consts.DEFAULT_SCALE));
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

    /**
     * 计算月工资计算周期的开始、结束时间
     */
    private void calculationMonthTime() {
        Calendar calendar = DateUtil.getTimeInMillis(DateUtil.getYear(), DateUtil.getMonth());
        mMonthStartTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        mMonthEndTime = calendar.getTimeInMillis();
    }

}
