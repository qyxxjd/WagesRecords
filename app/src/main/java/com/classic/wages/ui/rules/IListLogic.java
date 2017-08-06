package com.classic.wages.ui.rules;

import android.support.v7.widget.RecyclerView;
import com.classic.wages.entity.BasicInfo;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.interfaces
 *
 * 文件描述：规范列表页面业务接口
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午17:30
 */
public interface IListLogic<T extends BasicInfo> {

    List<T> getData();

    /**
     * 当前规则的列表适配器
     *
     * @return
     */
    RecyclerView.Adapter getAdapter();

    /**
     * 数据查询
     *
     * @param year 查询的年份，null代表全部
     * @param month 查询的月份，null代表全部(1-9月份需要在前面加0)
     */
    void onDataQuery(String year, String month);

    /**
     * 数据查询
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    void onDataQuery(long startTime, long endTime);
}
