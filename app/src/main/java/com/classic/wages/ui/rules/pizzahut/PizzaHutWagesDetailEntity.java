package com.classic.wages.ui.rules.pizzahut;

import com.classic.wages.ui.rules.base.BaseWagesDetailEntity;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.base
 *
 * 文件描述：必胜客工资详情实体类
 * 创 建 人：续写经典
 * 创建时间：16/11/4 下午6:40
 */
class PizzaHutWagesDetailEntity extends BaseWagesDetailEntity {

    float totalNightHours;  //晚班总时长
    float totalNightWages;  //晚班总工资
    float totalRestHours;   //带薪休息总时长
    float totalRestWages;   //带薪休息总工资
}
