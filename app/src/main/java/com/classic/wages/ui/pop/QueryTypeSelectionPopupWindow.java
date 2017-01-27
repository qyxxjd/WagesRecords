package com.classic.wages.ui.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.qy.util.activity.R;
import com.classic.wages.consts.QueryType;

/**
 * @author 续写经典
 * @date 2015/12/20
 */
public class QueryTypeSelectionPopupWindow extends PopupWindow implements View.OnClickListener {

    private Listener mListener;

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_type_month:
                mListener.onQueryTypeChange(QueryType.QUERY_TYPE_MONTH);
                break;
            case R.id.query_type_date:
                mListener.onQueryTypeChange(QueryType.QUERY_TYPE_DATE);
                break;
        }
        dismiss();
    }

    public interface Listener {
        void onQueryTypeChange(@QueryType int type);
    }

    public QueryTypeSelectionPopupWindow(
            @NonNull final Activity activity, @QueryType int type, @NonNull Listener listener) {
        final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.pop_query_type_selection, null);
        final TextView tvMonth = (TextView) view.findViewById(R.id.query_type_month);
        final TextView tvDate = (TextView) view.findViewById(R.id.query_type_date);
        this.mListener = listener;
        tvMonth.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        if (type == QueryType.QUERY_TYPE_MONTH) {
            tvMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_selected, 0, 0, 0);
        } else if (type == QueryType.QUERY_TYPE_DATE) {
            tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_selected, 0, 0, 0);
        }
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    }

    public void show(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
