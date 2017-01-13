package com.classic.wages.db.dao;

import java.io.File;

/**
 * Created by classic on 2017/1/12.
 */

public interface IBackup {

    /**
     * 备份数据
     */
    void backup(File file);

    /**
     * 恢复数据
     */
    void restore(File file);
}
