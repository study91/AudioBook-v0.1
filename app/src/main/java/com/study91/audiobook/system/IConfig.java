package com.study91.audiobook.system;

/**
 * 配置接口
 */
public interface IConfig {
    /**
     * 初始化
     */
    void init();

    /**
     * 获取有声书数据源
     * @return 有声书数据源
     */
    String getBookDataSource();

    /**
     * 获取用户数据源
     * @return 用户数据源
     */
    String getUserDataSource();

    /**
     * 获取用户ID
     * @return 用户ID
     */
    int getUserID();
}
