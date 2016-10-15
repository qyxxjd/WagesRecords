package com.classic.wages.di.modules;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 *
 * 文件描述：实例生成
 * 创 建 人：续写经典
 * 创建时间：16/6/5 下午2:07
 */
@Module public class AppModule {

    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides @Singleton Application provideApplication() {
        return mApplication;
    }

}
