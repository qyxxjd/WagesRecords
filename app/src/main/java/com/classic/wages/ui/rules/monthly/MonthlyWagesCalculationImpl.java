package com.classic.wages.ui.rules.monthly;

import android.support.annotation.NonNull;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.entity.MonthlyInfo;
import com.classic.wages.ui.rules.base.BaseWagesCalculationImpl;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-月工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class MonthlyWagesCalculationImpl extends BaseWagesCalculationImpl<MonthlyInfo> {

    public MonthlyWagesCalculationImpl(@NonNull MonthlyInfoDao dao) {
        super(dao);
    }

    @Override protected float getWages(@NonNull MonthlyInfo info) {
        return MonthlyUtils.getWages(info);
    }

    //@Override public void calculationCurrentMonthWages(TextView tv) {
    //    calculation(mMonthlyInfoDao.queryCurrentMonth(), tv);
    //}
    //
    //@Override public void calculationCurrentYearWages(TextView tv) {
    //    calculation(mMonthlyInfoDao.queryCurrentYear(), tv);
    //}
    //
    //@Override public void calculationTotalWages(TextView tv) {
    //    calculation(mMonthlyInfoDao.queryAll(), tv);
    //}
    //
    //private void calculation(Observable<List<MonthlyInfo>> observable, TextView tv){
    //    final WeakReference<TextView> weakReference = new WeakReference<>(tv);
    //    observable.flatMap(new Func1<List<MonthlyInfo>, Observable<Float>>() {
    //                    @Override public Observable<Float> call(List<MonthlyInfo> list) {
    //                        return Observable.just(getTotalWages(list));
    //                    }
    //                })
    //                .subscribe(new Action1<Float>() {
    //                    @Override public void call(Float wages) {
    //                        if(weakReference.get() != null){
    //                            weakReference.get().setText(MoneyUtil.replace(wages));
    //                        }
    //                    }
    //                }, RxUtil.ERROR_ACTION);
    //}



    //private float getTotalWages(List<MonthlyInfo> list){
    //    float totalWages = 0f;
    //    if(DataUtil.isEmpty(list)) return totalWages;
    //    for (MonthlyInfo item : list) {
    //        totalWages += (item.getMonthlyWage() + item.getSubsidy() + item.getBonus() -
    //                        item.getDeductions());
    //    }
    //    return MoneyUtil.newInstance(totalWages).round(Consts.DEFAULT_SCALE).create().floatValue();
    //}
}
