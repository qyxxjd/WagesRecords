package com.classic.wages.db.table;

/**
 * @author 续写经典
 * @date 2013/11/26
 */
public class WorkInfoTable {
    public static final String TABLENAME           = "t_workinfo";
    public static final String COLUMN_ID           = "id";
    public static final String COLUMN_STARTINGTIME = "startingTime";
    public static final String COLUMN_ENDTIME      = "endTime";
    public static final String COLUMN_CREATETIME   = "createTime";
    public static final String COLUMN_WEEK         = "week";
    public static final String COLUMN_MULTIPLE     = "multiple";
    public static final String COLUMN_FORMATTIME   = "formatTime";

    public static String getTableSql() {
        final StringBuilder sb = new StringBuilder("create table if not exists ");
        sb.append(TABLENAME)
          .append(" ( ")
          .append(COLUMN_ID)
          .append(" integer Primary Key AUTOINCREMENT, ")
          .append(COLUMN_STARTINGTIME)
          .append(" integer not null, ")
          .append(COLUMN_ENDTIME)
          .append(" integer not null, ")
          .append(COLUMN_CREATETIME)
          .append(" integer not null, ")
          .append(COLUMN_WEEK)
          .append(" integer, ")
          .append(COLUMN_MULTIPLE)
          .append(" float, ")
          .append(COLUMN_FORMATTIME)
          .append(" datetime ")
          .append(")");

        return sb.toString();
    }
}
