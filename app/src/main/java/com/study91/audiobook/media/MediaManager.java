package com.study91.audiobook.media;

import android.content.Context;
import com.study91.audiobook.dict.StorageType;

/**
 * 媒体播放器管理器
 */
class MediaManager {
    /**
     * 创建媒体播放器
     * @param context 应用程序上下文
     * @return 媒体播放器
     */
    static IMediaPlayer createMediaPlayer(Context context) {
        return new AssetMediaPlayer(context);
    }

    /**
     * 创建媒体播放器
     * @param context 应用程序上下文
     * @param storageType 存储类型
     * @return 媒体播放器
     */
    static IMediaPlayer createMediaPlayer(Context context, StorageType storageType) {
        IMediaPlayer mediaPlayer = null;

        //创建媒体播放器
        switch (storageType) {
            case ASSETS: //Assets媒体资源
                mediaPlayer = new AssetMediaPlayer(context);
                break;
        }

        return mediaPlayer;
    }
}
