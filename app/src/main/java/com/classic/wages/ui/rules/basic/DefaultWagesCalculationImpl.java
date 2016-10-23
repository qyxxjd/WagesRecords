package com.classic.wages.ui.rules.basic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;
import com.classic.core.utils.MoneyUtil;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.IWagesCalculation;
import java.lang.ref.WeakReference;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class DefaultWagesCalculationImpl implements IWagesCalculation {

    private       Context     mContext;
    private       WorkInfoDao mWorkInfoDao;
    private final float       mHourlyWage;

    public DefaultWagesCalculationImpl(@NonNull Context context, @NonNull WorkInfoDao workInfoDao) {
        this.mContext = context.getApplicationContext();
        this.mWorkInfoDao = workInfoDao;
        mHourlyWage = Float.valueOf(new SharedPreferencesUtil(context, Consts.SP_NAME)
                .getStringValue(Consts.SP_HOURLY_WAGE, Consts.DEFAULT_HOURLY_WAGE));
    }

    @Override public void calculationCurrentMonthWages(TextView tv) {
        calculation(mWorkInfoDao.queryCurrentMonth(), tv);
    }

    @Override public void calculationCurrentYearWages(TextView tv) {
        calculation(mWorkInfoDao.queryCurrentYear(), tv);
    }

    @Override public void calculationTotalWages(TextView tv) {
        calculation(mWorkInfoDao.queryAll(), tv);
    }

    private void calculation(Observable<List<WorkInfo>> observable, TextView tv){
        final WeakReference<TextView> weakReference = new WeakReference<>(tv);
        observable.flatMap(new Func1<List<WorkInfo>, Observable<Float>>() {
                        @Override public Observable<Float> call(List<WorkInfo> list) {
                            return Observable.just(DefaultUtil.getTotalWages(list, mHourlyWage));
                        }
                    })
                    .subscribe(new Action1<Float>() {
                        @Override public void call(Float wages) {
                            if(weakReference.get() != null){
                                weakReference.get().setText(MoneyUtil.replace(wages));
                            }
                        }
                    });
    }
}
