package com.study91.ad;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kyview.InitConfiguration;
import com.kyview.interfaces.AdViewBannerListener;
import com.kyview.interfaces.AdViewInstlListener;
import com.kyview.manager.AdViewBannerManager;
import com.kyview.manager.AdViewInstlManager;
import com.kyview.manager.AdViewSpreadManager;

/**
 * AdView广告类
 */
class AdViewAD implements IAd {
    private final String TEST_TAG = "AdViewAD";
    //私有静态变量
    private static InitConfiguration initConfiguration; //广告请求配置，为全局作用，只需要调用一次即可
    private static String key; //广告识别键值
    private static String keySet[]; //广告识别键值数组

    //私有变量
    private Context context; //应用程序上下文

    /**
     * 构造函数
     * @param context 应用程序上下文
     * @param key 广告识别键值
     */
    public AdViewAD(Context context, String key) {
        this.context = context;

        if (AdViewAD.key == null) {
            AdViewAD.key = key;
            AdViewAD.keySet = new String[] {key};
        }

        if (AdViewAD.initConfiguration == null) {
            //请求广告之前请务必先进行初始化，否则将无法使用广告
            //设置广告请求配置参数，可使用默认配置InitConfiguration.createDefault(this);
            AdViewAD.initConfiguration = new InitConfiguration.Builder(context)
                    .setUpdateMode(InitConfiguration.UpdateMode.EVERYTIME) //实时获取配置，非必须写
                    .setBannerCloseble(InitConfiguration.BannerSwitcher.CANCLOSED) //横幅可关闭按钮
                    .setRunMode(InitConfiguration.RunMode.TEST) //测试模式时log更多，方便调试，不影响竞价展示正式广告和收益情况，可能影响个别第三方平台的正式广告展示，建议应用上线时删除该行
                    .build();

            //初始化横幅广告配置，必须写
            AdViewBannerManager.getInstance(context).init(
                    AdViewAD.initConfiguration,
                    AdViewAD.keySet);

            //初始化插屏广告配置，必须写
            AdViewInstlManager.getInstance(context).init(
                    AdViewAD.initConfiguration,
                    AdViewAD.keySet);

            //初始化开屏广告配置，必须写
            AdViewSpreadManager.getInstance(context).init(
                    AdViewAD.initConfiguration,
                    AdViewAD.keySet);
        }
    }

    @Override
    public View getBannerView() {
        RelativeLayout bannerView = new RelativeLayout(getContext()); //初始化横幅广告视图变量
        setViewLayoutParams(bannerView); //设置横幅广告视图布局参数

        //获取AdView横幅广告视图布局
        View view = AdViewBannerManager.getInstance(getContext()).getAdViewLayout(
                getContext(),
                AdViewAD.key);

        //如果AdView横幅广告视图布局已经存在，先移除
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }

        //获取横幅广告
        AdViewBannerManager.getInstance(
                getContext()).requestAd(getContext(),
                AdViewAD.key,
                new BannerViewListener(bannerView));

        view.setTag(AdViewAD.key); //设置AdView广告布局Tag
        setViewLayoutParams(view); //设置AdView广告布局参数

        bannerView.addView(view); //将AdView广告视图添加到横幅广告视图
        //bannerView.invalidate(); //使横幅广告视图无效

        return bannerView;
    }

    @Override
    public void requestInsertAD() {
        AdViewInstlManager.getInstance(getContext()).requestAd(
                getContext(),
                AdViewAD.key,
                new InsertADLisener());
    }

    @Override
    public void showInsertAD() {
        AdViewInstlManager.getInstance(getContext()).showAd(
                getContext(),
                AdViewAD.key);
    }

    /**
     * 设置视图布局参数
     * @param view 视图
     */
    private void setViewLayoutParams(View view) {
        //设置为按内容大小显示
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT); //设置为相对父视图居中显示
        view.setLayoutParams(layoutParams); //应用布局参数
    }

    private Context getContext() {
        return context;
    }

    /**
     * 横幅广告事件监听器
     */
    private class BannerViewListener implements AdViewBannerListener {
        private RelativeLayout layout;

        public BannerViewListener(RelativeLayout layout) {
            this.layout = layout;
        }

        @Override
        public void onAdClick(String s) {
            Log.d(TEST_TAG, "onAdClick：横幅广告 “" + s + "” 被点击。");
        }

        @Override
        public void onAdDisplay(String s) {
            Log.d(TEST_TAG, "onAdDisplay：横幅广告 “" + s + "” 被显示。");
        }

        @Override
        public void onAdClose(String s) {
            Log.d(TEST_TAG, "onAdClose：横幅广告 “" + s + "” 被关闭。");
            if (this.layout != null) {
                layout.removeView(layout.findViewWithTag(s));
            }
        }

        @Override
        public void onAdFailed(String s) {
            Log.d(TEST_TAG, "onAdFailed：横幅广告 “" + s + "” 被异常中断。");
        }

        @Override
        public void onAdReady(String s) {
            Log.d(TEST_TAG, "onAdReady：横幅广告实验性功能。");
        }
    }

    /**
     * 插屏广告事件监听器
     */
    private class InsertADLisener implements AdViewInstlListener {
        @Override
        public void onAdClick(String s) {
            Log.d(TEST_TAG, "onAdClick：插屏广告 “" + s + "” 被点击。");
        }

        @Override
        public void onAdDisplay(String s) {
            Log.d(TEST_TAG, "onAdDisplay：插屏广告 “" + s + "” 被展示。");
        }

        @Override
        public void onAdDismiss(String s) {
            Log.d(TEST_TAG, "onAdDismiss：插屏广告 “" + s + "” 已消失。");
        }

        @Override
        public void onAdRecieved(String s) {
            Log.d(TEST_TAG, "onAdRecieved：插屏广告 “" + s + "” 请求成功。");
        }

        @Override
        public void onAdFailed(String s) {
            Log.d(TEST_TAG, "onAdFailed：插屏广告 “" + s + "” 请求失败。");
        }
    }

}
