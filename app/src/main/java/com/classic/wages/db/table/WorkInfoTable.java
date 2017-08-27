package com.classic.wages.db.table;

import java.util.ArrayList;

/**
 * @author 续写经典
 * @date 2013/11/26
 */
public class WorkInfoTable implements IBasicColumn {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String TABLE_NAME           = "t_workinfo";
    public static final String COLUMN_STARTING_TIME = "startingTime";
    public static final String COLUMN_END_TIME      = "endTime";
    public static final String COLUMN_REST_TIME     = "restTime";

    public static String createTableSql() {
        final StringBuilder sb = new StringBuilder("create table if not exists ");
        sb.append(TABLE_NAME)
          .append(" ( ")
          .append(COLUMN_ID)
          .append(" integer Primary Key AUTOINCREMENT, ")
          .append(COLUMN_STARTING_TIME)
          .append(" integer not null, ")
          .append(COLUMN_END_TIME)
          .append(" integer not null, ")
          .append(COLUMN_FORMAT_TIME)
          .append(" datetime, ")
          .append(COLUMN_CREATE_TIME)
          .append(" integer not null, ")
          .append(COLUMN_LAST_UPDATE_TIME)
          .append(" integer default 0, ")
          .append(COLUMN_REST_TIME)
          .append(" integer default 0, ")
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

    public static ArrayList<String> getUpdateSql3(){
        ArrayList<String> sqlArray = new ArrayList<>();
        sqlArray.add(SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_SUBSIDY, " float"));
        sqlArray.add(SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_BONUS, " float"));
        sqlArray.add(SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_DEDUCTIONS, " float"));
        sqlArray.add(SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_REMARK, " text"));
        return sqlArray;
    }

    public static String getUpdateSql4(){
        return SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_LAST_UPDATE_TIME, " integer default 0");
    }

    public static String getUpdateSql5() {
        return SqlUtil.getAddColumnSql(TABLE_NAME, COLUMN_REST_TIME, " integer default 0");
    }
}
