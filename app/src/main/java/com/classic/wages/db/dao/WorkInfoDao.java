package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.classic.wages.consts.Consts;
import com.classic.wages.db.table.MonthlyInfoTable;
import com.classic.wages.db.table.WorkInfoTable;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.utils.CloseUtil;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.LogUtil;
import com.classic.wages.utils.Util;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class WorkInfoDao implements IDao<WorkInfo>, IBackup {
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

    @Override public WorkInfo query(long createTime) {
        WorkInfo workInfo = null;
        final StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(WorkInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(WorkInfoTable.COLUMN_CREATE_TIME)
                .append(" = ")
                .append(createTime);
        Cursor cursor = mDatabase.query(sql.toString());
        if(null != cursor && cursor.moveToFirst()) {
            try {
                workInfo = cursorToWorkInfo(cursor);
            } finally {
                CloseUtil.close(cursor);
            }
        }
        return workInfo;
    }

    private WorkInfo cursorToWorkInfo(@NonNull Cursor cursor) {
        return new WorkInfo(
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_CREATE_TIME)),
                cursor.getInt(cursor.getColumnIndex(WorkInfoTable.COLUMN_WEEK)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_MULTIPLE)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_SUBSIDY)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_BONUS)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_DEDUCTIONS)),
                cursor.getString(cursor.getColumnIndex(WorkInfoTable.COLUMN_FORMAT_TIME)),
                cursor.getString(cursor.getColumnIndex(WorkInfoTable.COLUMN_REMARK)),
                cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_LAST_UPDATE_TIME)),
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_STARTING_TIME)),
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_END_TIME)),
                cursor.getInt(cursor.getColumnIndex(WorkInfoTable.COLUMN_REST_TIME)));
    }

    @Override public Observable<List<WorkInfo>> query(String year, String month) {
        return queryListBySql(getSql(year, month));
    }


    @Override public Observable<List<WorkInfo>> query(long startTime, long endTime) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(WorkInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(WorkInfoTable.COLUMN_STARTING_TIME)
                .append(" between ")
                .append(startTime)
                .append(" AND ")
                .append(endTime)
                .append(" ORDER BY ")
                .append(WorkInfoTable.COLUMN_STARTING_TIME)
                .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    @Override public Observable<List<WorkInfo>> queryCurrentMonth() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                                        .append(WorkInfoTable.TABLE_NAME)
                                        .append(" WHERE ")
                                        .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                        .append(" between datetime('now','start of month','+1 second') ")
                                        .append("AND datetime('now','start of month','+1 month','-1 second')")
                                        .append(" ORDER BY ")
                                        .append(WorkInfoTable.COLUMN_STARTING_TIME)
                                        .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    @Override public Observable<List<WorkInfo>> queryCurrentYear() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                                        .append(WorkInfoTable.TABLE_NAME)
                                        .append(" WHERE strftime('%Y',")
                                        .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                        .append(")=strftime('%Y',date('now')) ")
                                        .append(" ORDER BY ")
                                        .append(WorkInfoTable.COLUMN_STARTING_TIME)
                                        .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    @Override public Observable<List<WorkInfo>> queryAll() {
        return queryListBySql(getSql(null, null));
    }

    @Override public Observable<List<String>> queryYears() {
        final String sql = new StringBuilder("SELECT DISTINCT STRFTIME('%Y', ")
                .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                .append(") AS years FROM ")
                .append(WorkInfoTable.TABLE_NAME)
                .toString();
        return mDatabase.createQuery(WorkInfoTable.TABLE_NAME, sql)
                        .map(new Function<SqlBrite.Query, List<String>>() {
                            @Override public List<String> apply(@io.reactivex.annotations.NonNull SqlBrite.Query query)
                                    throws Exception {
                                return convertYears(query.run());
                            }
                        });
    }

    private String getSql(String year, String month) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME);
        if (!Util.isEmpty(year) || !Util.isEmpty(month)) {
            sb.append(" WHERE ");
        }
        if (!Util.isEmpty(year)) {
            sb.append(" strftime('%Y',")
              .append(WorkInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(year)
              .append("' ");
        }
        if (!Util.isEmpty(year) && !Util.isEmpty(month)) {
            sb.append(" AND ");
        }
        if (!Util.isEmpty(month)) {
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
                        .map(new Function<SqlBrite.Query, List<WorkInfo>>() {
                            @Override public List<WorkInfo> apply(
                                    @io.reactivex.annotations.NonNull SqlBrite.Query query) throws Exception {
                                return convert(query.run());
                            }
                        });
    }

    private ContentValues convert(@NonNull WorkInfo workInfo, boolean isUpdate) {
        ContentValues values = new ContentValues();
        values.put(WorkInfoTable.COLUMN_STARTING_TIME, workInfo.getStartingTime());
        values.put(WorkInfoTable.COLUMN_END_TIME, workInfo.getEndTime());
        values.put(WorkInfoTable.COLUMN_REST_TIME, workInfo.getRestTime());
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
                list.add(cursorToWorkInfo(cursor));
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
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
            LogUtil.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }

    @Override public boolean backup(final File file) throws Exception {
        if(null == file || !file.exists()) {
            throw new NullPointerException("not found backup file");
        }
        @SuppressWarnings("StringBufferReplaceableByString")
        Cursor cursor = mDatabase.query(new StringBuilder("SELECT * FROM ")
                .append(WorkInfoTable.TABLE_NAME)
                .toString());
        if (null == cursor) {
            return false;
        }
        final List<WorkInfo> backupData = convert(cursor);
        if(DataUtil.isEmpty(backupData)) return false;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            for (WorkInfo item : backupData) {
                fileWriter.write(WorkInfoDao.this.toString(item));
            }
        } finally {
            CloseUtil.close(fileWriter);
            CloseUtil.close(cursor);
        }
        LogUtil.d("普通工资，备份条数：" + backupData.size());
        return true;
    }

    @Override public boolean restore(final File file) throws Exception {
        if(null == file || !file.exists()) {
            throw new NullPointerException("not found backup file");
        }
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        int count = 0;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file),
                    Consts.CHARTSET);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                WorkInfo info = toWorkInfo(line);
                if(null != info) {
                    WorkInfo temp = query(info.getCreateTime());
                    if(null == temp) {
                        insert(info);
                    } else if (temp.getLastUpdateTime() < info.getLastUpdateTime()) {
                        info.setId(temp.getId());
                        update(info);
                    }
                    ++count;
                }
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
            CloseUtil.close(bufferedReader);
            CloseUtil.close(inputStreamReader);
        }
        LogUtil.d("普通工资，恢复条数：" + count);
        return true;
    }

    private static final int CORRECT_LENGTH = 13;

    private WorkInfo toWorkInfo(String content) {
        if (Util.isEmpty(content) || content.indexOf(Consts.BACKUP_SEPARATOR) <= 0) {
            return null;
        }
        final String[] data = content.split(Consts.BACKUP_SEPARATOR);
        final int rulesType = Integer.parseInt(data[0]);
        if (rulesType != ICalculationRules.RULES_DEFAULT || data.length < CORRECT_LENGTH) {
            return null;
        }
        int restTime = data.length > 13 ? Integer.parseInt(data[13]) : 0;
        return new WorkInfo(Long.parseLong(data[1]), Long.parseLong(data[2]),
                              Integer.parseInt(data[3]), Float.parseFloat(data[4]),
                              Float.parseFloat(data[5]), Float.parseFloat(data[6]),
                              Float.parseFloat(data[7]), data[8],
                              (Consts.EMPTY_CONTENT.equals(data[9]) ? "" : data[9]),
                              Long.parseLong(data[10]), Long.parseLong(data[11]),
                              Long.parseLong(data[12]), restTime);
    }

    private String toString(WorkInfo item) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder().append(ICalculationRules.RULES_DEFAULT)
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getId())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getCreateTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getWeek())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getMultiple())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getSubsidy())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getBonus())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getDeductions())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getFormatTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(Util.isEmpty(item.getRemark())
                                          ? Consts.EMPTY_CONTENT
                                          : item.getRemark())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getLastUpdateTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getStartingTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getEndTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getRestTime())
                                  .append(Consts.LINE_FEED)
                                  .toString();
    }
}
