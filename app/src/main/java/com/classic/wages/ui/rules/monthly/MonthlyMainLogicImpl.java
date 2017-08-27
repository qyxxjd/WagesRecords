package com.classic.wages.ui.rules.monthly;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.entity.MonthlyInfo;
import com.classic.wages.ui.rules.base.BaseMainLogicImpl;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.LogUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.basic
 *
 * 文件描述：首页-月工资计算
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:33
 */
public class MonthlyMainLogicImpl extends BaseMainLogicImpl<MonthlyInfo> {

    public MonthlyMainLogicImpl(@NonNull MonthlyInfoDao dao) {
        super(dao);
    }

    @Override public void calculationCurrentMonthWages(TextView tv) {
        calculation(mDao.queryCurrentMonth(), tv);
    }

    @Override public void calculationLastMonthWages(TextView tv) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, DateUtil.getYear());
        calendar.set(Calendar.MONTH, DateUtil.getMonth());
        calendar.add(Calendar.MONTH, -1);
        final String month = formatMonth(calendar.get(Calendar.MONTH) + 1);
        LogUtil.d("上月份：" + month);
        calculation(mDao.query(String.valueOf(calendar.get(Calendar.YEAR)), month), tv);
    }

    private String formatMonth(int month) {
        return month <= 9 ? ("0" + month) : String.valueOf(month);
    }

    @Override protected float getTotalWages(List<MonthlyInfo> list) {
        return MonthlyUtils.getTotalWages(list);
    }
}
