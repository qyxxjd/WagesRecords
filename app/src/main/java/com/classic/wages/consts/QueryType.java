package com.classic.wages.consts;

import android.support.annotation.IntDef;

/**
 * 搜索类型
 */
@IntDef({ QueryType.QUERY_TYPE_MONTH, QueryType.QUERY_TYPE_DATE })
public @interface QueryType {
    /**
     * 按月份搜索
     */
    int QUERY_TYPE_MONTH = 1;
    /**
     * 按日期搜索
     */
    int QUERY_TYPE_DATE  = 2;
}
