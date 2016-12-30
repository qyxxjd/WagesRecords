package com.classic.wages.consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 续写经典
 * @date 2013/12/5
 */
public class Consts {
    private Consts() {}

    public static final String DIR_NAME           = "WagesRecords";
    public static final String APK_NAME           = "last.apk";
    public static final String AUTHORITIES_SUFFIX = ".provider";

    public static final String STORAGE_PERMISSIONS_DESCRIBE  = "应用需要访问你的存储空间,进行日志存储";
    public static final String FEEDBACK_PERMISSIONS_DESCRIBE = "语音反馈需要使用语音录制权限";
    public static final String APPLY_FOR_PERMISSIONS         = "权限申请";
    public static final String SETUP                         = "设置";
    public static final String CANCEL                        = "取消";

    public static final int          MIN_YEAR = 2010;
    public static final int          MAX_YEAR = 2050;
    public static final List<String> YEARS    = new ArrayList<>();
    public static final List<String> MONTHS   = new ArrayList<>();
    static {
        for (int i = MIN_YEAR; i <= MAX_YEAR; i++) {
            YEARS.add(String.valueOf(i));
        }
        for (int i = 1; i <= 12; i++) {
            MONTHS.add(String.valueOf(i));
        }
    }

    public static final List<String> RULES_LIST =
            Arrays.asList("默认规则", "按月加班", "必胜客兼职", "月工资", "计件", "按天加班");

    public static final String[] FORMAT_WEEKS  = { "日", "一", "二", "三", "四", "五", "六" };
    public static final int      HOUR_2_MS     = 3600000;
    public static final int      DEFAULT_SCALE = 2;

    public static final String DEFAULT_HOURLY_WAGE       = "11";  //默认时薪
    public static final String DEFAULT_NIGHT_SUBSIDY     = "3.3"; //默认晚班补贴
    public static final String DEFAULT_DAY_FIXED_HOURS   = "10";  //默认按天加班的固定时长
    public static final String DEFAULT_MONTH_FIXED_HOURS = "100"; //默认按月加班的固定时长

    public static final String SP_NAME        = "recordsWages";
    /** 当前工资计算规则 */
    public static final String SP_RULES_TYPE  = "rulesType";

    /**
     * 默认规则
     * 时薪
     */
    public static final String SP_HOURLY_WAGE = "hourlyWage";

    /**
     * 按天加班规则
     */
    //正常时薪
    public static final String SP_FIXED_DAY_HOURLY_WAGE          = "fixedDayHourlyWage";
    //固定时长, 每天超过这个时间算加班工资
    public static final String SP_FIXED_DAY_FIXED_HOURS          = "fixedDayFixedHours";
    //加班时薪
    public static final String SP_FIXED_DAY_OVERTIME_HOURLY_WAGE = "fixedDayOvertimeHourlyWage";

    /**
     * 按月加班规则
     */
    //正常时薪
    public static final String SP_FIXED_MONTH_HOURLY_WAGE          = "fixedMonthHourlyWage";
    //固定时长, 每个月超过这个时间算加班工资
    public static final String SP_FIXED_MONTH_FIXED_HOURS          = "fixedMonthFixedHours";
    //加班时薪
    public static final String SP_FIXED_MONTH_OVERTIME_HOURLY_WAGE = "fixedMonthOvertimeHourlyWage";

    /**
     * 必胜客兼职规则
     */
    //正常时薪
    public static final String SP_PIZZA_HUT_HOURLY_WAGE      = "pizzaHutHourlyWage";
    //带薪休息的时薪
    public static final String SP_PIZZA_HUT_REST_HOURLY_WAGE = "pizzaHutRestHourlyWage";
    //晚班补贴
    public static final String SP_PIZZA_HUT_NIGHT_SUBSIDY    = "pizzaHutNightSubsidy";

    public static final String WAGES_DETAIL_SEPARATOR = "----";
}
