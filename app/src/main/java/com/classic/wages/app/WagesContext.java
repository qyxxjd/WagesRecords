package com.classic.wages.app;

import android.content.Context;
import cn.qy.util.activity.BuildConfig;
import com.classic.core.utils.AppInfoUtil;
import com.github.moduth.blockcanary.BlockCanaryContext;
import java.util.ArrayList;
import java.util.List;

final class WagesContext extends BlockCanaryContext {
    private Context mAppContext;

    WagesContext(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Override public String provideQualifier() {
        return new StringBuilder().append("wages_")
                                  .append(AppInfoUtil.getVersionCode(mAppContext))
                                  .append("_")
                                  .append(AppInfoUtil.getVersionName(mAppContext))
                                  .toString();
    }

    @Override public String provideUid() {
        return "10565";
    }

    @Override public String provideNetworkType() {
        return "4G";
    }

    @Override public int provideMonitorDuration() {
        return 9999;
    }

    @Override public int provideBlockThreshold() {
        return 500;
    }

    @Override public boolean displayNotification() {
        return BuildConfig.DEBUG;
    }

    @Override public List<String> concernPackages() {
        ArrayList<String> packages = new ArrayList<>();
        packages.add(AppInfoUtil.getPackageName(mAppContext));
        return packages; //关注的包名
    }

    @Override public List<String> provideWhiteList() {
        return super.provideWhiteList(); //白名单列表
    }
}