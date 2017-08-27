package com.classic.wages.ui.rules;

import android.widget.TextView;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules
 *
 * 文件描述：规范首页业务接口
 * 创 建 人：续写经典
 * 创建时间：16/10/23 下午1:13
 */
public interface IMainLogic {

    /**
     * 计算当前月份工资
     */
    void calculationCurrentMonthWages(TextView tv);

    /**
     * 计算上月份工资
     */
    void calculationLastMonthWages(TextView tv);

    /**
     * 计算当前年份工资
     */
    void calculationCurrentYearWages(TextView tv);

    /**
     * 计算总工资
     */
    void calculationTotalWages(TextView tv);
}
