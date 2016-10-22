package com.classic.wages.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.wages.ui.base.AppBaseFragment;

/**
 * 文件描述：首页
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午2:21
 */
public class MainFragment extends AppBaseFragment {

    @BindView(R.id.main_month_wages) TextView mMonthWages;
    @BindView(R.id.main_year_wages)  TextView mYearWages;
    @BindView(R.id.main_total_wages) TextView mTotalWages;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_main;
    }

    @TargetApi(Build.VERSION_CODES.DONUT) @Override
    public void initView(View parentView, Bundle savedInstanceState) {
        //((CarApplication) activity.getApplicationContext()).getAppComponent().inject(this);
        super.initView(parentView, savedInstanceState);

        mMonthWages.setText("2387.87");
        mYearWages.setText("6789.87");
        mTotalWages.setText("214536.87");
    }

    @Override public void onCalculationRulesChange(@Rules int rules) {

    }
}
