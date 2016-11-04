package com.classic.wages.ui.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import cn.qy.util.activity.R;
import com.classic.wages.ui.widget.LineGridView;
import java.util.List;

/**
 * @author 续写经典
 * @date 2015/12/20
 */
public class WagesDetailPopupWindow extends PopupWindow {

  public WagesDetailPopupWindow(final Activity activity, List<String> items) {
    final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View view = inflater.inflate(R.layout.pop_wages_detail, null);
    final LineGridView gridView = (LineGridView) view.findViewById(R.id.pop_wages_detail_gv);
    this.setContentView(view);
    this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    this.setFocusable(true);
    this.setOutsideTouchable(true);
    this.update();
    this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    this.setAnimationStyle(R.style.PopAnim);

    //TODO
    //lineGridView.setAdapter(new WagesInfoAdapter());
  }


  public void show(View parent){
    if(!this.isShowing()){
      this.showAsDropDown(parent,0,0);
    }else{
      this.dismiss();
    }
  }
}
