package com.classic.wages.db.table;

import java.util.ArrayList;

/**
 * @author 续写经典
 * @date 2013/11/26
 */
public class WorkInfoTable implements IBasicColumn {
    public static final String TABLE_NAME           = "t_workinfo";
    public static final String COLUMN_STARTING_TIME = "startingTime";
    public static final String COLUMN_END_TIME      = "endTime";

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
        sqlArray.add("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+COLUMN_SUBSIDY+" float");
        sqlArray.add("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+COLUMN_BONUS+" float");
        sqlArray.add("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+COLUMN_DEDUCTIONS+" float");
        sqlArray.add("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+COLUMN_REMARK+" text");
        return sqlArray;
    }
}
