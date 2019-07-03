package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 存储类型
 */
public enum StorageType {
    /**
     * Assets资源
     */
    ASSETS,

    /**
     * 文件目录
     */
    FilesDir,

    /**
     * SD卡资源
     */
    SDCARD;

    /**
     * 获取存储类型字符串
     * @return 存储类型字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
