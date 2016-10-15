package com.classic.wages.db.dao;

import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.db.dao
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午6:18
 */
public interface IDao<T> {

    long insert(T t);

    void insert(List<T> list);

    int update(T t);

    int delete(long id);

    T query(long id);

    List<T> query(Integer year, Integer month);
}
