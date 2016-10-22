package com.classic.wages.entity;

import com.classic.core.utils.DateUtil;
import com.classic.wages.utils.Util;

/**
 * 工作信息
 *
 * @author 续写经典
 * @date 2013/11/26
 */
public class WorkInfo extends BasicInfo {

    private long   startingTime;
    private long   endTime;
    private String formatTime; //用于sql日期函数查询

    /** 工作结束时间 */
    public long getEndTime() {
        return endTime;
    }

    /** 工作结束时间 */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /** 工作开始时间 */
    public long getStartingTime() {
        return startingTime;
    }

    /** 工作开始时间 */
    public void setStartingTime(long startingTime) {
        this.startingTime = startingTime;
        setWeek(Util.getDayOfWeek(startingTime));
        setFormatTime(DateUtil.formatDate(DateUtil.FORMAT, startingTime));
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }

    public WorkInfo(long startingTime, long endTime) {
        this(startingTime, endTime, 0F);
    }

    public WorkInfo(long startingTime, long endTime, float multiple) {
        this.endTime = endTime;
        setStartingTime(startingTime);
        setCreateTime(System.currentTimeMillis());
        setMultiple(multiple);
    }

    public WorkInfo(long id, long startingTime, long endTime, long createTime, int week, float multiple,
                    String formatTime, float subsidy, float bonus, float deductions, String remark) {
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.formatTime = formatTime;
        setId(id);
        setCreateTime(createTime);
        setWeek(week);
        setMultiple(multiple);
        setSubsidy(subsidy);
        setBonus(bonus);
        setDeductions(deductions);
        setRemark(remark);
    }
}
