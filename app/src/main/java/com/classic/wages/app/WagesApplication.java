package com.classic.wages.app;

import android.app.Application;
import com.classic.wages.di.components.AppComponent;
import com.classic.wages.di.components.DaggerAppComponent;
import com.classic.wages.di.modules.AppModule;
import com.classic.wages.di.modules.DbModule;
import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;

public class WagesApplication extends Application {
    private AppComponent mAppComponent;

    @Override public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        BlockCanary.install(this, new WagesContext(this)).start();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                                          .dbModule(new DbModule()).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
