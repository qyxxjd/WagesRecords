package com.classic.wages.consts;

import java.util.Arrays;
import java.util.List;

/**
 * @author 续写经典
 * @date 2013/12/5
 */
public class Consts {
    private Consts() {}

    public static final List<String> RULES_LIST = Arrays.asList(
            "默认规则", "固定+加班", "必胜客兼职", "按月计算", "计件" );

    public static final int PAGE_SIZE = 10;

    public static final String DEFAULT_HOURLY_WAGE = "11";
    public static final String DEFAULT_WORK_HOURS  = "100";
    public static final String DEFAULT_NIGHT_WAGE  = "3.3";

    public static final String SP_NAME        = "recordsWages";
    /** 时薪 */
    public static final String SP_HOURLY_WAGE = "hourlyWage";
    /** 当前工资计算规则 */
    public static final String SP_RULES_TYPE  = "rulesType";

    /*    规则一（固定+加班）    */
    /** 加班时薪 */
    public static final String SP_OVERTIME_WAGE = "overtimeWage";
    /** 固定时长-超过这个时间按加班计算 */
    public static final String SP_WORK_HOURS    = "workHours";


    /*    规则二（必胜客兼职）    */
    /** 时薪-带薪休息 */
    public static final String SP_REST_WAGE  = "restWage";
    /** 时薪-22点后晚班津贴 */
    public static final String SP_NIGHT_WAGE = "nightWage";

    public static final String TAG_DATA_CHANGE  = "dataChange";
    public static final String TAG_RULES_CHANGE = "rulesChange";
}
