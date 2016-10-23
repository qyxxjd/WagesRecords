package com.classic.wages.di.components;

import com.classic.wages.di.modules.AppModule;
import com.classic.wages.ui.fragment.AddFragment;
import com.classic.wages.ui.fragment.ListFragment;
import com.classic.wages.ui.fragment.MainFragment;
import dagger.Component;
import javax.inject.Singleton;

/**
 *
 * 文件描述：实例注入
 * 创 建 人：续写经典
 * 创建时间：16/6/5 下午2:07
 */
@Singleton @Component(modules = { AppModule.class })
public interface AppComponent {
    void inject(MainFragment fragment);
    void inject(ListFragment fragment);
    void inject(AddFragment fragment);
}
