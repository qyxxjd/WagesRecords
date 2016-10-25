package com.classic.wages.di.modules;

import android.app.Application;
import com.classic.core.utils.SharedPreferencesUtil;
import com.classic.wages.consts.Consts;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 *
 * 文件描述：实例生成
 * 创 建 人：续写经典
 * 创建时间：16/6/5 下午2:07
 */
@Module(includes = { DbModule.class })
public class AppModule {

    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides @Singleton Application provideApplication() {
        return mApplication;
    }

    @Provides @Singleton SharedPreferencesUtil provideSharedPreferencesUtil() {
        return new SharedPreferencesUtil(mApplication, Consts.SP_NAME);
    }


}
