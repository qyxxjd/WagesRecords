package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.entity.BasicInfo;
import com.classic.wages.entity.QuantityInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.pop.TemplatePopupWindow;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.MoneyUtil;
import com.classic.wages.utils.ToastUtil;
import com.classic.wages.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：计件工资添加页面
 * 创 建 人：续写经典
 * 创建时间：16/10/20 下午4:08
 */
public class AddQuantityFragment extends AppBaseFragment implements AddActivity.Listener,
                                                            TimePickerView.OnTimeSelectListener {
    private static final String FORMAT = "yyyy-MM-dd HH:mm";
    private static final float ZERO    = 0f;

    @BindView(R.id.add_quantity_time_hint)  TextView         mWorkTimeHint;
    @BindView(R.id.add_quantity_time)       TextView         mWorkTime;
    @BindView(R.id.add_quantity_unit_price) MaterialEditText mUnitPrice;
    @BindView(R.id.add_quantity_count)      MaterialEditText mCount;
    @BindView(R.id.add_quantity_title)      MaterialEditText mTitle;
    @BindView(R.id.add_bonus)               MaterialEditText mBonus;
    @BindView(R.id.add_deductions)          MaterialEditText mDeductions;
    @BindView(R.id.add_subsidy)             MaterialEditText mSubsidy;
    @BindView(R.id.add_remark)              MaterialEditText mRemark;
    @BindView(R.id.add_template)            ImageButton      mTemplate;

    @Inject QuantityInfoDao mQuantityInfoDao;

    private int            mType;
    private QuantityInfo   mQuantityInfo;
    /** 当前选择的开始时间 */
    private Long           mCurrentTime;
    private TimePickerView mTimePickerView;

    private List<QuantityInfo> mTemplateList;


    public static AddQuantityFragment newInstance(@AddActivity.AddTypes int type, BasicInfo basicInfo) {
        Bundle args = new Bundle();
        args.putInt(AddActivity.PARAMS_TYPE, type);
        if (null != basicInfo) {
            args.putSerializable(AddActivity.PARAMS_BASIC_INFO, basicInfo);
        }
        AddQuantityFragment fragment = new AddQuantityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_add_quantity;
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
        mWorkTime.setText(DateUtil.formatDate(FORMAT, mCurrentTime));
        mWorkTimeHint.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_quantity_time) void onWorkTimeClick(){
        showDatePicker(null== mCurrentTime ? new Date() : new Date(mCurrentTime));
    }

    @OnClick(R.id.add_template) void onTemplateClick(){
        if (DataUtil.isEmpty(mTemplateList)) {
            loadTemplate();
            return;
        }
        togglePopWindow();
    }

    private void loadTemplate() {
        // TODO
        // 1. 查询已添加的物品列表
        // 2. 按添加次数倒叙排列
        // 3. togglePopWindow()

        // Test code
        mTemplateList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            QuantityInfo info = new QuantityInfo(System.currentTimeMillis(),
                    "有什么好用的Android Studio的插件值得推荐？" + (i+1), 1, 2);
            mTemplateList.add(info);
        }
        togglePopWindow();
    }

    private TemplatePopupWindow mTemplatePopupWindow;
    private void togglePopWindow() {
        if (null == mTemplatePopupWindow) {
            final TemplatePopupWindow.Listener listener = new TemplatePopupWindow.Listener() {
                @Override
                public void onDismiss() {
                    mTemplate.setImageResource(R.drawable.ic_arrow_down);
                }

                @Override
                public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int i) {
                    if (i < mTemplateList.size()) {
                        refreshByTemplate(mTemplateList.get(i));
                    }
                }
            };
            mTemplatePopupWindow = new TemplatePopupWindow.Builder()
                    .activity(mActivity)
                    .dataSource(mTemplateList)
                    .listener(listener)
                    .build();
        }
        mTemplatePopupWindow.show(mTitle);
        mTemplate.setImageResource(mTemplatePopupWindow != null && mTemplatePopupWindow.isShowing() ?
                R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
    }

    private void refreshByTemplate(@NonNull QuantityInfo info) {
        mTitle.setText(info.getTitle());
        Util.setText(mUnitPrice, info.getUnitPrice());
        Util.setFocus(mCount);
    }

    @Override public void onAdd() {
        if (checkParams()) {
            final String title = mTitle.getText().toString().trim();
            final float count = Float.valueOf(mCount.getText().toString().trim());
            final float unitPrice = Float.valueOf(mUnitPrice.getText().toString().trim());
            final QuantityInfo info = new QuantityInfo(mCurrentTime, title, count, unitPrice);
            updateInfo(info);
            if(mQuantityInfoDao.insert(info) > 0L){
                ToastUtil.showToast(mAppContext, R.string.add_success);
                mActivity.finish();
            } else {
                ToastUtil.showToast(mAppContext, R.string.add_failure);
            }
        }
    }

    @Override public void onModify() {
        if(checkParams()){
            final String title = mTitle.getText().toString().trim();
            final float count = Float.valueOf(mCount.getText().toString().trim());
            final float unitPrice = Float.valueOf(mUnitPrice.getText().toString().trim());
            mQuantityInfo.setWorkTime(mCurrentTime);
            mQuantityInfo.setTitle(title);
            mQuantityInfo.setQuantity(count);
            mQuantityInfo.setUnitrice(unitPrice);
            updateInfo(mQuantityInfo);
            mQuantityInfo.setLastUpdateTime(System.currentTimeMillis());
            if(mQuantityInfoDao.update(mQuantityInfo) > 0){
                ToastUtil.showToast(mAppContext, R.string.modify_success);
                mActivity.finish();
            } else {
                ToastUtil.showToast(mAppContext, R.string.modify_failure);
            }
        }
    }

    private void updateInfo(@NonNull QuantityInfo info){
        info.setBonus(Util.getNumber(mBonus));
        info.setSubsidy(Util.getNumber(mSubsidy));
        info.setDeductions(Util.getNumber(mDeductions));
        if(!TextUtils.isEmpty(mRemark.getText().toString())){
            info.setRemark(mRemark.getText().toString().trim());
        }
    }

    private void showDatePicker(Date date) {
        // mTimePickerView = new TimePickerView(mActivity, TimePickerView.Type.ALL);
        // mTimePickerView.setCyclic(false);
        // mTimePickerView.setCancelable(false);
        // mTimePickerView.setOnTimeSelectListener(this);
        // mTimePickerView.setRange(Consts.MIN_YEAR, Consts.MAX_YEAR);
        // mTimePickerView.setTime(date);
        // mTimePickerView.show();

        mTimePickerView = createTimePickerView(mActivity, this, date.getTime());
        mTimePickerView.show();
    }

    private void initParams(){
        final Bundle bundle = getArguments();
        mType = bundle.getInt(AddActivity.PARAMS_TYPE);
        if(mType == AddActivity.TYPE_MODIFY){
            mQuantityInfo = (QuantityInfo) bundle.getSerializable(AddActivity.PARAMS_BASIC_INFO);
            if(null == mQuantityInfo){
                mActivity.finish();
                return;
            }
            setValues(mQuantityInfo);
        }
    }

    private void setValues(@NonNull QuantityInfo info){
        mCurrentTime = info.getWorkTime();
        mWorkTime.setText(DateUtil.formatDate(FORMAT, mCurrentTime));
        mWorkTimeHint.setVisibility(View.VISIBLE);
        mTitle.setText(info.getTitle());
        mCount.setText(MoneyUtil.replace(info.getQuantity()));
        Util.setText(mUnitPrice, info.getUnitPrice());

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
        Util.setFocus(mCount);
    }

    private boolean checkParams() {
        if (null == mCurrentTime) {
            ToastUtil.showToast(mAppContext, R.string.add_quantity_time_empty);
            return false;
        }
        if(TextUtils.isEmpty(mTitle.getText().toString())){
            ToastUtil.showToast(mAppContext, R.string.add_quantity_title_empty);
            return false;
        }
        if(TextUtils.isEmpty(mCount.getText().toString())){
            ToastUtil.showToast(mAppContext, R.string.add_quantity_count_empty);
            return false;
        }
        if(TextUtils.isEmpty(mUnitPrice.getText().toString())){
            ToastUtil.showToast(mAppContext, R.string.add_quantity_unit_price_empty);
            return false;
        }
        return true;
    }
}
