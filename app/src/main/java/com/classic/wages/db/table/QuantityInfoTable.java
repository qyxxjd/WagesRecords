package com.classic.wages.db.table;

/**
 * 计件数据表
 */
public class QuantityInfoTable implements IBasicColumn{
    public static final String TABLE_NAME         = "t_quantity_info";
    public static final String COLUMN_WORK_TIME   = "workTime";
    public static final String COLUMN_TITLE       = "title";
    public static final String COLUMN_QUANTITY    = "quantity";
    public static final String COLUMN_UNIT_PRICE  = "unitPrice";

    public static String getTableSql() {
        final StringBuilder sb = new StringBuilder("create table if not exists ");
        sb.append(TABLE_NAME)
          .append(" ( ")
          .append(COLUMN_ID)
          .append(" integer Primary Key AUTOINCREMENT, ")
          .append(COLUMN_CREATE_TIME)
          .append(" integer not null, ")
          .append(COLUMN_WORK_TIME)
          .append(" integer, ")
          .append(COLUMN_TITLE)
          .append(" text, ")
          .append(COLUMN_QUANTITY)
          .append(" float, ")
          .append(COLUMN_UNIT_PRICE)
          .append(" float, ")
          .append(COLUMN_FORMAT_TIME)
          .append(" datetime, ")
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
}
