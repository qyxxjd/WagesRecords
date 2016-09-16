package com.classic.wages.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.wages.app.AppBaseFragment;

/**
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/5/29 下午2:21
 */
public class MainFragment extends AppBaseFragment implements
        CommonRecyclerAdapter.OnItemClickListener,
        CommonRecyclerAdapter.OnItemLongClickListener {

    @BindView(R.id.main_recycler_view) RecyclerView         mRecyclerView;

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
    }

    @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
    }

    @Override public void onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, final int position) {
        //new MaterialDialog.Builder(activity).backgroundColorRes(R.color.white)
        //                                    .content(R.string.delete_dialog_content)
        //                                    .contentColorRes(R.color.primary_light)
        //                                    .positiveText(R.string.confirm)
        //                                    .negativeText(R.string.cancel)
        //                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
        //                                        @Override
        //                                        public void onClick(MaterialDialog dialog, DialogAction which) {
        //                                            int rows = mConsumerDao.delete(mAdapter.getItem(position).getId());
        //                                            ToastUtil.showToast(mAppContext,
        //                                                    rows > 0 ? R.string.delete_success : R.string.delete_fail);
        //                                            dialog.dismiss();
        //                                        }
        //                                    })
        //                                    .show();
    }

    @Override public void onCalculationRulesChange(int rules) {

    }
}
