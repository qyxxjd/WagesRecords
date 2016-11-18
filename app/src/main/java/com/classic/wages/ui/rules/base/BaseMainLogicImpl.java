package com.classic.wages.ui.rules.base;

import android.support.annotation.NonNull;
import android.widget.TextView;
import com.classic.core.utils.DataUtil;
import com.classic.core.utils.MoneyUtil;
import com.classic.wages.db.dao.IDao;
import com.classic.wages.ui.rules.IMainLogic;
import com.classic.wages.utils.RxUtil;
import java.lang.ref.WeakReference;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public abstract class BaseMainLogicImpl<T> implements IMainLogic {

    private IDao<T> mDao;

    protected abstract float getTotalWages(List<T> list);

    public BaseMainLogicImpl(@NonNull IDao<T> dao) {
        this.mDao = dao;
    }

    @Override public void calculationCurrentMonthWages(TextView tv) {
        calculation(mDao.queryCurrentMonth(), tv);
    }

    @Override public void calculationCurrentYearWages(TextView tv) {
        calculation(mDao.queryCurrentYear(), tv);
    }

    @Override public void calculationTotalWages(TextView tv) {
        calculation(mDao.queryAll(), tv);
    }

    protected void calculation(Observable<List<T>> observable, TextView tv){
        final WeakReference<TextView> weakReference = new WeakReference<>(tv);
        observable.flatMap(new Func1<List<T>, Observable<Float>>() {
                        @Override public Observable<Float> call(List<T> list) {
                            return Observable.just(DataUtil.isEmpty(list) ?
                                                   0f : getTotalWages(list));
                        }
                    })
                  .compose(RxUtil.<Float>applySchedulers(RxUtil.THREAD_ON_UI_TRANSFORMER))
                  .subscribe(new Action1<Float>() {
                      @Override public void call(Float wages) {
                          if(weakReference.get() != null){
                              weakReference.get().setText(MoneyUtil.replace(wages));
                          }
                      }
                  }, RxUtil.ERROR_ACTION);
    }

}