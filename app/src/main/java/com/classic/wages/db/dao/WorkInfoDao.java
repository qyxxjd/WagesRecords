package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.classic.core.utils.CloseUtil;
import com.classic.wages.db.table.WorkInfoTable;
import com.classic.wages.entity.WorkInfo;
import com.orhanobut.logger.Logger;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

public class WorkInfoDao implements IDao<WorkInfo> {
    private BriteDatabase mDatabase;

    public WorkInfoDao(@NonNull BriteDatabase database) {
        mDatabase = database;
    }

    @Override public long insert(@NonNull WorkInfo workInfo) {
        return mDatabase.insert(WorkInfoTable.TABLE_NAME, convert(workInfo, false));
    }

    @Override public void insert(@NonNull List<WorkInfo> list) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            for (WorkInfo item : list) {
                insert(item);
            }
            transaction.markSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transaction.end();
        }
    }

    @Override public int update(@NonNull WorkInfo workInfo) {
        return mDatabase.update(WorkInfoTable.TABLE_NAME, convert(workInfo, true),
                                WorkInfoTable.COLUMN_ID + "=" + workInfo.getId());
    }

    @Override public int delete(long id) {
        return mDatabase.delete(WorkInfoTable.TABLE_NAME, WorkInfoTable.COLUMN_ID + "=" + id);
    }

    @Override public Observable<List<WorkInfo>> query(Integer year, Integer month) {
        return queryListBySql(getSql(year, month));
    }

    @Override public Observable<List<WorkInfo>> queryAll() {
        return queryListBySql(getSql(null, null));
    }

    private String getSql(Integer year, Integer month) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME);
        if (null != year || null != month) {
            sb.append(" WHERE ");
        }
        if (null != year) {
            sb.append(" strftime('%Y',")
              .append(WorkInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(year)
              .append("' ");
        }
        if (null != year && null != month) {
            sb.append(" AND ");
        }
        if (null != month) {
            sb.append(" strftime('%m',")
              .append(WorkInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(month)
              .append("' ");
        }
        sb.append(" ORDER BY ").append(WorkInfoTable.COLUMN_STARTING_TIME).append(" DESC ");
        return sb.toString();
    }

    private Observable<List<WorkInfo>> queryListBySql(String sql) {
        return mDatabase.createQuery(WorkInfoTable.TABLE_NAME, sql)
                        .map(new Func1<SqlBrite.Query, List<WorkInfo>>() {
                            @Override public List<WorkInfo> call(SqlBrite.Query query) {
                                return convert(query.run());
                            }
                        });
    }

    private ContentValues convert(@NonNull WorkInfo workInfo, boolean isUpdate) {
        ContentValues values = new ContentValues();
        values.put(WorkInfoTable.COLUMN_STARTING_TIME, workInfo.getStartingTime());
        values.put(WorkInfoTable.COLUMN_END_TIME, workInfo.getEndTime());
        values.put(WorkInfoTable.COLUMN_WEEK, workInfo.getWeek());
        values.put(WorkInfoTable.COLUMN_MULTIPLE, workInfo.getMultiple());
        values.put(WorkInfoTable.COLUMN_FORMAT_TIME, workInfo.getFormatTime());
        values.put(WorkInfoTable.COLUMN_SUBSIDY, workInfo.getSubsidy());
        values.put(WorkInfoTable.COLUMN_BONUS, workInfo.getBonus());
        values.put(WorkInfoTable.COLUMN_DEDUCTIONS, workInfo.getDeductions());
        values.put(WorkInfoTable.COLUMN_REMARK, workInfo.getRemark());
        if (!isUpdate) {
            values.put(WorkInfoTable.COLUMN_CREATE_TIME, workInfo.getCreateTime());
        }
        return values;
    }

    private List<WorkInfo> convert(Cursor cursor) {
        if(null == cursor) return null;
        List<WorkInfo> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(
                        new WorkInfo(cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_ID)),
                                     cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_STARTING_TIME)),
                                     cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_END_TIME)),
                                     cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_CREATE_TIME)),
                                     cursor.getInt(cursor.getColumnIndex(WorkInfoTable.COLUMN_WEEK)),
                                     cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_MULTIPLE)),
                                     cursor.getString(cursor.getColumnIndex(WorkInfoTable.COLUMN_FORMAT_TIME)),
                                     cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_SUBSIDY)),
                                     cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_BONUS)),
                                     cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_DEDUCTIONS)),
                                     cursor.getString(cursor.getColumnIndex(WorkInfoTable.COLUMN_REMARK)))
                );
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }
}
