package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.view.BookImageViewPager;

/**
 * 页窗口
 */
public class PageActivity extends Activity {
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏

        setContentView(R.layout.activity_page);

        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout);
        ui.fullLayout.removeAllViews();
        BookImageViewPager pageImageViewPager = new BookImageViewPager(this);
        ui.fullLayout.addView(pageImageViewPager);
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 全屏布局
         */
        RelativeLayout fullLayout;
    }
}
