package com.classic.wages.ui.fragment;

import cn.qy.util.activity.R;
import com.classic.wages.app.AppBaseFragment;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/9/15 下午5:54
 */
public class ListFragment extends AppBaseFragment {

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_list;
    }
}
