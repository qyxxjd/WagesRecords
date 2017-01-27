package com.classic.wages.db.table;

class SqlUtil {

    static String getAddColumnSql(String tableName, String columnName, String columnDetail) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder().append("ALTER TABLE ")
                                  .append(tableName)
                                  .append(" ADD COLUMN ")
                                  .append(columnName)
                                  .append(columnDetail)
                                  .toString();
    }
}
