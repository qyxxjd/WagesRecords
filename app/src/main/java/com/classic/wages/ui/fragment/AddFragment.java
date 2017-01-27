package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.bigkoo.pickerview.TimePickerView;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.ToastUtil;
import com.classic.wages.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Date;
import javax.inject.Inject;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：默认添加页面
 * 创 建 人：续写经典
 * 创建时间：16/10/20 下午4:08
 */
public class AddFragment extends AppBaseFragment implements AddActivity.Listener,
                                                            CompoundButton.OnCheckedChangeListener,
                                                            TimePickerView.OnTimeSelectListener {

    private static final String FORMAT   = "yyyy-MM-dd HH:mm";
    private static final float  ZERO     = 0f;

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
    @BindView(R.id.add_remark)               MaterialEditText mRemark;
    @BindView(R.id.add_holiday_custom_value) MaterialEditText mHolidayCustomValue;

    @Inject WorkInfoDao mWorkInfoDao;

    private int            mType;
    private WorkInfo       mWorkInfo;
    /** 当前是否选择的开始时间 */
    private boolean        isChooseStartTime;
    /** 当前选择的开始时间 */
    private Long           mCurrentStartTime;
    /** 当前选择的结束时间 */
    private Long           mCurrentEndTime;
    private TimePickerView mTimePickerView;

    public static AddFragment newInstance(@AddActivity.AddTypes int type, BasicInfo basicInfo) {
        Bundle args = new Bundle();
        args.putInt(AddActivity.PARAMS_TYPE, type);
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

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        ((WagesApplication)mActivity.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        initParams();
        mHolidayDouble.setOnCheckedChangeListener(this);
        mHolidayThree.setOnCheckedChangeListener(this);
        mHolidayCustom.setOnCheckedChangeListener(this);
    }

    @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        final int id = compoundButton.getId();
        mHolidayDouble.setChecked(id == R.id.add_holiday_double && isChecked);
        mHolidayThree.setChecked(id == R.id.add_holiday_three && isChecked);
        final boolean isSelectCustom = id == R.id.add_holiday_custom && isChecked;
        mHolidayCustom.setChecked(isSelectCustom);
        mHolidayCustomValue.setVisibility(isSelectCustom ? View.VISIBLE : View.GONE);
    }

    @Override public void onTimeSelect(Date date) {
        if (isChooseStartTime) {
            mCurrentStartTime = date.getTime();
            mStartTime.setText(DateUtil.formatDate(FORMAT, mCurrentStartTime));
            mStartTimeHint.setVisibility(View.VISIBLE);
        } else {
            mCurrentEndTime = date.getTime();
            mEndTime.setText(DateUtil.formatDate(FORMAT, mCurrentEndTime));
            mEndTimeHint.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.add_start_time) void onStartTimeClick(){
        isChooseStartTime = true;
        showDatePicker(null==mCurrentStartTime ? new Date() : new Date(mCurrentStartTime));
    }
    @OnClick(R.id.add_end_time) void onEndTimeClick(){
        isChooseStartTime = false;
        showDatePicker(null==mCurrentEndTime ? new Date() : new Date(mCurrentEndTime));
    }

    @Override public void onAdd() {
        if (checkParams()) {
            final WorkInfo info = new WorkInfo(mCurrentStartTime, mCurrentEndTime);
            updateWorkInfo(info);
            if(mWorkInfoDao.insert(info) > 0L){
                ToastUtil.showToast(mAppContext, R.string.add_success);
                mActivity.finish();
            } else {
                ToastUtil.showToast(mAppContext, R.string.add_failure);
            }
        }
    }

    @Override public void onModify() {
        if(checkParams()){
            mWorkInfo.setStartingTime(mCurrentStartTime);
            mWorkInfo.setEndTime(mCurrentEndTime);
            updateWorkInfo(mWorkInfo);
            mWorkInfo.setLastUpdateTime(System.currentTimeMillis());
            if(mWorkInfoDao.update(mWorkInfo) > 0){
                ToastUtil.showToast(mAppContext, R.string.modify_success);
                mActivity.finish();
            } else {
                ToastUtil.showToast(mAppContext, R.string.modify_failure);
            }
        }
    }

    private void updateWorkInfo(@NonNull WorkInfo info){
        if (mHolidayDouble.isChecked()) {
            info.setMultiple(2f);
        } else if (mHolidayThree.isChecked()) {
            info.setMultiple(3f);
        } else if (mHolidayCustom.isChecked()) {
            final float multiple = Float.valueOf(mHolidayCustomValue.getText().toString());
            info.setMultiple(multiple);
        }
        info.setBonus(Util.getNumber(mBonus));
        info.setSubsidy(Util.getNumber(mSubsidy));
        info.setDeductions(Util.getNumber(mDeductions));
        if(!TextUtils.isEmpty(mRemark.getText().toString())){
            info.setRemark(mRemark.getText().toString().trim());
        }
    }

    private void showDatePicker(Date date) {
        mTimePickerView = new TimePickerView(mActivity, TimePickerView.Type.ALL);
        mTimePickerView.setCyclic(false);
        mTimePickerView.setCancelable(false);
        mTimePickerView.setOnTimeSelectListener(this);
        mTimePickerView.setRange(Consts.MIN_YEAR, Consts.MAX_YEAR);
        mTimePickerView.setTime(date);
        mTimePickerView.show();
    }

    private void initParams(){
        final Bundle bundle = getArguments();
        mType = bundle.getInt(AddActivity.PARAMS_TYPE);
        if(mType == AddActivity.TYPE_MODIFY){
            mWorkInfo = (WorkInfo) bundle.getSerializable(AddActivity.PARAMS_BASIC_INFO);
            if(null == mWorkInfo){
                mActivity.finish();
                return;
            }
            setValues(mWorkInfo);
        }
    }

    private void setValues(@NonNull WorkInfo workInfo){
        mCurrentStartTime = workInfo.getStartingTime();
        mCurrentEndTime = workInfo.getEndTime();
        mStartTime.setText(DateUtil.formatDate(FORMAT, mCurrentStartTime));
        mEndTime.setText(DateUtil.formatDate(FORMAT, mCurrentEndTime));
        mStartTimeHint.setVisibility(View.VISIBLE);
        mEndTimeHint.setVisibility(View.VISIBLE);

        if(workInfo.getBonus() > ZERO){
            Util.setText(mBonus, workInfo.getBonus());
        }
        if(workInfo.getDeductions() > ZERO){
            Util.setText(mDeductions, workInfo.getDeductions());
        }
        if(workInfo.getSubsidy() > ZERO){
            Util.setText(mSubsidy, workInfo.getSubsidy());
        }
        if(!TextUtils.isEmpty(workInfo.getRemark())){
            Util.setText(mRemark, workInfo.getRemark());
        }

        if (workInfo.getMultiple() == 2f) {
            mHolidayDouble.setChecked(true);
        } else if (workInfo.getMultiple() == 3f) {
            mHolidayThree.setChecked(true);
        } else if (workInfo.getMultiple() > ZERO) {
            mHolidayCustom.setChecked(true);
            Util.setText(mHolidayCustomValue, workInfo.getMultiple());
            mHolidayCustomValue.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkParams() {
        if (null == mCurrentStartTime) {
            ToastUtil.showToast(mAppContext, R.string.add_start_time_empty);
            return false;
        }
        if (null == mCurrentEndTime) {
            ToastUtil.showToast(mAppContext, R.string.add_end_time_empty);
            return false;
        }
        if (mCurrentStartTime >= mCurrentEndTime) {
            ToastUtil.showToast(mAppContext, R.string.add_time_error);
            return false;
        }
        if (mHolidayCustom.isChecked() &&
                TextUtils.isEmpty(mHolidayCustomValue.getText().toString().trim())) {
            ToastUtil.showToast(mAppContext, R.string.add_holiday_custom_empty);
            return false;
        }
        return true;
    }

}
