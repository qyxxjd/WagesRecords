package com.classic.wages.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.classic.wages.db.table.WorkInfoTable;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "RecordsWages.db";
    private static final int    DB_VERSION = 3;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(WorkInfoTable.getTableSql());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();
            switch (newVersion) {
                case 3:
                    update3(db);
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

    //添加按月计算/计件
    private void update3(SQLiteDatabase db) {
        //db.execSQL(MonthlyInfoTable.getTableSql());
        //db.execSQL(QuantityInfoTable.getTableSql());
    }
}
