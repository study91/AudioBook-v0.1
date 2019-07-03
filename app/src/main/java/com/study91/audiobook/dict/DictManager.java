package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 字典管理器
 */
public class DictManager {
    /**
     * 创建字典
     * @param context 应用程序上下文
     * @return 字典
     */
    public static IDict createDict(Context context) {
        return new Dict(context);
    }
}
