package com.classic.wages.ui.rules.impl;

import android.support.v7.widget.RecyclerView;
import com.classic.wages.ui.rules.IRules;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.rules.impl
 *
 * 文件描述：默认规则
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:59
 */
public class DefaultRulesImpl implements IRules {

    public DefaultRulesImpl(){

    }

    @Override public RecyclerView.Adapter getAdapter() {
        return null;
    }

    @Override public void onDataQuery(Integer year, Integer month) {

    }
}
