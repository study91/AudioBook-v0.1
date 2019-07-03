package com.study91.audiobook.media;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import com.study91.audiobook.dict.MediaState;

/**
 * 媒体播放器接口
 */
interface IMediaPlayer {
    /**
     * 设置媒体文件名
     * @param filename 媒体文件名
     */
    void setFilename(String filename);

    /**
     * 获取媒体文件名
     * @return 媒体文件名
     */
    String getFilename();

    /**
     * 设置媒体标题
     * @param title 媒体标题
     */
    void setTitle(String title);

    /**
     * 获取媒体标题
     * @return 媒体标题
     */
    String getTitle();

    /**
     * 播放
     */
    void play();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    boolean isPlaying();

    /**
     * 定位播放位置
     * @param position 播放位置（以毫秒为单位）
     */
    void seekTo(int position);

    /**
     * 获取媒体长度
     * @return 媒体长度（以毫秒为单位）
     */
    int getLength();

    /**
     * 获取媒体当前播放位置
     * @return 媒体当前播放位置（以毫秒为单位）
     */
    int getPosition();

    /**
     * 设置音量
     * @param leftVolume 左声道音量
     * @param rightVolume 右声道音量
     */
    void setVolume(float leftVolume, float rightVolume);

    /**
     * 设置图标文件名
     * @param filename 图标文件名
     */
    void setIconFilename(String filename);

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
     * 获取媒体状态
     * @return 媒体状态
     */
    MediaState getMediaState();

    /**
     * 设置媒体准备就绪事件监听器
     * @param listener 媒体准备就绪事件监听器
     */
    void setOnPreparedListener(MediaPlayer.OnPreparedListener listener);

   /**
     * 设置媒体播放完成事件监听器
     * @param listener 媒体播放完成事件监听器
     */
    void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);

    /**
     * 释放媒体播放器
     */
    void release();
}
