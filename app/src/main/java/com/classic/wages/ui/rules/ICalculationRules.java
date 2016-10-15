package com.classic.wages.ui.rules;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.interfaces
 *
 * 文件描述：工资计算规则事件监听
 * 创 建 人：续写经典
 * 创建时间：16/9/16 下午12:30
 */
public interface ICalculationRules {
    /** 计算规则：默认规则 */
    int RULES_DEFAULT  = 0x00;
    /** 计算规则：固定+加班 */
    int RULES_FIXED    = 0x01;
    /** 计算规则：必胜客兼职 */
    int RULES_PIZZAHUT = 0x02;
    /** 计算规则：按月计算 */
    int RULES_MONTHLY  = 0x03;
    /** 计算规则：计件 */
    int RULES_QUANTITY = 0x04;

    /**
     * 计算规则改变
     * @param rules
     */
    void onCalculationRulesChange(int rules);
}
