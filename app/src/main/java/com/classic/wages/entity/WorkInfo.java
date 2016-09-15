package com.classic.wages.entity;

import com.classic.core.utils.DateUtil;
import com.classic.wages.utils.Util;
import java.io.Serializable;

/**
 * 工作信息
 *
 * @author 续写经典
 * @date 2013/11/26
 */
public class WorkInfo implements Serializable {

    private long   id;
    private long   startingTime;
    private long   endTime;
    private long   createTime;
    private int    week;
    private float  multiple;
    private String formatTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** 创建时间 */
    public long getCreateTime() {
        return createTime;
    }

    /** 创建时间 */
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /** 工作结束时间 */
    public long getEndTime() {
        return endTime;
    }

    /** 工作结束时间 */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /** 多倍工资(如：节假日双倍、三倍) */
    public float getMultiple() {
        return multiple;
    }

    /** 多倍工资(如：节假日双倍、三倍) */
    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    /** 工作开始时间 */
    public long getStartingTime() {
        return startingTime;
    }

    /** 工作开始时间 */
    public void setStartingTime(long startingTime) {
        this.startingTime = startingTime;
    }

    /** 周几 */
    public int getWeek() {
        return week;
    }

    /** 周几 */
    public void setWeek(int week) {
        this.week = week;
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }

    public WorkInfo() {}

    public WorkInfo(long startingTime, long endTime) {
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.createTime = System.currentTimeMillis();
        this.week = Util.getDayOfWeek(startingTime);
        this.formatTime = DateUtil.formatDate(DateUtil.FORMAT, startingTime);
    }

    public WorkInfo(long startingTime, long endTime, float multiple) {
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.multiple = multiple;
        this.createTime = System.currentTimeMillis();
        this.week = Util.getDayOfWeek(startingTime);
        this.formatTime = DateUtil.formatDate(DateUtil.FORMAT, startingTime);
    }

    public WorkInfo(long id, long startingTime, long endTime, long createTime, int week, float multiple, String formatTime) {
        this.id = id;
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.week = week;
        this.multiple = multiple;
        this.formatTime = formatTime;
    }
}
