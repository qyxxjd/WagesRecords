package com.classic.wages.ui.rules.base;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述：工资详情实体类
 * 创 建 人：续写经典
 * 创建时间：16/11/4 下午6:40
 */
public class BaseWagesDetailEntity {

    public float totalNormalHours;   //正常工作总时长
    public float totalNormalWages;   //正常工作总工资
    public float totalHolidayHours;  //节假日总时长
    public float totalHolidayWages;  //节假日总工资
    public float totalOvertimeHours; //加班总时长
    public float totalOvertimeWages; //加班总工资
    public float totalSubsidy;    //总补贴
    public float totalBonus;      //总奖金
    public float totalDeductions; //总扣款
    public float totalWages;      //总工资
}
