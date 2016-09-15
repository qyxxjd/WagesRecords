package com.classic.wages.app;

import android.app.Application;

public class WagesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //if(BuildConfig.DEBUG){
        //    BasicConfig.getInstance(this).initDir().initLog(true);
        //} else {
        //    BasicConfig.getInstance(this).init();
        //}
    }

}
