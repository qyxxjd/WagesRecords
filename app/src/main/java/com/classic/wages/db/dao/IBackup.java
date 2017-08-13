package com.classic.wages.db.dao;

import java.io.File;

public interface IBackup {
    /**
     * 备份数据
     */
    boolean backup(File file) throws Exception;

    /**
     * 恢复数据
     */
    boolean restore(File file) throws Exception;
}
