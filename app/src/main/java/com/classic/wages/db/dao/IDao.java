package com.classic.wages.db.dao;

import android.support.annotation.NonNull;
import java.util.List;
import rx.Observable;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.db.dao
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午6:18
 */
public interface IDao<T> {

    long insert(@NonNull T t);

    void insert(@NonNull List<T> list);

    int update(@NonNull T t);

    int delete(long id);

    Observable<List<T>> query(Integer year, Integer month);

    Observable<List<T>> queryAll();

}
