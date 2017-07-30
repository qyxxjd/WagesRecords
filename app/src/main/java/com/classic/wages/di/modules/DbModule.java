/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.classic.wages.di.modules;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.classic.wages.db.DbHelper;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.utils.LogUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import cn.qy.util.activity.BuildConfig;
import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;

/**
 * 文件描述：数据库相关实例生成
 * 创 建 人：续写经典
 * 创建时间：16/6/5 下午2:07
 */
@Module public final class DbModule {

    private final Application mApplication;

    public DbModule(Application application) {
        this.mApplication = application;
    }

    @Provides @Singleton SQLiteOpenHelper provideOpenHelper() {
        return new DbHelper(mApplication);
    }

    @SuppressWarnings("CheckResult") @Provides @Singleton SqlBrite provideSqlBrite() {
        final SqlBrite.Builder builder = new SqlBrite.Builder();
        if (BuildConfig.DEBUG) {
            builder.logger(new SqlBrite.Logger() {
                @Override public void log(String message) {
                    LogUtil.d(message);
                }
            });
        }
        return builder.build();
    }

    @Provides @Singleton BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper helper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(BuildConfig.DEBUG);
        return db;
    }

    @Provides @Singleton WorkInfoDao provideWorkInfoDao(BriteDatabase briteDatabase) {
        return new WorkInfoDao(briteDatabase);
    }

    @Provides @Singleton MonthlyInfoDao provideMonthlyInfoDao(BriteDatabase briteDatabase) {
        return new MonthlyInfoDao(briteDatabase);
    }

    @Provides @Singleton QuantityInfoDao provideQuantityInfoDao(BriteDatabase briteDatabase) {
        return new QuantityInfoDao(briteDatabase);
    }
}
