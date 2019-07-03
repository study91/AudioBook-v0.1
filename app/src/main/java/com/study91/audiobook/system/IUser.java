package com.study91.audiobook.system;

import com.study91.audiobook.book.IBook;
import com.study91.audiobook.dict.LoopMode;

/**
 * 用户接口
 */
public interface IUser {
    /**
     * 获取用户ID
     * @return 用户ID
     */
    int getUserID();

    /**
     * 获取用户名
     * @return 用户名
     */
    String getUserName();

    /**
     * 是否测试用户
     * @return true=测试用户，false=正式用户
     */
    boolean isTest();

    /**
     * 设置当前打开的书
     * @param bookID 书ID
     */
    void setCurrentBook(int bookID);

    /**
     * 获取当前打开的书
     * @return 当前打开的书
     */
    IBook getCurrentBook();

    /**
     * 设置语音音量
     * @param volume 语音音量
     */
    void setAudioVolume(float volume);

    /**
     * 获取语音音量
     * @return 语音音量
     */
    float getAudioVolume();

    /**
     * 设置音乐音量
     * @param volume 音乐音量
     */
    void setMusicVolume(float volume);

    /**
     * 获取音乐音量
     * @return 音乐音量
     */
    float getMusicVolume();

    /**
     * 获取语音循环模式
     * @return 语音循环模式
     */
    LoopMode getAudioLoopMode();

    /**
     * 获取音乐循环模式
     * @return 音乐循环模式
     */
    LoopMode getMusicLoopMode();

    /**
     * 更新
     */
    void update();
}
