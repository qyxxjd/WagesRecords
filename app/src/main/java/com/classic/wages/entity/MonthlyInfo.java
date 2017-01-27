package com.classic.wages.entity;

import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

/**
 * 月工资
 *
 * @author 续写经典
 * @date 2016/05/02
 */
public class MonthlyInfo extends BasicInfo {

    private long  monthlyTime;
    private float monthlyWage;

    public long getMonthlyTime() {
        return monthlyTime;
    }

    public void setMonthlyTime(long monthlyTime) {
        this.monthlyTime = monthlyTime;
        setWeek(Util.getDayOfWeek(monthlyTime));
        setFormatTime(DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, monthlyTime));
    }
    /** 月工资 */
    public float getMonthlyWage() {
        return monthlyWage;
    }
    /** 月工资 */
    public void setMonthlyWage(float monthlyWage) {
        this.monthlyWage = monthlyWage;
    }

    public MonthlyInfo() { }

    public MonthlyInfo(long monthlyTime, float monthlyWage) {
        this.monthlyWage = monthlyWage;
        setCreateTime(System.currentTimeMillis());
        setMonthlyTime(monthlyTime);
    }

    public MonthlyInfo(long id, long createTime, int week,
                       float multiple, float subsidy, float bonus,
                       float deductions, String formatTime, String remark, long lastUpdateTime,
                       long monthlyTime, float monthlyWage) {
        this.monthlyTime = monthlyTime;
        this.monthlyWage = monthlyWage;
        setId(id);
        setCreateTime(createTime);
        setWeek(week);
        setMultiple(multiple);
        setSubsidy(subsidy);
        setBonus(bonus);
        setDeductions(deductions);
        setFormatTime(formatTime);
        setRemark(remark);
        setLastUpdateTime(lastUpdateTime);
    }
}
