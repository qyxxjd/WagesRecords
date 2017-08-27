package com.classic.wages.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.classic.wages.db.table.MonthlyInfoTable;
import com.classic.wages.db.table.QuantityInfoTable;
import com.classic.wages.db.table.WorkInfoTable;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "RecordsWages.db";
    private static final int    DB_VERSION = 5;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(WorkInfoTable.createTableSql());
        db.execSQL(MonthlyInfoTable.getTableSql());
        db.execSQL(QuantityInfoTable.getTableSql());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();
            switch (newVersion) {
                case 3:
                    update3(db);
                    break;
                case 4:
                    update4(db);
                    break;
                case 5:
                    update5(db);
                    break;
                default:
                    break;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.setVersion(newVersion);
    }


    private void update3(SQLiteDatabase db) {
        //添加奖金、补贴、扣款、备注字段
        ArrayList<String> sqlArray = WorkInfoTable.getUpdateSql3();
        for (String sql:sqlArray){
            db.execSQL(sql);
        }
        sqlArray.clear();
        //新增月工资
        db.execSQL(MonthlyInfoTable.getTableSql());
        //新增计件工资
        db.execSQL(QuantityInfoTable.getTableSql());
    }

    /**
     * 新增最后更新时间字段
     * @param db
     */
    private void update4(SQLiteDatabase db) {
        db.execSQL(MonthlyInfoTable.getUpdateSql4());
        db.execSQL(QuantityInfoTable.getUpdateSql4());
        db.execSQL(WorkInfoTable.getUpdateSql4());
    }

    /**
     * 新增肯德基兼职工资计算
     *
     * @param db
     */
    private void update5(SQLiteDatabase db) {
        db.execSQL(WorkInfoTable.getUpdateSql5());
    }
}
