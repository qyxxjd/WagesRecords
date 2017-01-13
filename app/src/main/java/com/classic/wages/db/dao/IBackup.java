package com.classic.wages.db.dao;

import java.io.File;

/**
 * Created by classic on 2017/1/12.
 */

public interface IBackup {

    /**
     * 备份数据
     */
    boolean backup(File file);

    /**
     * 恢复数据
     */
    boolean restore(File file);
}
