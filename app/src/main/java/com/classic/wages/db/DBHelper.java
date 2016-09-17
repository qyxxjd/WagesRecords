package com.classic.wages.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.classic.wages.db.table.WorkInfoTable;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "RecordsWages.db";
    private static final int    DB_VERSION = 3;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(WorkInfoTable.createTableSql());
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

    private void update3(SQLiteDatabase db) {
        ArrayList<String> sqlArray = WorkInfoTable.getUpdateSql3();
        for (String sql:sqlArray){
            db.execSQL(sql);
        }
        sqlArray.clear();

        //db.execSQL(MonthlyInfoTable.getTableSql());
        //db.execSQL(QuantityInfoTable.getTableSql());
    }
}
