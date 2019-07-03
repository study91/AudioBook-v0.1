package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 媒体广播接收器Action
 */
public enum ReceiverAction {
    /**
     * 服务端
     */
    SERVICE ,

    /**
     * 客户端
     */
    CLIENT;

    /**
     * 获取Action
     * @param context 应用程序上下文
     */
    public String getAction(Context context) {
        return context.getPackageName() + "." + name() + ".BroadcastReceiverAction";
    }
}
