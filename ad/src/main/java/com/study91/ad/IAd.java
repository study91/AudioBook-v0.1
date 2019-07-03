package com.study91.ad;

import android.view.View;

/**
 * 广告接口
 */
public interface IAd {
    /**
     * 获取横幅广告视图
     * @return 横幅广告视图
     */
    View getBannerView();

    /**
     * 请求插屏广告
     */
    void requestInsertAD();

    /**
     * 显示插屏广告
     */
    void showInsertAD();
}
