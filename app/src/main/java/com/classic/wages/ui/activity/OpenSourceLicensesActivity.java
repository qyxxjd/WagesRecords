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
import butterknife.BindView;
import cn.qy.util.activity.R;
import com.classic.adapter.BaseAdapterHelper;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.core.utils.IntentUtil;
import com.classic.wages.ui.base.AppBaseActivity;
import com.classic.wages.utils.GlideImageLoad;
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

    static class LicenseItem {
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

    private final class LicensesAdapter extends CommonRecyclerAdapter<LicenseItem>{

        LicensesAdapter(Context context, int layoutResId, List<LicenseItem> data) {
            super(context, layoutResId, data);
        }

        @Override public void onUpdate(BaseAdapterHelper helper, LicenseItem item, int position) {
            helper.setImageLoad(new GlideImageLoad())
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
        //LICENSE_ARRAY.add(new LicenseItem("", "", LICENSE_TYPE_MIT,
        //                                  "",
        //                                  ""));
        //LICENSE_ARRAY.add(new LicenseItem("", "", LICENSE_TYPE_MIT,
        //                                  "",
        //                                  ""));
    }
}
