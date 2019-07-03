package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 内容类型
 */
public enum ContentType {
   /**
     * 无内容
     */
    NO_CONTENT,

    /**
     * 有内容
     */
    HAS_CONTENT;

    /**
     * 获取内容类型字符串
     * @return 内容类型字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }


}
