package com.classic.wages.di.components;

import com.classic.wages.di.modules.DbModule;
import com.classic.wages.ui.fragment.AddFragment;
import com.classic.wages.ui.fragment.AddMonthlyFragment;
import com.classic.wages.ui.fragment.AddQuantityFragment;
import com.classic.wages.ui.fragment.ListFragment;
import com.classic.wages.ui.fragment.MainFragment;
import com.classic.wages.ui.fragment.SettingFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 *
 * 文件描述：实例注入
 * 创 建 人：续写经典
 * 创建时间：16/6/5 下午2:07
 */
@Singleton @Component(modules = { DbModule.class })
public interface AppComponent {
    void inject(MainFragment fragment);
    void inject(ListFragment fragment);
    void inject(SettingFragment fragment);
    void inject(AddFragment fragment);
    void inject(AddMonthlyFragment fragment);
    void inject(AddQuantityFragment fragment);
}
