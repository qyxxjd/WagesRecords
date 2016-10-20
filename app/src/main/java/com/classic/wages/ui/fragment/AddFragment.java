package com.classic.wages.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.classic.core.utils.ToastUtil;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：默认添加页面
 * 创 建 人：续写经典
 * 创建时间：16/10/20 下午4:08
 */
public class AddFragment extends AppBaseFragment implements AddActivity.Listener {

    @BindView(R.id.add_start_time_hint) TextView mStartTimeHint;
    @BindView(R.id.add_start_time)      TextView mStartTime;
    @BindView(R.id.add_end_time_hint)   TextView mEndTimeHint;
    @BindView(R.id.add_end_time)        TextView mEndTime;

    @BindView(R.id.add_holiday_double) CheckBox mHolidayDouble;
    @BindView(R.id.add_holiday_three)  CheckBox mHolidayThree;
    @BindView(R.id.add_holiday_custom) CheckBox mHolidayCustom;

    @BindView(R.id.add_bonus)                MaterialEditText mBonus;
    @BindView(R.id.add_deductions)           MaterialEditText mDeductions;
    @BindView(R.id.add_subsidy)              MaterialEditText mSubsidy;
    @BindView(R.id.add_holiday_custom_value) MaterialEditText mHolidayCustomValue;

    public static AddFragment newInstance(BasicInfo basicInfo) {
        Bundle args = new Bundle();
        if (null != basicInfo) {
            args.putSerializable(AddActivity.PARAMS_BASIC_INFO, basicInfo);
        }
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_add;
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
    }

    @OnClick({ R.id.add_start_time, R.id.add_end_time }) public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_start_time:
                ToastUtil.showToast(mAppContext, "startTime");
                break;
            case R.id.add_end_time:
                ToastUtil.showToast(mAppContext, "endTime");
                break;
        }
    }

    @Override public void onAdd() {
        ToastUtil.showToast(mAppContext, "onAdd");
    }

    @Override public void onModify() {
        ToastUtil.showToast(mAppContext, "onModify");
    }
}
