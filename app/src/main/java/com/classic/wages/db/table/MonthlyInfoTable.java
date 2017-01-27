package com.classic.wages.db.table;

/**
 * 月工资数据表
 */
public class MonthlyInfoTable implements IBasicColumn{

    public static final String TABLE_NAME          = "t_monthly_info";
    public static final String COLUMN_MONTHLY_TIME = "monthlyTime";
    public static final String COLUMN_MONTHLY_WAGE = "monthlyWage";

    public static String getTableSql() {
        final StringBuilder sb = new StringBuilder("create table if not exists ");
        sb.append(TABLE_NAME)
          .append(" ( ")
          .append(COLUMN_ID)
          .append(" integer Primary Key AUTOINCREMENT, ")
          .append(COLUMN_CREATE_TIME)
          .append(" integer not null, ")
          .append(COLUMN_LAST_UPDATE_TIME)
          .append(" integer default 0, ")
          .append(COLUMN_FORMAT_TIME)
          .append(" datetime, ")
          .append(COLUMN_MONTHLY_TIME)
          .append(" integer not null, ")
          .append(COLUMN_MONTHLY_WAGE)
          .append(" float, ")
          .append(COLUMN_WEEK)
          .append(" integer, ")
          .append(COLUMN_MULTIPLE)
          .append(" float, ")
          .append(COLUMN_SUBSIDY)
          .append(" float, ")
          .append(COLUMN_BONUS)
          .append(" float, ")
          .append(COLUMN_DEDUCTIONS)
          .append(" float, ")
          .append(COLUMN_REMARK)
          .append(" text ")
          .append(")");

        return sb.toString();
    }

    public static String getUpdateSql4(){
        return SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_LAST_UPDATE_TIME, " integer default 0");
    }
}
