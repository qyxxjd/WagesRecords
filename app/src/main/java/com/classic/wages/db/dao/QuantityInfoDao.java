package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.classic.core.utils.CloseUtil;
import com.classic.wages.db.table.QuantityInfoTable;
import com.classic.wages.entity.QuantityInfo;
import com.orhanobut.logger.Logger;
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
 * 创建时间：16/10/27 下午7:20
 */
public class QuantityInfoDao implements IDao<QuantityInfo> {
    private BriteDatabase mDatabase;

    public QuantityInfoDao(@NonNull BriteDatabase database) {
        mDatabase = database;
    }

    @Override public long insert(@NonNull QuantityInfo quantityInfo) {
        return mDatabase.insert(QuantityInfoTable.TABLE_NAME, convert(quantityInfo, false));
    }

    @Override public void insert(@NonNull List<QuantityInfo> list) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            for (QuantityInfo item : list) {
                insert(item);
            }
            transaction.markSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transaction.end();
        }
    }

    @Override public int update(@NonNull QuantityInfo quantityInfo) {
        return mDatabase.update(QuantityInfoTable.TABLE_NAME, convert(quantityInfo, true),
                QuantityInfoTable.COLUMN_ID + "=" + quantityInfo.getId());
    }

    @Override public int delete(long id) {
        return mDatabase.delete(QuantityInfoTable.TABLE_NAME, QuantityInfoTable.COLUMN_ID + "=" + id);
    }

    @Override public Observable<List<QuantityInfo>> query(String year, String month) {
        return queryListBySql(getSql(year, month));
    }

    @Override public Observable<List<QuantityInfo>> queryCurrentMonth() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(QuantityInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(QuantityInfoTable.COLUMN_FORMAT_TIME)
                .append(" between datetime('now','start of month','+1 second') ")
                .append("AND datetime('now','start of month','+1 month','-1 second')")
                .append(" ORDER BY ")
                .append(QuantityInfoTable.COLUMN_WORK_TIME)
                .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    @Override public Observable<List<QuantityInfo>> queryCurrentYear() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(QuantityInfoTable.TABLE_NAME)
                .append(" WHERE strftime('%Y',")
                .append(QuantityInfoTable.COLUMN_FORMAT_TIME)
                .append(")=strftime('%Y',date('now')) ")
                .append(" ORDER BY ")
                .append(QuantityInfoTable.COLUMN_WORK_TIME)
                .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    @Override public Observable<List<QuantityInfo>> queryAll() {
        return queryListBySql(getSql(null, null));
    }

    @Override public Observable<List<String>> queryYears() {
        final String sql = new StringBuilder("SELECT DISTINCT STRFTIME('%Y', ")
                .append(QuantityInfoTable.COLUMN_FORMAT_TIME)
                .append(") AS years FROM ")
                .append(QuantityInfoTable.TABLE_NAME)
                .toString();
        return mDatabase.createQuery(QuantityInfoTable.TABLE_NAME, sql)
                        .map(new Func1<SqlBrite.Query, List<String>>() {
                            @Override public List<String> call(SqlBrite.Query query) {
                                return convertYears(query.run());
                            }
                        });
    }

    private String getSql(String year, String month) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(QuantityInfoTable.TABLE_NAME);
        if (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(month)) {
            sb.append(" WHERE ");
        }
        if (!TextUtils.isEmpty(year)) {
            sb.append(" strftime('%Y',")
              .append(QuantityInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(year)
              .append("' ");
        }
        if (!TextUtils.isEmpty(year) && !TextUtils.isEmpty(month)) {
            sb.append(" AND ");
        }
        if (!TextUtils.isEmpty(month)) {
            sb.append(" strftime('%m',")
              .append(QuantityInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(month)
              .append("' ");
        }
        sb.append(" ORDER BY ").append(QuantityInfoTable.COLUMN_WORK_TIME).append(" DESC ");
        return sb.toString();
    }

    private Observable<List<QuantityInfo>> queryListBySql(String sql) {
        return mDatabase.createQuery(QuantityInfoTable.TABLE_NAME, sql)
                        .map(new Func1<SqlBrite.Query, List<QuantityInfo>>() {
                            @Override public List<QuantityInfo> call(SqlBrite.Query query) {
                                return convert(query.run());
                            }
                        });
    }

    private ContentValues convert(@NonNull QuantityInfo info, boolean isUpdate) {
        ContentValues values = new ContentValues();
        values.put(QuantityInfoTable.COLUMN_WORK_TIME, info.getWorkTime());
        values.put(QuantityInfoTable.COLUMN_TITLE, info.getTitle());
        values.put(QuantityInfoTable.COLUMN_QUANTITY, info.getQuantity());
        values.put(QuantityInfoTable.COLUMN_UNIT_PRICE, info.getUnitPrice());
        values.put(QuantityInfoTable.COLUMN_WEEK, info.getWeek());
        values.put(QuantityInfoTable.COLUMN_MULTIPLE, info.getMultiple());
        values.put(QuantityInfoTable.COLUMN_FORMAT_TIME, info.getFormatTime());
        values.put(QuantityInfoTable.COLUMN_SUBSIDY, info.getSubsidy());
        values.put(QuantityInfoTable.COLUMN_BONUS, info.getBonus());
        values.put(QuantityInfoTable.COLUMN_DEDUCTIONS, info.getDeductions());
        values.put(QuantityInfoTable.COLUMN_REMARK, info.getRemark());
        if (!isUpdate) {
            values.put(QuantityInfoTable.COLUMN_CREATE_TIME, info.getCreateTime());
        }
        return values;
    }

    private List<QuantityInfo> convert(Cursor cursor) {
        if(null == cursor) return null;
        List<QuantityInfo> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(new QuantityInfo(
                        cursor.getLong(cursor.getColumnIndex(QuantityInfoTable.COLUMN_ID)),
                        cursor.getLong(cursor.getColumnIndex(QuantityInfoTable.COLUMN_CREATE_TIME)),
                        cursor.getLong(cursor.getColumnIndex(QuantityInfoTable.COLUMN_WORK_TIME)),
                        cursor.getString(cursor.getColumnIndex(QuantityInfoTable.COLUMN_TITLE)),
                        cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_QUANTITY)),
                        cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_UNIT_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(QuantityInfoTable.COLUMN_WEEK)),
                        cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_MULTIPLE)),
                        cursor.getString(cursor.getColumnIndex(QuantityInfoTable.COLUMN_FORMAT_TIME)),
                        cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_SUBSIDY)),
                        cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_BONUS)),
                        cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_DEDUCTIONS)),
                        cursor.getString(cursor.getColumnIndex(QuantityInfoTable.COLUMN_REMARK))));
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }

    private List<String> convertYears(Cursor cursor) {
        if(null == cursor) return null;
        List<String> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }
}
