package com.study91.ad;

import android.content.Context;

/**
 * 广告管理器
 */
public class ADManager {
    /**
     * 创建广告
     * @param context 应用程序上下文
     * @return 广告
     */
    public static IAd createAD(Context context) {
        String adViewKey = "SDK20161629040641z7snyxkrbndasty";
        IAd ad = new AdViewAD(context, adViewKey);
        return ad;
    }
}
