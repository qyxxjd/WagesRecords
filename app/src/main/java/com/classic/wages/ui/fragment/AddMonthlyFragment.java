package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.entity.MonthlyInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.ToastUtil;
import com.classic.wages.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：月工资添加页面
 * 创 建 人：续写经典
 * 创建时间：16/10/20 下午4:08
 */
public class AddMonthlyFragment extends AppBaseFragment implements AddActivity.Listener,
                                                            TimePickerView.OnTimeSelectListener {

    private static final String FORMAT   = "yyyy-MM";
    private static final float  ZERO     = 0f;

    @BindView(R.id.add_monthly_time_hint) TextView mMonthlyTimeHint;
    @BindView(R.id.add_monthly_time)      TextView mMonthlyTime;

    @BindView(R.id.add_monthly_wage) MaterialEditText mMonthlyWage;
    @BindView(R.id.add_bonus)        MaterialEditText mBonus;
    @BindView(R.id.add_deductions)   MaterialEditText mDeductions;
    @BindView(R.id.add_subsidy)      MaterialEditText mSubsidy;
    @BindView(R.id.add_remark)       MaterialEditText mRemark;

    @Inject MonthlyInfoDao mMonthlyInfoDao;

    private int            mType;
    private MonthlyInfo    mMonthlyInfo;
    /** 当前选择的开始时间 */
    private Long           mCurrentTime;
    private TimePickerView mTimePickerView;


    public static AddMonthlyFragment newInstance(@AddActivity.AddTypes int type, BasicInfo basicInfo) {
        Bundle args = new Bundle();
        args.putInt(AddActivity.PARAMS_TYPE, type);
        if (null != basicInfo) {
            args.putSerializable(AddActivity.PARAMS_BASIC_INFO, basicInfo);
        }
        AddMonthlyFragment fragment = new AddMonthlyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_add_monthly;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        ((WagesApplication)mActivity.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        initParams();
    }

    @Override public void onTimeSelect(Date date, View view) {
        mCurrentTime = date.getTime();
        mMonthlyTime.setText(DateUtil.formatDate(FORMAT, mCurrentTime));
        mMonthlyTimeHint.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_monthly_time) void onMonthlyTimeClick(){
        showDatePicker(null== mCurrentTime ? new Date() : new Date(mCurrentTime));
    }

    @Override public void onAdd() {
        if (checkParams()) {
            final float monthlyWage = Float.valueOf(mMonthlyWage.getText().toString().trim());
            final MonthlyInfo info = new MonthlyInfo(mCurrentTime, monthlyWage);
            updateInfo(info);
            if(mMonthlyInfoDao.insert(info) > 0L){
                ToastUtil.showToast(mAppContext, R.string.add_success);
                mActivity.finish();
            } else {
                ToastUtil.showToast(mAppContext, R.string.add_failure);
            }
        }
    }

    @Override public void onModify() {
        if(checkParams()){
            final float monthlyWage = Float.valueOf(mMonthlyWage.getText().toString().trim());
            mMonthlyInfo.setMonthlyTime(mCurrentTime);
            mMonthlyInfo.setMonthlyWage(monthlyWage);
            updateInfo(mMonthlyInfo);
            mMonthlyInfo.setLastUpdateTime(System.currentTimeMillis());
            if(mMonthlyInfoDao.update(mMonthlyInfo) > 0){
                ToastUtil.showToast(mAppContext, R.string.modify_success);
                mActivity.finish();
            } else {
                ToastUtil.showToast(mAppContext, R.string.modify_failure);
            }
        }
    }

    private void updateInfo(@NonNull MonthlyInfo info){
        info.setBonus(Util.getNumber(mBonus));
        info.setSubsidy(Util.getNumber(mSubsidy));
        info.setDeductions(Util.getNumber(mDeductions));
        if(!TextUtils.isEmpty(mRemark.getText().toString())){
            info.setRemark(mRemark.getText().toString().trim());
        }
    }

    private void showDatePicker(Date date) {
        mTimePickerView = createPickerView(mActivity, this, date.getTime(), false);
        mTimePickerView.show();
    }

    private void initParams(){
        final Bundle bundle = getArguments();
        mType = bundle.getInt(AddActivity.PARAMS_TYPE);
        if(mType == AddActivity.TYPE_MODIFY){
            mMonthlyInfo = (MonthlyInfo) bundle.getSerializable(AddActivity.PARAMS_BASIC_INFO);
            if(null == mMonthlyInfo){
                mActivity.finish();
                return;
            }
            setValues(mMonthlyInfo);
        }
    }

    private void setValues(@NonNull MonthlyInfo info){
        mCurrentTime = info.getMonthlyTime();
        mMonthlyTime.setText(DateUtil.formatDate(FORMAT, mCurrentTime));
        mMonthlyTimeHint.setVisibility(View.VISIBLE);
        Util.setText(mMonthlyWage, info.getMonthlyWage());

        if(info.getBonus() > ZERO){
            Util.setText(mBonus, info.getBonus());
        }
        if(info.getDeductions() > ZERO){
            Util.setText(mDeductions, info.getDeductions());
        }
        if(info.getSubsidy() > ZERO){
            Util.setText(mSubsidy, info.getSubsidy());
        }
        if(!TextUtils.isEmpty(info.getRemark())){
            Util.setText(mRemark, info.getRemark());
        }
    }

    private boolean checkParams() {
        if (null == mCurrentTime) {
            ToastUtil.showToast(mAppContext, R.string.add_monthly_time_empty);
            return false;
        }
        if(TextUtils.isEmpty(mMonthlyWage.getText().toString())){
            ToastUtil.showToast(mAppContext, R.string.add_monthly_wage_empty);
            return false;
        }
        return true;
    }
}
