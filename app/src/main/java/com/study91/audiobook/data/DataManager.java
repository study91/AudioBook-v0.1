package com.study91.audiobook.data;

/**
 * 数据管理器
 */
public class DataManager {
    /**
     * 创建数据
     * @param filename 数据库文件
     * @return 数据
     */
    public static IData createData(String filename) {
        return new SQLiteData(filename);
    }
}
