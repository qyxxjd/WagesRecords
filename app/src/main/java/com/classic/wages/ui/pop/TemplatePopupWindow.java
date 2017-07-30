package com.classic.wages.ui.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.wages.entity.QuantityInfo;

import java.util.List;

import cn.qy.util.activity.R;

public class TemplatePopupWindow extends PopupWindow implements CommonRecyclerAdapter.OnItemClickListener{

    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private Adapter  mAdapter;
    private Listener mListener;
    private List<QuantityInfo> mDataSource;

    private TemplatePopupWindow(Builder builder) {
        mActivity = builder.mActivity;
        mListener = builder.mListener;
        mDataSource = builder.mDataSource;

        final LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.pop_template, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pop_template_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int i) {
        dismiss();
        if (null != mListener) {
            mListener.onItemClick(viewHolder, view, i);
        }
    }

    public interface Listener {
        void onDismiss();

        void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int i);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (null != mListener) {
            mListener.onDismiss();
        }
    }

    public void show(View parent) {
        if (null == mAdapter) {
            mAdapter = new Adapter(mActivity, mDataSource);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (!this.isShowing()) {
            this.setFocusable(true);
            this.setOutsideTouchable(true);
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private final class Adapter extends CommonRecyclerAdapter<QuantityInfo> {

        Adapter(Context context, List<QuantityInfo> data) {
            super(context, R.layout.item_template, data);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, QuantityInfo item, int position) {
            helper.setText(R.id.item_template_title, item.getTitle());
        }
    }

    public static final class Builder {
        private Activity mActivity;
        private Listener mListener;
        private List<QuantityInfo> mDataSource;

        public Builder() { }

        public Builder activity(Activity val) {
            mActivity = val;
            return this;
        }

        public Builder listener(Listener val) {
            mListener = val;
            return this;
        }

        public Builder dataSource(List<QuantityInfo> val) {
            mDataSource = val;
            return this;
        }

        public TemplatePopupWindow build() {
            return new TemplatePopupWindow(this);
        }
    }
}
