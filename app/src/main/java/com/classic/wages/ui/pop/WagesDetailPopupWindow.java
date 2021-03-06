package com.classic.wages.ui.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.widget.LineGridView;
import com.classic.wages.utils.DataUtil;
import com.classic.wages.utils.MoneyUtil;

import java.util.List;

import cn.qy.util.activity.R;

/**
 * @author 续写经典
 * @date 2015/12/20
 */
public class WagesDetailPopupWindow extends PopupWindow {

    public WagesDetailPopupWindow(@NonNull final Activity activity, @NonNull List<String> items) {
        this(activity, items, null);
    }


    public WagesDetailPopupWindow(@NonNull final Activity activity, @NonNull List<String> items, List<String> groups) {
        final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.pop_wages_detail, null);
        final LineGridView detailGrid = (LineGridView) view.findViewById(R.id.pop_wages_detail_gv);
        final ListView groupGrid = (ListView) view.findViewById(R.id.pop_wages_group_lv);
        final View divider = view.findViewById(R.id.pop_wages_divider);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        detailGrid.setAdapter(new Adapter(activity, items));
        final boolean visibility = !DataUtil.isEmpty(groups);
        setVisibility(groupGrid, visibility);
        setVisibility(divider, visibility);
        if (visibility) {
            groupGrid.setAdapter(new Adapter(activity, groups));
        }
    }

    private void setVisibility(View view, boolean visibility) {
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void show(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private static final class Adapter extends CommonAdapter<String> {

        Adapter(Context context, List<String> data) {
            super(context, R.layout.item_wages_detail, data);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, String item, int position) {
            final String[] items = item.split(Consts.WAGES_DETAIL_SEPARATOR);
            String value;
            try {
                value = MoneyUtil.replace(items[1], Consts.DEFAULT_SCALE);
            } catch (NumberFormatException e) {
                value = items[1];
            }
            helper.setText(R.id.item_wages_detail_label, items[0])
                  .setText(R.id.item_wages_detail_value, value);
        }
    }
}
