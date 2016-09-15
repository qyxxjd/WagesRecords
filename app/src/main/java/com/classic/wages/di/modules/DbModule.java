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
import android.text.TextUtils;
import cn.qy.util.activity.BuildConfig;
import com.classic.core.log.Logger;
import com.classic.wages.db.DbHelper;
import com.classic.wages.db.dao.WorkInfoDao;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import rx.schedulers.Schedulers;

/**
 *
 * 文件描述：数据库相关实例生成
 * 创 建 人：续写经典
 * 创建时间：16/6/5 下午2:07
 */
@Module public final class DbModule {

    @Provides @Singleton SQLiteOpenHelper provideOpenHelper(Application application) {
        return new DbHelper(application);
    }

    @Provides @Singleton SqlBrite provideSqlBrite() {
        return SqlBrite.create(new SqlBrite.Logger() {
            @Override public void log(String message) {
                if(!TextUtils.isEmpty(message)){
                    Logger.d(message);
                }
            }
        });
    }

    @Provides @Singleton BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper helper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(BuildConfig.DEBUG);
        return db;
    }

    @Provides @Singleton WorkInfoDao provideWorkInfoDao(BriteDatabase briteDatabase) {
        return new WorkInfoDao(briteDatabase);
    }
}
