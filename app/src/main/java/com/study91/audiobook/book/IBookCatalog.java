package com.study91.audiobook.book;

import android.graphics.drawable.Drawable;

import com.study91.audiobook.dict.DisplayMode;
import com.study91.audiobook.dict.FamiliarLevel;

/**
 * 目录接口
 */
public interface IBookCatalog {
    /**
     * 获取目录ID
     * @return 目录ID
     */
    int getCatalogID();

    /**
     * 获取书ID
     * @return 书ID
     */
    int getBookID();

    /**
     * 获取目录索引
     * @return 目录索引
     */
    int getIndex();

    /**
     * 获取目录位置
     * @return 目录位置
     */
    int getPosition();

    /**
     * 获取目录页码
     * @return 目录页码
     */
    int getPageNumber();

    /**
     * 获取目录页
     * @return 目录页
     */
    IBookPage getPage();

    /**
     * 获取目录标题
     * @return 目录标题
     */
    String getTitle();

    /**
     * 是否有解释
     * @return true=有解释 false=没有解释
     */
    boolean hasExplain();

    /**
     * 是否有语音
     * @return true=有语音 false=没有语音
     */
    boolean hasAudio();

    /**
     * 设置语音播放开关
     * @param value true=充许播放，false=禁止播放
     */
    void setAudioPlayEnable(boolean value);

    /**
     * 是否充许播放语音
     * @return true=充许播放语音 false=不充许播放语音
     */
    boolean allowPlayAudio();

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

    /**
     * 获取语音结束时间
     * @return 语音结束时间
     */
    long getAudioEndTime();

    /**
     * 获取图标显示模式
     * @return 图标显示模式
     */
    DisplayMode getIconDisplayMode();

    /**
     * 获取图标文件名
     * @return 图标文件名
     */
    String getIconFilename();

    /**
     * 获取图标Drawwable
     * @return 图标Drawwable
     */
    Drawable getIconDrawable();

    /**
     * 获取熟悉级别
     * @return 熟悉级别
     */
    FamiliarLevel getFamiliarLevel();
}
