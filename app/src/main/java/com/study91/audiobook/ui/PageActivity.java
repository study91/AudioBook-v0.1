package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.view.BookImageViewPager;
import com.study91.audiobook.book.view.OnSingleTapListener;
import com.study91.audiobook.media.view.MediaPlayerView;

/**
 * 页活动
 */
public class PageActivity extends Activity implements View.OnClickListener {
    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏

        setContentView(R.layout.activity_page);

        //全屏布局
        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout);
        ui.fullLayout.removeAllViews();

        //顶部布局
        ui.topLayout = (RelativeLayout) findViewById(R.id.topLayout);

        //返回按钮
        ui.backButton = (Button) findViewById(R.id.backButton);
        ui.backButton.setOnClickListener(this);

        //播放按钮
        ui.playButton = (Button) findViewById(R.id.playButton);
        ui.playButton.setOnClickListener(this);

        //目录按钮
        ui.catalogButton = (Button) findViewById(R.id.catalogButton);
        ui.catalogButton.setOnClickListener(this);

        //媒体播放视图
        ui.mediaPlayerView = (MediaPlayerView) findViewById(R.id.mediaPlayerView);

        //书图片视图页
        ui.bookImageViewPager = new BookImageViewPager(this);
        ui.bookImageViewPager.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap() {
                setToolbar(!hasToolbar());
            }
        });

        ui.fullLayout.addView(ui.bookImageViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton: //返回按钮
                finish();
                break;
            case R.id.playButton: //播放按钮
                break;
            case R.id.catalogButton: //目录按钮
                Intent intent = new Intent(this, CatalogActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 是否有工具条
     * @return true=有工具条，false=没有工具条
     */
    private boolean hasToolbar() {
        return m.hasToolbar;
    }

    /**
     * 设置工具条
     * @param hasToolbar ture=显示工具条，false=隐藏工具条
     */
    private void setToolbar(boolean hasToolbar) {
        if (hasToolbar) {
            //显示工具条
            ui.mediaPlayerView.setVisibility(View.VISIBLE); //显示媒体播放工具条
            ui.topLayout.setVisibility(View.VISIBLE); //显示顶部工具条
        } else {
            //隐藏工具条
            ui.mediaPlayerView.setVisibility(View.GONE); //隐藏媒体播放工具条
            ui.topLayout.setVisibility(View.GONE); //隐藏顶部工具条
        }

        m.hasToolbar = hasToolbar; //缓存工具条状态
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否有工具条
         */
        boolean hasToolbar = true;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 全屏布局
         */
        RelativeLayout fullLayout;

        /**
         * 顶部布局
         */
        RelativeLayout topLayout;

        /**
         * 返回按钮
         */
        Button backButton;

        /**
         * 播放按钮
         */
        Button playButton;

        /**
         * 目录按钮
         */
        Button catalogButton;

        /**
         * 书图片视图页
         */
        BookImageViewPager bookImageViewPager;

        /**
         * 媒体播放器视图
         */
        MediaPlayerView mediaPlayerView;
    }
}
