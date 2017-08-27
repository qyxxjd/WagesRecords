package com.classic.wages.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.classic.wages.entity.BasicInfo;
import com.classic.wages.entity.WorkInfo;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import cn.qy.util.activity.R;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：肯德基兼职添加页面
 * 创 建 人：续写经典
 * 创建时间：16/10/20 下午4:08
 */
public class AddKFCFragment extends AddFragment {

    @BindView(R.id.add_kfc_rest_time) MaterialEditText mRestTime;

    public static AddKFCFragment newInstance(@AddActivity.AddTypes int type, BasicInfo basicInfo) {
        Bundle args = new Bundle();
        args.putInt(AddActivity.PARAMS_TYPE, type);
        if (null != basicInfo) {
            args.putSerializable(AddActivity.PARAMS_BASIC_INFO, basicInfo);
        }
        AddKFCFragment fragment = new AddKFCFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_add_kfc;
    }

    @Override void updateWorkInfo(@NonNull WorkInfo info){
        super.updateWorkInfo(info);
        if (!Util.isEmpty(mRestTime.getText().toString())) {
            info.setRestTime(Integer.parseInt(mRestTime.getText().toString()));
        }
    }

    @Override void setValues(@NonNull WorkInfo workInfo){
        super.setValues(workInfo);
        if (workInfo.getRestTime() > 0) {
            mRestTime.setText(String.valueOf(workInfo.getRestTime()));
        }
    }

}
