package com.classic.wages.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.adapter.interfaces.ImageLoad;
import com.classic.core.utils.IntentUtil;
import com.classic.wages.ui.base.AppBaseActivity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;


/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.activity
 *
 * 文件描述: 开源代码许可证
 * 创 建 人: 续写经典
 * 创建时间: 2016/10/26 18:40
 */

public class OpenSourceLicensesActivity extends AppBaseActivity implements CommonRecyclerAdapter.OnItemClickListener{

    @BindView(R.id.licenses_recycler_view) RecyclerView mRecyclerView;

    public static void start(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, OpenSourceLicensesActivity.class));
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override public int getLayoutResId() {
        return R.layout.activity_open_source_licenses;
    }

    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle(R.string.setting_licenses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final LicensesAdapter adapter = new LicensesAdapter(mActivity,
                                                            R.layout.item_open_source_licenses,
                                                            LICENSE_ARRAY);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        IntentUtil.browser(mActivity, LICENSE_ARRAY.get(position).projectUrl);
    }

    private static class LicenseItem {
        String title;
        String author;
        String licenseType;
        String logoUrl;
        String projectUrl;

        LicenseItem(String title, String author, String licenseType, String logoUrl, String projectUrl) {
            this.title = title;
            this.author = author;
            this.licenseType = licenseType;
            this.logoUrl = logoUrl;
            this.projectUrl = projectUrl;
        }
    }

    private static final ImageLoad  PICASSO_IMAGE_LOAD = new ImageLoad() {
        @Override public void load(Context context, ImageView imageView, String imageUrl) {
            Picasso.with(context)
                   .load(imageUrl)
                   .placeholder(R.drawable.ic_github)
                   .into(imageView);
        }
    };

    private final class LicensesAdapter extends CommonRecyclerAdapter<LicenseItem>{

        LicensesAdapter(Context context, int layoutResId, List<LicenseItem> data) {
            super(context, layoutResId, data);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, LicenseItem item, int position) {
            helper.setImageLoad(PICASSO_IMAGE_LOAD)
                  .setImageUrl(R.id.licenses_item_logo, item.logoUrl)
                  .setText(R.id.licenses_item_title, item.title)
                  .setText(R.id.licenses_item_author, item.author)
                  .setText(R.id.licenses_item_type, item.licenseType);
        }
    }

    private static final String LICENSE_TYPE_MIT    = "MIT";
    private static final String LICENSE_TYPE_APACHE = "Apache2.0";
    private static final ArrayList<LicenseItem> LICENSE_ARRAY = new ArrayList<>();
    static {
        LICENSE_ARRAY.add(new LicenseItem("CommonAdapter", "续写经典", LICENSE_TYPE_MIT,
                                          "https://avatars1.githubusercontent.com/u/10043599?v=3&s=466",
                                          "https://github.com/qyxxjd/CommonAdapter"));
        LICENSE_ARRAY.add(new LicenseItem("AndroidBasicProject", "续写经典", LICENSE_TYPE_MIT,
                                          "https://avatars1.githubusercontent.com/u/10043599?v=3&s=466",
                                          "https://github.com/qyxxjd/AndroidBasicProject"));
        LICENSE_ARRAY.add(new LicenseItem("RxJava", "ReactiveX", LICENSE_TYPE_APACHE,
                                          "https://avatars1.githubusercontent.com/u/6407041?v=3&s=200",
                                          "https://github.com/ReactiveX/RxJava"));
        LICENSE_ARRAY.add(new LicenseItem("RxAndroid", "ReactiveX", LICENSE_TYPE_APACHE,
                                          "https://avatars1.githubusercontent.com/u/6407041?v=3&s=200",
                                          "https://github.com/ReactiveX/RxAndroid"));
        LICENSE_ARRAY.add(new LicenseItem("Butter Knife", "Jake Wharton", LICENSE_TYPE_APACHE,
                                          "https://avatars1.githubusercontent.com/u/66577?v=3&s=466",
                                          "https://github.com/JakeWharton/butterknife"));
        LICENSE_ARRAY.add(new LicenseItem("Picasso", "Square", LICENSE_TYPE_APACHE,
                                          "https://avatars2.githubusercontent.com/u/82592?v=3&s=200",
                                          "https://github.com/square/picasso"));
        LICENSE_ARRAY.add(new LicenseItem("LeakCanary", "Square", LICENSE_TYPE_APACHE,
                                          "https://avatars2.githubusercontent.com/u/82592?v=3&s=200",
                                          "https://github.com/square/leakcanary"));
        LICENSE_ARRAY.add(new LicenseItem("SQLBrite", "Square", LICENSE_TYPE_APACHE,
                                          "https://avatars2.githubusercontent.com/u/82592?v=3&s=200",
                                          "https://github.com/square/sqlbrite"));
        LICENSE_ARRAY.add(new LicenseItem("Dagger 2", "Google", LICENSE_TYPE_APACHE,
                                          "https://avatars0.githubusercontent.com/u/1342004?v=3&s=200",
                                          "https://github.com/google/dagger"));
        LICENSE_ARRAY.add(new LicenseItem("AndroidPerformanceMonitor", "MarkZhai", LICENSE_TYPE_APACHE,
                                          "https://avatars3.githubusercontent.com/u/1106500?v=3&s=466",
                                          "https://github.com/markzhai/AndroidPerformanceMonitor"));
        //LICENSE_ARRAY.add(new LicenseItem("Glide", "Bump Technologies", "BSD/MIT/Apache2.0",
        //                                  "https://avatars3.githubusercontent.com/u/423539?v=3&s=200",
        //                                  "https://github.com/bumptech/glide"));
        LICENSE_ARRAY.add(new LicenseItem("NavigationTabBar", "Devlight", LICENSE_TYPE_APACHE,
                                          "https://avatars2.githubusercontent.com/u/18118313?v=3&s=200",
                                          "https://github.com/DevLight-Mobile-Agency/NavigationTabBar"));
        LICENSE_ARRAY.add(new LicenseItem("CircleImageView", "Henning Dodenhof", LICENSE_TYPE_APACHE,
                                          "https://avatars3.githubusercontent.com/u/1824223?v=3&s=466",
                                          "https://github.com/hdodenhof/CircleImageView"));
        LICENSE_ARRAY.add(new LicenseItem("Material Dialogs", "Aidan Follestad", LICENSE_TYPE_MIT,
                                          "https://avatars2.githubusercontent.com/u/1820165?v=3&s=466",
                                          "https://github.com/afollestad/material-dialogs"));
        LICENSE_ARRAY.add(new LicenseItem("MaterialEditText", "扔物线", LICENSE_TYPE_APACHE,
                                          "https://avatars0.githubusercontent.com/u/4454687?v=3&s=466",
                                          "https://github.com/rengwuxian/MaterialEditText"));
        LICENSE_ARRAY.add(new LicenseItem("Material Spinner", "Jared Rummler", LICENSE_TYPE_APACHE,
                                          "https://avatars1.githubusercontent.com/u/6203389?v=3&s=466",
                                          "https://github.com/jaredrummler/Material-Spinner"));
        LICENSE_ARRAY.add(new LicenseItem("PickerView", "Sai", "",
                                          "https://avatars1.githubusercontent.com/u/9945153?v=3&s=466",
                                          "https://github.com/saiwu-bigkoo/Android-PickerView"));
    }
}
