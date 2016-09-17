package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.classic.core.log.Logger;
import com.classic.core.utils.CloseUtil;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.db.table.WorkInfoTable;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.ArrayList;
import java.util.List;

public class WorkInfoDao {
    private BriteDatabase mDatabase;

    public WorkInfoDao(@NonNull BriteDatabase database) {
        mDatabase = database;
    }

    public void add(WorkInfo workInfo) {
        ContentValues values = new ContentValues();
        values.put(WorkInfoTable.COLUMN_STARTING_TIME, workInfo.getStartingTime());
        values.put(WorkInfoTable.COLUMN_END_TIME, workInfo.getEndTime());
        values.put(WorkInfoTable.COLUMN_CREATE_TIME, workInfo.getCreateTime());
        values.put(WorkInfoTable.COLUMN_WEEK, workInfo.getWeek());
        values.put(WorkInfoTable.COLUMN_MULTIPLE, workInfo.getMultiple());
        values.put(WorkInfoTable.COLUMN_FORMAT_TIME, workInfo.getFormatTime());
        values.put(WorkInfoTable.COLUMN_SUBSIDY, workInfo.getSubsidy());
        values.put(WorkInfoTable.COLUMN_BONUS, workInfo.getBonus());
        values.put(WorkInfoTable.COLUMN_DEDUCTIONS, workInfo.getDeductions());
        values.put(WorkInfoTable.COLUMN_REMARK, workInfo.getRemark());
        mDatabase.insert(WorkInfoTable.TABLE_NAME, values);
    }

    public void add(List<WorkInfo> workInfos) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            for (WorkInfo item : workInfos) {
                add(item);
            }
            transaction.markSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transaction.end();
        }
    }

    public void update(WorkInfo workInfo) {
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
        mDatabase.update(WorkInfoTable.TABLE_NAME, values,
                WorkInfoTable.COLUMN_ID + "=" + workInfo.getId());
    }

    public void delete(long id) {
        mDatabase.delete(WorkInfoTable.TABLE_NAME, WorkInfoTable.COLUMN_ID + "=" + id);
    }

    /** 获取今天的数据 */
    public List<WorkInfo> queryToday() {
        //select * from qy_data where strftime('%d.%m.%Y', date(start)) = strftime('%d.%m.%Y', 'now')
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE strftime('%d.%m.%Y', date(")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(")) = strftime('%d.%m.%Y', 'now')");
        return queryListBySql(sb.toString());
    }

    /** 获取昨日的数据 */
    public List<WorkInfo> queryYesterday() {
        //select * from qy_data where date('now', '-1 days')=date(start)
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE date('now', '-1 days')=date(")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(" )");
        return queryListBySql(sb.toString());
    }

    /** 获取本周的数据 */
    public List<WorkInfo> queryCurrentWeek() {
        //select * from qy_data where start between datetime(date(datetime('now',strftime('-%w day','now'))),'+1 second') and datetime(date(datetime('now',(6 - strftime('%w day','now'))||' day','1 day')),'-1 second')
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE ")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(" between datetime(date(datetime('now',strftime('-%w day','now'))),'+1 second') ")
                                                                    .append("AND datetime(date(datetime('now',(6 - strftime('%w day','now'))||' day','1 day')),'-1 second')");
        return queryListBySql(sb.toString());
    }

    /** 获取本月的数据 */
    public List<WorkInfo> queryCurrentMonth() {
        //select * from qy_data where start between datetime('now','start of month','+1 second') and datetime('now','start of month','+1 month','-1 second')
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE ")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(" between datetime('now','start of month','+1 second') ")
                                                                    .append("AND datetime('now','start of month','+1 month','-1 second')")
                                                                    .append(" ORDER BY ")
                                                                    .append(WorkInfoTable.COLUMN_STARTING_TIME)
                                                                    .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    /** 获取本年的数据 */
    public List<WorkInfo> queryCurrentYear(int page, int pageSize) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE strftime('%Y',")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(")=strftime('%Y',date('now')) ")
                                                                    .append(" ORDER BY ")
                                                                    .append(WorkInfoTable.COLUMN_STARTING_TIME)
                                                                    .append(" DESC ")
                                                                    .append("LIMIT ")
                                                                    .append(pageSize);
        if (page > 1) {
            sb.append(" OFFSET ").append((page - 1) * pageSize);
        }
        return queryListBySql(sb.toString());
    }

    /** 获取本年的数据 */
    public List<WorkInfo> queryCurrentYear() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE strftime('%Y',")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(")=strftime('%Y',date('now')) ");
        return queryListBySql(sb.toString());
    }

    /**
     * 获取所有数据
     */
    public List<WorkInfo> queryAll() {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME);
        return queryListBySql(sb.toString());
    }

    /**
     * 获取所有数据
     *
     * @param page 取第几页数据
     * @param pageSize 每页取几条数据
     */
    public List<WorkInfo> queryAll(int page, int pageSize) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" ORDER BY ")
                                                                    .append(WorkInfoTable.COLUMN_STARTING_TIME)
                                                                    .append(" DESC ")
                                                                    .append("LIMIT ")
                                                                    .append(pageSize);
        if (page > 1) {
            sb.append(" OFFSET ").append((page - 1) * pageSize);
        }
        return queryListBySql(sb.toString());
    }

    /** 根据ID获取数据 */
    public WorkInfo queryById(long id) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE ")
                                                                    .append(WorkInfoTable.COLUMN_ID)
                                                                    .append("=")
                                                                    .append(id);
        return query(sb.toString());
    }

    /** 获取某月的数据 */
    public List<WorkInfo> queryByMonth(String year, String month) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ").append(WorkInfoTable.TABLE_NAME)
                                                                    .append(" WHERE strftime('%Y',")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(")='")
                                                                    .append(year)
                                                                    .append("' AND strftime('%m',")
                                                                    .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                    .append(")='")
                                                                    .append(month)
                                                                    .append("' ")
                                                                    .append(" ORDER BY ")
                                                                    .append(WorkInfoTable.COLUMN_STARTING_TIME)
                                                                    .append(" DESC ");
        return queryListBySql(sb.toString());
    }

    /**
     * 获取某年份工作了几天
     */
    public int getDaysByYear(String year) {
        final StringBuffer sb = new StringBuffer("SELECT count(distinct date(").append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                               .append(")) FROM ")
                                                                               .append(WorkInfoTable.TABLE_NAME)
                                                                               .append(" WHERE strftime('%Y',")
                                                                               .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                               .append(")='")
                                                                               .append(year)
                                                                               .append("' ");
        return queryCountBySql(sb.toString());
    }

    /**
     * 获取某月份工作了几天
     */
    public int getDaysByMonth(String year, String month) {
        final StringBuffer sb = new StringBuffer("SELECT count(distinct date(").append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                               .append(")) FROM ")
                                                                               .append(WorkInfoTable.TABLE_NAME)
                                                                               .append(" WHERE strftime('%Y',")
                                                                               .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                               .append(")='")
                                                                               .append(year)
                                                                               .append("' AND strftime('%m',")
                                                                               .append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                               .append(")='")
                                                                               .append(month)
                                                                               .append("' ");
        return queryCountBySql(sb.toString());
    }

    /**
     * 获取工作总天数
     */
    public int getAllDays() {
        final StringBuilder sb = new StringBuilder("SELECT count(distinct date(").append(WorkInfoTable.COLUMN_FORMAT_TIME)
                                                                                 .append(")) FROM ")
                                                                                 .append(WorkInfoTable.TABLE_NAME);
        return queryCountBySql(sb.toString());
    }

    private int queryCountBySql(String sql) {
        int count = 0;
        Cursor cursor = mDatabase.query(sql);
        if (cursor != null) {
            try {
                cursor.moveToNext();
                count = cursor.getInt(0);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            } finally {
                CloseUtil.close(cursor);
            }
        }
        return count;
    }

    private WorkInfo query(String sql) {
        WorkInfo info = null;
        Cursor cursor = mDatabase.query(sql);
        if (cursor != null) {
            try {
                cursor.moveToNext();
                info = convert(cursor);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            } finally {
                CloseUtil.close(cursor);
            }
        }
        return info;
    }

    private List<WorkInfo> queryListBySql(String sql) {
        List<WorkInfo> data = null;
        Cursor cursor = mDatabase.query(sql);
        if (cursor != null) {
            try {
                data = new ArrayList<>();
                while (cursor.moveToNext()) {
                    data.add(convert(cursor));
                }
            } catch (Exception e) {
                Logger.e(e.getMessage());
            } finally {
                CloseUtil.close(cursor);
            }
        }
        return data;
    }

    private WorkInfo convert(@NonNull Cursor cursor) {
        return new WorkInfo(cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_STARTING_TIME)),
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_END_TIME)),
                cursor.getLong(cursor.getColumnIndex(WorkInfoTable.COLUMN_CREATE_TIME)),
                cursor.getInt(cursor.getColumnIndex(WorkInfoTable.COLUMN_WEEK)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_MULTIPLE)),
                cursor.getString(cursor.getColumnIndex(WorkInfoTable.COLUMN_FORMAT_TIME)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_SUBSIDY)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_BONUS)),
                cursor.getFloat(cursor.getColumnIndex(WorkInfoTable.COLUMN_DEDUCTIONS)),
                cursor.getString(cursor.getColumnIndex(WorkInfoTable.COLUMN_REMARK))
        );
    }
}
