package com.study91.audiobook.dict;

/**
 * 媒体状态
 */
public enum MediaState {
    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 等待播放状态
     */
    WAITING_TO_PLAY,

    /**
     * 准备中状态
     */
    PREPARING,

    /**
     * 准备就绪状态
     */
    PREPARED,

    /**
     * 暂停状态
     */
    PAUSED,

    /**
     * 来电暂停状态
     */
    CALL_PAUSED,

    /**
     * 停止状态
     */
    STOPPED,

    /**
     * 播放完成状态
     */
    COMPLETED,

    /**
     * 结束状态
     */
    END
}
