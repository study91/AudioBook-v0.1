package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 全屏模式
 */
public enum FullMode {
    /**
     * 关闭全屏
     */
    CLOSE,

    /**
     * 横屏标题
     */
    LANDSCAPE_TITLE,

    /**
     * 横屏字幕
     */
    LANDSCAPE_CAPTION,

    /**
     * 横屏图片
     */
    LANDSCAPE_IMAGE,

    /**
     * 竖屏标题
     */
    PORTRAIT_TITLE,

    /**
     * 竖屏字幕
     */
    PORTRAIT_CAPTION,

    /**
     * 竖屏图片
     */
    PORTRAIT_IMAGE;

    /**
     * 获取全屏模式字符串
     * @return 全屏模式字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
