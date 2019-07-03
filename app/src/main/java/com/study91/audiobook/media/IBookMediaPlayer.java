package com.study91.audiobook.media;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import com.study91.audiobook.dict.SoundType;

/**
 * 书媒体播放器接口
 */
public interface IBookMediaPlayer {
    /**
     * 设置语音文件
     * @param filename 语音文件名
     * @param title 语音标题
     * @param iconFilename 图标文件名
     */
    void setAudioFile(String filename, String title, String iconFilename);

    /**
     * 获取语音文件名
     * @return 语音文件名
     */
    String getAudioFilename();

    /**
     * 获取语音标题
     * 注：语音标题也就是书标题
     * @return 语音标题
     */
    String getAudioTitle();

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
     * 设置音乐文件
     * 注：背景音乐只有 SoundType=AUDIO_AND_MUSIC 时（声音类型为有语音和背景音乐）才有效
     * @param filename 背景音乐文件名
     * @param title 背景音乐标题
     */
    void setMusicFile(String filename, String title);

    /**
     * 获取背景音乐文件名
     * @return 背景音乐文件名
     */
    String getMusicFilename();

    /**
     * 获取背景音乐标题
     * @return 背景音乐标题
     */
    String getMusicTitle();

    /**
     * 设置声音类型
     * @param soundType 声音类型
     */
    void setSoundType(SoundType soundType);

    /**
     * 获取声音类型
     * @return 声音类型
     */
    SoundType getSoundType();

    /**
     * 设置语音音量
     * @param volume 语音音量
     */
    void setAudioVolume(float volume);

    /**
     * 获取语音音量
     * @return 音量
     */
    float getAudioVolume();

    /**
     * 设置背景音乐音量
     * @param volume 背景音乐音量
     */
    void setMusicVolume(float volume);

    /**
     * 获取背景音乐音量
     * @return 背景音乐音量
     */
    float getMusicVolume();

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
     * 返回值含义如下：
     * true=正在播放，false=没有播放
     */
    boolean isPlaying();

    /**
     * 设置是否循环播放
     * @param isLooping 循环值（true=循环播放，false=不循环播放）
     */
    void setIsLooping(boolean isLooping);

    /**
     * 定位播放位置
     * @param position 播放位置
     */
    void seekTo(int position);

    /**
     * 获取播放位置
     * @return 播放位置
     */
    int getPosition();

    /**
     * 获取媒体长度
     * @return 媒体长度
     */
    int getLength();

    /**
     * 释放书媒体播放器
     */
    void release();

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
}
