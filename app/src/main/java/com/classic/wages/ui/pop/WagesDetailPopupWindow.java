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
import cn.qy.util.activity.R;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonAdapter;
import com.classic.wages.consts.Consts;
import com.classic.wages.ui.widget.LineGridView;
import java.util.List;

/**
 * @author 续写经典
 * @date 2015/12/20
 */
public class WagesDetailPopupWindow extends PopupWindow {

    public WagesDetailPopupWindow(@NonNull final Activity activity, @NonNull List<String> items) {
        final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.pop_wages_detail, null);
        final LineGridView gridView = (LineGridView) view.findViewById(R.id.pop_wages_detail_gv);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        gridView.setAdapter(new Adapter(activity, items));
    }

    public void show(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private class Adapter extends CommonAdapter<String> {

        Adapter(Context context, List<String> data) {
            super(context, R.layout.item_wages_detail, data);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, String item, int position) {
            final String[] items = item.split(Consts.WAGES_DETAIL_SEPARATOR);
            helper.setText(R.id.item_wages_detail_label, items[0])
                  .setText(R.id.item_wages_detail_value, items[1]);
        }
    }
}
