package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 链接模式
 */
public enum LinkMode {
    /**
     * 无链接
     */
    NONE,

    /**
     * 主窗口显示
     */
    MAIN_WINDOW,

    /**
     * 显示原文和详解
     */
    ORIGINAL_AND_DETAIL,

    /**
     * 显示标题和详解
     */
    TITLE_AND_DETAIL,

    /**
     * 全屏显示
     */
    FULL;

    /**
     * 获取链接模式字符串
     * @return 链接模式字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
