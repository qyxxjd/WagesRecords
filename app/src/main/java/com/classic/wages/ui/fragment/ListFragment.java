package com.classic.wages.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.wages.ui.base.AppBaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.ArrayList;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：列表、查询页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class ListFragment extends AppBaseFragment {

    @BindView(R.id.query_months_spinner) MaterialSpinner mMonthsSpinner;
    @BindView(R.id.query_years_spinner)  MaterialSpinner mYearsSpinner;
    @BindView(R.id.recycler_view)        RecyclerView    mRecyclerView;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        final ArrayList<String> temp = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            temp.add(String.valueOf(i));
        }
        mMonthsSpinner.setItems(temp);
        mYearsSpinner.setItems(temp);
    }

    @Override public void onCalculationRulesChange(int rules) {

    }
}
