package com.classic.wages.entity;

import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

/**
 * 工作信息
 *
 * @author 续写经典
 * @date 2013/11/26
 */
public class WorkInfo extends BasicInfo {

    private long startingTime;
    private long endTime;
    private int restTime; // 5.2.1新增: 肯德基兼职-休息时间, 单位：分钟

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
        setFormatTime(DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, startingTime));
    }

    /** 肯德基兼职-休息时间 */
    public int getRestTime() {
        return restTime;
    }

    /** 肯德基兼职-休息时间 */
    public void setRestTime(int restTime) {
        this.restTime = restTime;
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

    public WorkInfo(long id, long createTime, int week,
                    float multiple, float subsidy, float bonus,
                    float deductions, String formatTime, String remark, long lastUpdateTime,
                    long startingTime, long endTime, int restTime) {
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.restTime = restTime;
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
