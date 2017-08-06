package com.classic.wages.db.dao;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.db.dao
 *
 * 文件描述：数据库查询接口
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午6:18
 */
public interface IDao<T> {

    long insert(@NonNull T t);

    void insert(@NonNull List<T> list);

    int update(@NonNull T t);

    int delete(long id);

    T query(long createTime);

    Observable<List<T>> query(String year, String month);

    Observable<List<T>> query(long startTime, long endTime);

    Observable<List<T>> queryCurrentMonth();

    Observable<List<T>> queryCurrentYear();

    Observable<List<T>> queryAll();

    Observable<List<String>> queryYears();
}
