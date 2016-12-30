package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.classic.wages.db.table.MonthlyInfoTable;
import com.classic.wages.entity.MonthlyInfo;
import com.classic.wages.utils.CloseUtil;
import com.elvishew.xlog.XLog;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.db.dao
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/27 下午7:19
 */
public class MonthlyInfoDao implements IDao<MonthlyInfo> {
    private BriteDatabase mDatabase;

    public MonthlyInfoDao(@NonNull BriteDatabase database) {
        mDatabase = database;
    }

    @Override public long insert(@NonNull MonthlyInfo monthlyInfo) {
        return mDatabase.insert(MonthlyInfoTable.TABLE_NAME, convert(monthlyInfo, false));
    }

    @Override public void insert(@NonNull List<MonthlyInfo> list) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            for (MonthlyInfo item : list) {
                insert(item);
            }
            transaction.markSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transaction.end();
        }
    }

    @Override public int update(@NonNull MonthlyInfo monthlyInfo) {
        return mDatabase.update(MonthlyInfoTable.TABLE_NAME, convert(monthlyInfo, true),
                                MonthlyInfoTable.COLUMN_ID + "=" + monthlyInfo.getId());
    }

    @Override
    public int delete(long id) {
        return mDatabase.delete(MonthlyInfoTable.TABLE_NAME, MonthlyInfoTable.COLUMN_ID + "=" + id);
    }

    @Override
    public Observable<List<MonthlyInfo>> query(String year, String month) {
        return queryListBySql(getSql(year, month));
    }

    private String getSql(String year, String month) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(MonthlyInfoTable.TABLE_NAME);
        if (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(month)) {
            sb.append(" WHERE ");
        }
        if (!TextUtils.isEmpty(year)) {
            sb.append(" strftime('%Y',")
              .append(MonthlyInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(year)
              .append("' ");
        }
        if (!TextUtils.isEmpty(year) && !TextUtils.isEmpty(month)) {
            sb.append(" AND ");
        }
        if (!TextUtils.isEmpty(month)) {
            sb.append(" strftime('%m',")
              .append(MonthlyInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(month)
              .append("' ");
        }
        sb.append(" ORDER BY ").append(MonthlyInfoTable.COLUMN_MONTHLY_TIME).append(" DESC ");
        return sb.toString();
    }

    @Override
    public Observable<List<MonthlyInfo>> queryCurrentMonth() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(MonthlyInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(MonthlyInfoTable.COLUMN_FORMAT_TIME)
                .append(" between datetime('now','start of month','+1 second') ")
                .append("AND datetime('now','start of month','+1 month','-1 second')")
                //.append(" ORDER BY ")
                //.append(MonthlyInfoTable.COLUMN_MONTHLY_TIME)
                //.append(" DESC ")
                ;
        return queryListBySql(sb.toString());
    }

    @Override
    public Observable<List<MonthlyInfo>> queryCurrentYear() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(MonthlyInfoTable.TABLE_NAME)
                .append(" WHERE strftime('%Y',")
                .append(MonthlyInfoTable.COLUMN_FORMAT_TIME)
                .append(")=strftime('%Y',date('now')) ")
                //.append(" ORDER BY ")
                //.append(MonthlyInfoTable.COLUMN_MONTHLY_TIME)
                //.append(" DESC ")
                ;
        return queryListBySql(sb.toString());
    }

    @Override
    public Observable<List<MonthlyInfo>> queryAll() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(MonthlyInfoTable.TABLE_NAME)
                .append(" ORDER BY ")
                .append(MonthlyInfoTable.COLUMN_MONTHLY_TIME)
                .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    @Override
    public Observable<List<String>> queryYears() {
        //no impl
        return null;
    }


    private Observable<List<MonthlyInfo>> queryListBySql(String sql) {
        return mDatabase.createQuery(MonthlyInfoTable.TABLE_NAME, sql)
                        .map(new Func1<SqlBrite.Query, List<MonthlyInfo>>() {
                            @Override
                            public List<MonthlyInfo> call(SqlBrite.Query query) {
                                return convert(query.run());
                            }
                        });
    }

    private ContentValues convert(@NonNull MonthlyInfo info, boolean isUpdate) {
        ContentValues values = new ContentValues();
        values.put(MonthlyInfoTable.COLUMN_MONTHLY_TIME, info.getMonthlyTime());
        values.put(MonthlyInfoTable.COLUMN_MONTHLY_WAGE, info.getMonthlyWage());
        values.put(MonthlyInfoTable.COLUMN_WEEK, info.getWeek());
        values.put(MonthlyInfoTable.COLUMN_MULTIPLE, info.getMultiple());
        values.put(MonthlyInfoTable.COLUMN_FORMAT_TIME, info.getFormatTime());
        values.put(MonthlyInfoTable.COLUMN_SUBSIDY, info.getSubsidy());
        values.put(MonthlyInfoTable.COLUMN_BONUS, info.getBonus());
        values.put(MonthlyInfoTable.COLUMN_DEDUCTIONS, info.getDeductions());
        values.put(MonthlyInfoTable.COLUMN_REMARK, info.getRemark());
        if (!isUpdate) {
            values.put(MonthlyInfoTable.COLUMN_CREATE_TIME, info.getCreateTime());
        }
        return values;
    }

    private List<MonthlyInfo> convert(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        List<MonthlyInfo> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(new MonthlyInfo(cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_ID)),
                                         cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_CREATE_TIME)),
                                         cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_MONTHLY_TIME)),
                                         cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_MONTHLY_WAGE)),
                                         cursor.getInt(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_WEEK)),
                                         cursor.getString(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_FORMAT_TIME)),
                                         cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_MULTIPLE)),
                                         cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_SUBSIDY)),
                                         cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_BONUS)),
                                         cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_DEDUCTIONS)),
                                         cursor.getString(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_REMARK))));
            }
        } catch (Exception e) {
            XLog.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }
}
