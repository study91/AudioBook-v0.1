package com.study91.audiobook.dict;

/**
 * 字典接口
 */
public interface IDict {
    /**
     * 获取字典值
     * @param type 字典类别
     * @param id 字典ID
     * @return 字典值
     */
    String getValue(String type, int id);
}
