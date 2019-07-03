package com.study91.audiobook.book;

import android.graphics.drawable.Drawable;

/**
 * 页接口
 */
public interface IBookPage {
    /**
     * 获取书ID
     * @return 书ID
     */
    int getBookID();

    /**
     * 获取书
     * @return 书
     */
    IBook getBook();

    /**
     * 获取页码
     * @return 页码
     */
    int getPageNumber();

    /**
     * 获取图片文件名
     * @return 图片文件名
     */
    String getImageFilename();

    /**
     * 获取图片Drawable
     * @return 图片Drawable
     */
    Drawable getImageDrawable();

    /**
     * 获取图标文件名
     * @return 图标文件名
     */
    String getIconFilename();

    /**
     * 获取图标Drawable
     * @return 图标Drawable
     */
    Drawable getIconDrawable();

    /**
     * 是否有语音
     * @return true=有语音 false=没有语音
     */
    boolean hasAudio();

    /**
     * 获取语音文件名
     * @return 语音文件名
     */
    String getAudioFilename();

    /**
     * 获取语音开始时间
     * @return 语音开始时间
     */
    long getAudioStartTime();
}
