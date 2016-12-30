package com.classic.wages.entity;

import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;

/**
 * 计件工资
 *
 * @author 续写经典
 * @date 2016/05/02
 */
public class QuantityInfo extends BasicInfo {

    private long   workTime;
    private String title;
    private float  quantity;
    private float  unitPrice;

    /** 工作时间 */
    public long getWorkTime() {
        return workTime;
    }
    /** 工作时间 */
    public void setWorkTime(long workTime) {
        this.workTime = workTime;
        setWeek(Util.getDayOfWeek(workTime));
        setFormatTime(DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, workTime));
    }
    /** 计件物品名称 */
    public String getTitle() {
        return title;
    }
    /** 计件物品名称 */
    public void setTitle(String title) {
        this.title = title;
    }
    /** 数量 */
    public float getQuantity() {
        return quantity;
    }
    /** 数量 */
    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
    /** 单价 */
    public float getUnitPrice() {
        return unitPrice;
    }
    /** 单价 */
    public void setUnitrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public QuantityInfo() { }

    public QuantityInfo(long workTime, String title, float quantity, float unitPrice) {
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        setCreateTime(System.currentTimeMillis());
        setWorkTime(workTime);
    }

    public QuantityInfo(long id, long createTime, long workTime, String title, float quantity,
                        float unitPrice,  int week, float multiple, String formatTime,
                        float subsidy, float bonus, float deductions, String remark) {
        this.workTime = workTime;
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        setId(id);
        setCreateTime(createTime);
        setWeek(week);
        setMultiple(multiple);
        setSubsidy(subsidy);
        setBonus(bonus);
        setDeductions(deductions);
        setFormatTime(formatTime);
        setRemark(remark);
    }
}
