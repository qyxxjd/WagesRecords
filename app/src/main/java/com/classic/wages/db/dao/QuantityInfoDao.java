package com.classic.wages.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.classic.wages.consts.Consts;
import com.classic.wages.db.table.MonthlyInfoTable;
import com.classic.wages.db.table.QuantityInfoTable;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.utils.CloseUtil;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.LogUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.db.dao
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/27 下午7:20
 */
public class QuantityInfoDao implements IDao<QuantityInfo>, IBackup {
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

    @Override public QuantityInfo query(long createTime) {
        QuantityInfo quantityInfo = null;
        final StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(QuantityInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(QuantityInfoTable.COLUMN_CREATE_TIME)
                .append(" = ")
                .append(createTime);
        Cursor cursor = mDatabase.query(sql.toString());
        if(null != cursor && cursor.moveToFirst()) {
            try {
                quantityInfo = cursorToQuantityInfo(cursor);
            } finally {
                CloseUtil.close(cursor);
            }
        }
        return quantityInfo;
    }

    private QuantityInfo cursorToQuantityInfo(@NonNull Cursor cursor) {
        return new QuantityInfo(
                cursor.getLong(cursor.getColumnIndex(QuantityInfoTable.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndex(QuantityInfoTable.COLUMN_CREATE_TIME)),
                cursor.getInt(cursor.getColumnIndex(QuantityInfoTable.COLUMN_WEEK)),
                cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_MULTIPLE)),
                cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_SUBSIDY)),
                cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_BONUS)),
                cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_DEDUCTIONS)),
                cursor.getString(cursor.getColumnIndex(QuantityInfoTable.COLUMN_FORMAT_TIME)),
                cursor.getString(cursor.getColumnIndex(QuantityInfoTable.COLUMN_REMARK)),
                cursor.getLong(cursor.getColumnIndex(MonthlyInfoTable.COLUMN_LAST_UPDATE_TIME)),
                cursor.getLong(cursor.getColumnIndex(QuantityInfoTable.COLUMN_WORK_TIME)),
                cursor.getString(cursor.getColumnIndex(QuantityInfoTable.COLUMN_TITLE)),
                cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_QUANTITY)),
                cursor.getFloat(cursor.getColumnIndex(QuantityInfoTable.COLUMN_UNIT_PRICE)));
    }

    @Override public Observable<List<QuantityInfo>> query(String year, String month) {
        return queryListBySql(getSql(year, month));
    }

    @Override public Observable<List<QuantityInfo>> query(long startTime, long endTime) {
        final StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(QuantityInfoTable.TABLE_NAME)
                .append(" WHERE ")
                .append(QuantityInfoTable.COLUMN_WORK_TIME)
                .append(" between ")
                .append(startTime)
                .append(" AND ")
                .append(endTime)
                .append(" ORDER BY ")
                .append(QuantityInfoTable.COLUMN_WORK_TIME)
                .append(" DESC ");
        return queryListBySql(sb.toString());
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
                        .map(new Function<SqlBrite.Query, List<String>>() {
                            @Override public List<String> apply(@io.reactivex.annotations.NonNull SqlBrite.Query query)
                                    throws Exception {
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
                        .map(new Function<SqlBrite.Query, List<QuantityInfo>>() {
                            @Override public List<QuantityInfo> apply(
                                    @io.reactivex.annotations.NonNull SqlBrite.Query query) throws Exception {
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
        if (null == cursor) {
            return null;
        }
        List<QuantityInfo> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(cursorToQuantityInfo(cursor));
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        } finally {
            CloseUtil.close(cursor);
        }
        return list;
    }

    private List<String> convertYears(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
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

    @Override public void backup(final File file, final Listener listener) {
        if(null == file || !file.exists()) {
            if (null != listener) {
                listener.onError(new NullPointerException("not found backup file"));
            }
            return;
        }
        @SuppressWarnings("StringBufferReplaceableByString")
        Cursor cursor = mDatabase.query(new StringBuilder("SELECT * FROM ")
                .append(QuantityInfoTable.TABLE_NAME)
                .toString());
        if (null == cursor) {
            if (null != listener) {
                listener.onComplete();
            }
            return;
        }
        final List<QuantityInfo> backupData = convert(cursor);
        if(DataUtil.isEmpty(backupData)) return;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            long count = 1;
            for (QuantityInfo item : backupData) {
                fileWriter.write(QuantityInfoDao.this.toString(item));
                if (null != listener) {
                    listener.onProgress(count, backupData.size());
                }
                count++;
            }
            if (null != listener) {
                listener.onComplete();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onError(e);
            }
        } finally {
            CloseUtil.close(fileWriter);
            CloseUtil.close(cursor);
        }
        //queryAll().compose(RxUtil.<List<QuantityInfo>>applySchedulers(RxUtil.IO_TRANSFORMER))
        //          .subscribe(new Action1<List<QuantityInfo>>() {
        //              @Override public void call(List<QuantityInfo> list) {
        //                  FileWriter fileWriter = null;
        //                  try {
        //                      fileWriter = new FileWriter(file, true);
        //                      long count = 1;
        //                      for (QuantityInfo item : list) {
        //                          fileWriter.write(QuantityInfoDao.this.toString(item));
        //                          if (null != listener) {
        //                              listener.onProgress(count, list.size());
        //                          }
        //                          count++;
        //                      }
        //                      if (null != listener) {
        //                          listener.onComplete();
        //                      }
        //                  } catch (IOException e) {
        //                      e.printStackTrace();
        //                      if (null != listener) {
        //                          listener.onError(e);
        //                      }
        //                  } finally {
        //                      CloseUtil.close(fileWriter);
        //                  }
        //              }
        //          }, new Action1<Throwable>() {
        //              @Override public void call(Throwable throwable) {
        //                  if (null != listener) {
        //                      listener.onError(throwable);
        //                  }
        //              }
        //          });
    }

    @Override public void restore(final File file, final Listener listener) {
        if(null == file || !file.exists()) {
            if (null != listener) {
                listener.onError(new NullPointerException("not found backup file"));
            }
            return;
        }
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file),
                    Consts.CHARTSET);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                QuantityInfo info = toQuantityInfo(line);
                if(null != info) {
                    QuantityInfo temp = query(info.getCreateTime());
                    if(null == temp) {
                        insert(info);
                    } else if (temp.getLastUpdateTime() < info.getLastUpdateTime()) {
                        info.setId(temp.getId());
                        update(info);
                    }
                }
            }
            if (null != listener) {
                listener.onComplete();
            }
            transaction.markSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onError(new Throwable(e));
            }
        } finally {
            transaction.end();
            CloseUtil.close(bufferedReader);
            CloseUtil.close(inputStreamReader);
        }
        //Observable.create(new Observable.OnSubscribe<List<QuantityInfo>>() {
        //              @Override public void call(Subscriber<? super List<QuantityInfo>> subscriber) {
        //                  List<QuantityInfo> infos = new ArrayList<>();
        //                  InputStreamReader inputreader = null;
        //                  BufferedReader buffreader = null;
        //                  try {
        //                      inputreader = new InputStreamReader(new FileInputStream(file),
        //                              Consts.CHARTSET);
        //                      buffreader = new BufferedReader(inputreader);
        //                      String line;
        //                      while ((line = buffreader.readLine()) != null) {
        //                          QuantityInfo info = toQuantityInfo(line);
        //                          if(null != info) {
        //                              infos.add(info);
        //                          }
        //                      }
        //                  } catch (IOException e) {
        //                      e.printStackTrace();
        //                      if (null != listener) {
        //                          listener.onError(new Throwable(e));
        //                      }
        //                  } finally {
        //                      CloseUtil.close(buffreader);
        //                      CloseUtil.close(inputreader);
        //                  }
        //                  subscriber.onNext(infos);
        //              }
        //          })
        //          .compose(RxUtil.<List<QuantityInfo>>applySchedulers(RxUtil.IO_TRANSFORMER))
        //          .subscribe(new Action1<List<QuantityInfo>>() {
        //              @Override public void call(List<QuantityInfo> list) {
        //                  if (!DataUtil.isEmpty(list)) {
        //                      final int size = list.size();
        //                      for (int i = 0; i < size; i++) {
        //                          if(null != listener) {
        //                              listener.onProgress(i+1, size);
        //                          }
        //                          QuantityInfo info = list.get(i);
        //                          QuantityInfo temp = query(info.getCreateTime());
        //                          if(null == temp) {
        //                              insert(info);
        //                          } else if (temp.getLastUpdateTime() < info
        //                                  .getLastUpdateTime()) {
        //                              info.setId(temp.getId());
        //                              update(info);
        //                          }
        //                      }
        //                  }
        //                  if (null != listener) {
        //                      listener.onComplete();
        //                  }
        //              }
        //          }, new Action1<Throwable>() {
        //              @Override public void call(Throwable throwable) {
        //                  if (null != listener) {
        //                      listener.onError(throwable);
        //                  }
        //              }
        //          });
    }

    private static final int CORRECT_LENGTH = 15;

    /*
    long id, long createTime, int week, float multiple,
                        float subsidy, float bonus, float deductions, String formatTime,
                        String remark, long lastUpdateTime, long workTime, String title,
                        float quantity, float unitPrice
     */
    private QuantityInfo toQuantityInfo(String content) {
        if (TextUtils.isEmpty(content) || content.indexOf(Consts.BACKUP_SEPARATOR) <= 0) {
            return null;
        }
        final String[] data = content.split(Consts.BACKUP_SEPARATOR);
        return data.length == CORRECT_LENGTH &&
                       Integer.parseInt(data[0]) == ICalculationRules.RULES_QUANTITY
               ? new QuantityInfo(Long.parseLong(data[1]), Long.parseLong(data[2]),
                                  Integer.parseInt(data[3]), Float.parseFloat(data[4]),
                                  Float.parseFloat(data[5]), Float.parseFloat(data[6]),
                                  Float.parseFloat(data[7]), data[8],
                                  (Consts.EMPTY_CONTENT.equals(data[9]) ? "" : data[9]),
                                  Long.parseLong(data[10]), Long.parseLong(data[11]), data[12],
                                  Float.parseFloat(data[13]), Float.parseFloat(data[14]))
               : null;
    }

    private String toString(QuantityInfo item) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder().append(ICalculationRules.RULES_QUANTITY)
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
                                  .append(TextUtils.isEmpty(item.getRemark())
                                          ? Consts.EMPTY_CONTENT
                                          : item.getRemark())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getLastUpdateTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getWorkTime())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getTitle())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getQuantity())
                                  .append(Consts.BACKUP_SEPARATOR)
                                  .append(item.getUnitPrice())
                                  .append(Consts.LINE_FEED)
                                  .toString();
    }
}
