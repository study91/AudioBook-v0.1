package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.book.view.BookImageViewPager;
import com.study91.audiobook.book.view.OnSingleTapListener;
import com.study91.audiobook.dict.ReceiverAction;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaService;
import com.study91.audiobook.media.view.MediaPlayerView;
import com.study91.audiobook.system.SystemManager;

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

        bindMediaService(); //绑定媒体服务
        registerMediaBroadcastsReceiver(); //注册媒体广播接收器
    }

    @Override
    protected void onDestroy() {
        unregisterMediaBroadcastReceiver(); //注销媒体广播接收器
        unbindMediaService(); //取消媒体服务绑定
        super.onDestroy();
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
     * 绑定媒体服务
     */
    private void bindMediaService() {
        //创建媒体服务Intent
        Intent intent = new Intent(this, MediaService.class);

        //实例化媒体服务连接
        m.mediaServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MediaService.MediaServiceBinder binder = (MediaService.MediaServiceBinder) service;
                m.bookMediaPlayer = binder.getMediaPlayer();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        //绑定媒体服务
        bindService(intent, m.mediaServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 取消媒体服务绑定
     */
    private void unbindMediaService() {
        unbindService(m.mediaServiceConnection);
    }

    /**
     * 注册媒体广播接收器
     */
    private void registerMediaBroadcastsReceiver() {
        if (m.mediaBroadcastReceiver == null) {
            m.mediaBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //TODO 接收广播代码
                    ui.playButton.setVisibility(View.VISIBLE);
                    ui.playButton.setBackgroundResource(R.drawable.button_play); //设置播放图示

                    IBook book = SystemManager.getUser(getApplicationContext()).getCurrentBook();
                    IBookCatalog currentAudio = book.getCurrentAudio();
                    IBookPage currentPage = book.getCurrentPage();
                    boolean isPlaying = m.bookMediaPlayer.isPlaying();

                    if (currentAudio.getAudioFilename().equals(currentPage.getAudioFilename()) && isPlaying) {
                        ui.playButton.setBackgroundResource(R.drawable.button_pause);
                    }

                    //判断是否显示播放按钮
                    if (!currentPage.hasAudio()) {
                        ui.playButton.setVisibility(View.GONE);
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ReceiverAction.CLIENT.toString());
            registerReceiver(m.mediaBroadcastReceiver, intentFilter);
        }
    }

    /**
     * 注销媒体广播接收器
     */
    private void unregisterMediaBroadcastReceiver() {
        if (m.mediaBroadcastReceiver != null) {
            unregisterReceiver(m.mediaBroadcastReceiver);
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否有工具条
         */
        boolean hasToolbar = true;

        /**
         * 媒体服务连接
         */
        ServiceConnection mediaServiceConnection;

        /**
         * 媒体广播接收器
         */
        BroadcastReceiver mediaBroadcastReceiver;

        /**
         * 书媒体播放器
         */
        IBookMediaPlayer bookMediaPlayer;
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
