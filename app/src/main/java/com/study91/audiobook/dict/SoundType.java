package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 声音类型
 */
public enum SoundType {
    /**
     * 没有声音
     */
    NO_SOUND,

    /**
     * 有语音和背景音乐
     */
    AUDIO_AND_MUSIC,

    /**
     * 只有语音
     */
    ONLY_AUDIO,

    /**
     * 语音左声道，音乐右声道
     */
    AUDIO_LEFT,

    /**
     * 语音右声道，音乐左声道
     */
    AUDIO_RIGHT;

    /**
     * 获取声音类型字符串
     * @return 声音类型字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
