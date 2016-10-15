package com.classic.wages.ui.fragment;

import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.base.AppBaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：设置页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class SettingFragment extends AppBaseFragment {

    @BindView(R.id.setting_rules_spinner) MaterialSpinner mRulesSpinner;
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_setting;
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        mRulesSpinner.setItems(Consts.RULES_LIST);
    }
}
