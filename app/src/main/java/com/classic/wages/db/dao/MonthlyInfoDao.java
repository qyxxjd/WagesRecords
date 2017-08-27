package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.classic.wages.consts.Consts;
import com.classic.wages.db.table.MonthlyInfoTable;
import com.classic.wages.entity.MonthlyInfo;
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

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.db.dao
 *
 * 文件描述：月工资数据查询实现类
 * 创 建 人：续写经典
 * 创建时间：16/10/27 下午7:19
 */
public class MonthlyInfoDao implements IDao<MonthlyInfo>, IBackup {
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

    @Override public MonthlyInfo query(long createTime) {
        MonthlyInfo monthlyInfo = null;
        final StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(MonthlyInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(MonthlyInfoTable.COLUMN_CREATE_TIME)
                .append(" = ")
                .append(createTime);
        Cursor cursor = mDatabase.query(sql.toString());
        if(null != cursor && cursor.moveToFirst()) {
            try {
                monthlyInfo = cursorToMonthlyInfo(cursor);
            } finally {
                CloseUtil.close(cursor);
            }
        }
        return monthlyInfo;
    }

    @Override
    public Observable<List<MonthlyInfo>> query(String year, String month) {
        return queryListBySql(getSql(year, month));
    }

    @Override public Observable<List<MonthlyInfo>> query(long startTime, long endTime) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
               .append(MonthlyInfoTable.TABLE_NAME)
               .append(" WHERE ")
               .append(MonthlyInfoTable.COLUMN_MONTHLY_TIME)
               .append(" between ")
               .append(startTime)
               .append(" AND ")
               .append(endTime);
        return queryListBySql(sb.toString());
    }

    private String getSql(String year, String month) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(MonthlyInfoTable.TABLE_NAME);
        if (!Util.isEmpty(year) || !Util.isEmpty(month)) {
            sb.append(" WHERE ");
        }
        if (!Util.isEmpty(year)) {
            sb.append(" strftime('%Y',")
              .append(MonthlyInfoTable.COLUMN_FORMAT_TIME)
              .append(")='")
              .append(year)
              .append("' ");
        }
        if (!Util.isEmpty(year) && !Util.isEmpty(month)) {
            sb.append(" AND ");
        }
        if (!Util.isEmpty(month)) {
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
        return queryListBySql(QUERY_ALL_SQL);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private static final String QUERY_ALL_SQL = new StringBuilder("SELECT * FROM ")
            .append(MonthlyInfoTable.TABLE_NAME)
            //.append(" ORDER BY ")
            //.append(MonthlyInfoTable.COLUMN_MONTHLY_TIME)
            //.append(" DESC ");
            .toString();

    @Override
    public Observable<List<String>> queryYears() {
        //no impl
        return null;
    }

    private Observable<List<MonthlyInfo>> queryListBySql(String sql) {
        return mDatabase.createQuery(MonthlyInfoTable.TABLE_NAME, sql)
                        .map(new Function<SqlBrite.Query, List<MonthlyInfo>>() {
                            @Override public List<MonthlyInfo> apply(
                                    @io.reactivex.annotations.NonNull SqlBrite.Query query) throws Exception {
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
                list.add(cursorToMonthlyInfo(cursor));
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }
    private MonthlyInfo cursorToMonthlyInfo(@NonNull Cursor cursor) {
        return new MonthlyInfo(
                cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_CREATE_TIME)),
                cursor.getInt(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_WEEK)),
                cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_MULTIPLE)),
                cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_SUBSIDY)),
                cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_BONUS)),
                cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_DEDUCTIONS)),
                cursor.getString(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_FORMAT_TIME)),
                cursor.getString(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_REMARK)),
                cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_LAST_UPDATE_TIME)),
                cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_MONTHLY_TIME)),
                cursor.getFloat(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_MONTHLY_WAGE)));
    }

    @Override public boolean backup(final File file) throws Exception{
        if(null == file || !file.exists()) {
            throw new NullPointerException("not found backup file");
        }
        Cursor cursor = mDatabase.query(QUERY_ALL_SQL);
        if (null == cursor) {
            return false;
        }
        final List<MonthlyInfo> backupData = convert(cursor);
        if(DataUtil.isEmpty(backupData)) return false;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            for (MonthlyInfo item : backupData) {
                fileWriter.write(MonthlyInfoDao.this.toString(item));
            }
        } finally {
            CloseUtil.close(fileWriter);
            CloseUtil.close(cursor);
        }
        LogUtil.d("月工资，备份条数：" + backupData.size());
        return true;
    }

    @Override public boolean restore(final File file) throws Exception{
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
                MonthlyInfo info = toMonthlyInfo(line);
                if(null != info) {
                    MonthlyInfo temp = query(info.getCreateTime());
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
        LogUtil.d("月工资，恢复条数：" + count);
        return true;
    }

    private static final int CORRECT_LENGTH = 13;

    private MonthlyInfo toMonthlyInfo(String content) {
        if (Util.isEmpty(content) || content.indexOf(Consts.BACKUP_SEPARATOR) <= 0) {
            return null;
        }
        final String[] data = content.split(Consts.BACKUP_SEPARATOR);
        return data.length == CORRECT_LENGTH &&
                       Integer.parseInt(data[0]) == ICalculationRules.RULES_MONTHLY
               ? new MonthlyInfo(Long.parseLong(data[1]), Long.parseLong(data[2]),
                                 Integer.parseInt(data[3]), Float.parseFloat(data[4]),
                                 Float.parseFloat(data[5]), Float.parseFloat(data[6]),
                                 Float.parseFloat(data[7]), data[8],
                                 (Consts.EMPTY_CONTENT.equals(data[9]) ? "" : data[9]),
                                 Long.parseLong(data[10]), Long.parseLong(data[11]),
                                 Float.parseFloat(data[12]))
               : null;
    }

    private String toString(MonthlyInfo item) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder().append(ICalculationRules.RULES_MONTHLY)
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
                                  .append(item.getMonthlyTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getMonthlyWage())
                                  .append(Consts.LINE_FEED)
                                  .toString();
    }


}
