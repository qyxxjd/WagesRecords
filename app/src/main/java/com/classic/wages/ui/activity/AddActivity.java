package com.classic.wages.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import cn.qy.util.activity.R;
import com.classic.core.fragment.BaseFragment;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.base.AppBaseActivity;
import com.classic.wages.ui.fragment.AddFragment;
import com.classic.wages.ui.fragment.AddMonthlyFragment;
import com.classic.wages.ui.fragment.AddQuantityFragment;
import com.classic.wages.ui.rules.ICalculationRules;


/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.activity
 *
 * 文件描述：添加数据
 * 创 建 人：续写经典
 * 创建时间：16/10/20 下午3:31
 */
public class AddActivity extends AppBaseActivity implements Toolbar.OnMenuItemClickListener {
    public static final int TYPE_ADD    = 0;
    public static final int TYPE_MODIFY = 1;

    private static final String PARAMS_RULES      = "rules";
    public  static final String PARAMS_TYPE       = "type";
    public  static final String PARAMS_BASIC_INFO = "basicInfo";

    private int       mType;
    private int       mRulesType;
    private BasicInfo mBasicInfo;

    @IntDef({ TYPE_ADD, TYPE_MODIFY }) public @interface AddTypes {}

    public interface Listener {
        void onAdd();

        void onModify();
    }

    public static void start(
            @NonNull Activity activity,
            @AddTypes int type, int rules, BasicInfo basicInfo) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra(PARAMS_TYPE, type);
        intent.putExtra(PARAMS_RULES, rules);
        if(null != basicInfo){
            intent.putExtra(PARAMS_BASIC_INFO, basicInfo);
        }
        activity.startActivity(intent);
    }

    @Override public int getLayoutResId() {
        return R.layout.activity_add;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mType == TYPE_ADD ? R.menu.add_menu : R.menu.modify_menu, menu);
        return true;
    }

    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        getToolbar().setOnMenuItemClickListener(this);
        initParams();
        initFragment(savedInstanceState);
    }

    @Override public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                ((Listener)getFragment()).onAdd();
                return true;
            case R.id.action_modify:
                ((Listener)getFragment()).onModify();
                return true;
        }
        return false;
    }

    private void initParams() {
        final Intent intent = getIntent();
        mType = intent.getIntExtra(PARAMS_TYPE, TYPE_ADD);
        mRulesType = intent.getIntExtra(PARAMS_RULES, ICalculationRules.RULES_DEFAULT);
        mBasicInfo = (BasicInfo) intent.getSerializableExtra(PARAMS_BASIC_INFO);

        setTitle(mType == TYPE_ADD ? R.string.add : R.string.modify);
    }

    private void initFragment(Bundle savedInstanceState) {
        if(null != savedInstanceState) return;
        BaseFragment fragment = null;
        switch (mRulesType) {
            case ICalculationRules.RULES_MONTHLY:
                fragment = AddMonthlyFragment.newInstance(mType, mBasicInfo);
                break;
            case ICalculationRules.RULES_QUANTITY:
                fragment = AddQuantityFragment.newInstance(mType, mBasicInfo);
                break;
            case ICalculationRules.RULES_DEFAULT:
            case ICalculationRules.RULES_FIXED_DAY:
            case ICalculationRules.RULES_FIXED_MONTH:
            case ICalculationRules.RULES_PIZZAHUT:
                fragment = AddFragment.newInstance(mType, mBasicInfo);
                break;
            default:
                break;
        }
        if(null == fragment){
            throw new IllegalArgumentException("Illegal argument!");
        }
        changeFragment(R.id.add_fragment_layout, fragment);
    }
}
