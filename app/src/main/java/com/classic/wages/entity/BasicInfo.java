package com.classic.wages.entity;

import java.io.Serializable;

/**
 * 基本信息
 */
public class BasicInfo implements Serializable{

    private long   id;
    private long   createTime;
    private int    week;
    private float  subsidy; //补贴
    private float  bonus;   //奖金
    private float  deductions;//扣款
    private float  multiple;  //节假日多倍工资
    private String formatTime; //用于sql日期函数查询
    private String remark;
    private long   lastUpdateTime; //最后更新时间

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

    /** 多倍工资(如：节假日双倍、三倍) */
    public float getMultiple() {
        return multiple;
    }

    /** 多倍工资(如：节假日双倍、三倍) */
    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    /** 周几 */
    public int getWeek() {
        return week;
    }

    /** 周几 */
    public void setWeek(int week) {
        this.week = week;
    }

    /** 补贴 */
    public float getSubsidy() {
        return subsidy;
    }

    /** 补贴 */
    public void setSubsidy(float subsidy) {
        this.subsidy = subsidy;
    }

    /** 奖金 */
    public float getBonus() {
        return bonus;
    }

    /** 奖金 */
    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    /** 扣款 */
    public float getDeductions() {
        return deductions;
    }

    /** 扣款 */
    public void setDeductions(float deductions) {
        this.deductions = deductions;
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }

    /** 备注 */
    public String getRemark() {
        return remark;
    }

    /** 备注 */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /** 最后更新时间 */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /** 最后更新时间 */
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
