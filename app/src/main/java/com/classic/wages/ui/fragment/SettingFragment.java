package com.classic.wages.ui.fragment;

import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.rules.ICalculationRules;
import com.jaredrummler.materialspinner.MaterialSpinner;
import javax.inject.Inject;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：设置页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class SettingFragment extends AppBaseFragment implements MaterialSpinner.OnItemSelectedListener<String>{

    @BindView(R.id.setting_rules_spinner) MaterialSpinner mRulesSpinner;

    @Inject SharedPreferencesUtil mPreferencesUtil;

    private int mCurrentRules;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_setting;
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        mRulesSpinner.setItems(Consts.RULES_LIST);
        mRulesSpinner.setOnItemSelectedListener(this);
        mCurrentRules = mPreferencesUtil.getIntValue(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        mRulesSpinner.setSelectedIndex(mCurrentRules);
        refreshUIByRules(mCurrentRules);
    }

    @OnClick(R.id.setting_rules_detail) public void onRulesDetailClick(){
        //TODO
    }
    @OnClick(R.id.setting_update) public void onUpdateClick(){
        //TODO
    }
    @OnClick(R.id.setting_share) public void onShareClick(){
        //TODO
    }
    @OnClick(R.id.setting_feedback) public void onFeedbackClick(){
        //TODO
    }
    @OnClick(R.id.setting_author) public void onAuthorClick(){
        //TODO
    }
    @OnClick(R.id.setting_thanks) public void onThanksClick(){
        //TODO
    }

    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
        if(position == mCurrentRules) return;
        mCurrentRules = position;
        refreshUIByRules(mCurrentRules);
        //TODO 通知其它fragment
    }

    private void refreshUIByRules(int rules){
        //TODO
    }
}
