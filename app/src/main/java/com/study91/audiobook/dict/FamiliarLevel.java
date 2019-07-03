package com.study91.audiobook.dict;

import android.content.Context;

/**
 * 熟悉级别
 */
public enum FamiliarLevel {
    /**
     * 未学习
     */
    NOT_STUDY,

    /**
     * 不熟悉
     */
    UNFAMILIAR,

    /**
     * 熟悉
     */
    FAMILIAR,

    /**
     * 比较熟悉
     */
    MORE_FAMILIAR,

    /**
     * 非常熟悉
     */
    VERY_FAMILIAR;

    /**
     * 获取熟悉级别字符串
     * @return 熟悉级别字符串
     */
    public String getString(Context context) {
        IDict dict = DictManager.createDict(context);
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
