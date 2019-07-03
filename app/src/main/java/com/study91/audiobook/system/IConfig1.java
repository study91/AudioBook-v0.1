package com.study91.audiobook.system;

/**
 * 配置接口
 */
public interface IConfig1 {
    /**
     * 初始化
     */
    void init();

    /**
     * 获取配置ID
     * @return 配置ID
     */
    int getOptionID();

    /**
     * 获取选项名称
     * @return 选项名称
     */
    String getOptionName();

    /**
     * 获取有声书数据源
     * @return 有声书数据源
     */
    String getBookDataSource();

    /**
     * 获取有声书文件路径
     * @return 有声书文件路径
     */
    String getBookFilePath();

    /**
     * 是否测试状态
     * @return trur=测试状态，false=发布状态
     */
    boolean isTest();

    /**
     * 获取有声书ID（注：当前正在阅读的有声书ID）
     * @return 有声书ID
     */
    int getBookID();

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
     * 语音循环模式
     * @return 循环模式
     */
    LoopMode audioLoopMode();

    /**
     * 背景音乐循环模式
     * @return 循环模式
     */
    LoopMode musicLoopMode();

    /**
     * 更新语音音量
     */
    void update();

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
        RANDOM
    }
}
