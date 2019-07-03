package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 循环模式
 */
public enum LoopMode {
    /**
     * 不循环
     */
    NO,

    /**
     * 列表循环
     */
    LIST,

    /**
     * 随机循环
     */
    RANDOM;

    /**
     * 获取循环模式字符串
     * @return 循环模式字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
