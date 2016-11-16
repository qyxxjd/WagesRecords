package com.classic.wages.app;

import android.app.Application;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import com.classic.wages.di.components.AppComponent;
import com.classic.wages.di.components.DaggerAppComponent;
import com.classic.wages.di.modules.AppModule;
import com.classic.wages.di.modules.DbModule;
import com.squareup.leakcanary.LeakCanary;

public class WagesApplication extends Application {
    private static SharedPreferencesUtil sPreferencesUtil;

    private AppComponent mAppComponent;

    @Override public void onCreate() {
        super.onCreate();
        sPreferencesUtil = new SharedPreferencesUtil(this, Consts.SP_NAME);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        mAppComponent = DaggerAppComponent.builder()
                                          .appModule(new AppModule(this))
                                          .dbModule(new DbModule())
                                          .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static SharedPreferencesUtil getPreferencesUtil(){
        return sPreferencesUtil;
    }
}
