package com.classic.wages.db.dao;

import java.io.File;

/**
 * Created by classic on 2017/1/12.
 */

public interface IBackup {

    interface Listener {
        /**
         * 完成
         */
        void onComplete();

        /**
         * 出错
         * @param throwable
         */
        void onError(Throwable throwable);

        /**
         * 当前进度
         * @param currentCount 已完成数据条数
         * @param totalCount 总数据条数
         */
        void onProgress(long currentCount, long totalCount);
    }

    /**
     * 备份数据
     */
    void backup(File file, Listener listener);

    /**
     * 恢复数据
     */
    void restore(File file, Listener listener);
}
