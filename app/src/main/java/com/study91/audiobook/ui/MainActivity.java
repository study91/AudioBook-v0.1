package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.book.view.BookImageViewPager;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaService;
import com.study91.audiobook.system.IConfig;
import com.study91.audiobook.system.IPermission;
import com.study91.audiobook.system.IUser;
import com.study91.audiobook.system.SystemManager;

/**
 * 主窗口
 */
public class MainActivity extends Activity {
    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏

        setContentView(R.layout.activity_main);

        IPermission permission = SystemManager.createPermission(); //创建权限
        permission.request(this); //请求权限

        //初始化系统配置
        IConfig config = SystemManager.getConfig(this);
        config.init();

        //启动媒体服务
        startService(getMediaServiceIntent());
        bindService(getMediaServiceIntent(), getMediaServiceConnection(), BIND_AUTO_CREATE);

        ui.rjYuWen1aButton = (Button) findViewById(R.id.rjYuWen1aButton); //显示页按钮
        ui.rjYuWen1aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IUser user = SystemManager.getUser(getApplicationContext());
                user.setCurrentBook(1);

                IBook book = user.getCurrentBook(); //获取当前打开的书
                IBookCatalog currentAudio = book.getCurrentAudio(); //获取当前语音目录

                //设置语音文件
                getMediaPlayer().setAudioFile(
                        currentAudio.getAudioFilename(),
                        currentAudio.getTitle(),
                        currentAudio.getIconFilename());
                getMediaPlayer().setSoundType(book.getSoundType());
                getMediaPlayer().play();

//                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
//                startActivity(intent);
            }
        });

        ui.rjYuWen1bButton = (Button) findViewById(R.id.rjYuWen1bButton);
        ui.rjYuWen1bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IUser user = SystemManager.getUser(getApplicationContext());
                user.setCurrentBook(2);

                IBook book = user.getCurrentBook(); //获取当前打开的书
                IBookCatalog currentAudio = book.getCurrentAudio(); //获取当前语音目录

                //设置语音文件
                getMediaPlayer().setAudioFile(
                        currentAudio.getAudioFilename(),
                        currentAudio.getTitle(),
                        currentAudio.getIconFilename());
                getMediaPlayer().setSoundType(book.getSoundType());
                getMediaPlayer().play();

//                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
//                startActivity(intent);
            }
        });

//        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout); //全屏布局
//        ui.fullLayout.removeAllViews(); //将全局布局移除所有视图
//        ui.bookPageViewPager = new PageImageViewPager(getApplicationContext());
//        ui.bookPageViewPager.setOnSingleTapListener(new OnSingleTapListener() {
//            @Override
//            public void onSingleTap() {
//                Log.d("MainActivity", "ui.bookPageViewPager.setOnSingleTapListener");
//            }
//        });
//
//        ui.fullLayout.addView(ui.bookPageViewPager);
//
//        IBook1 book = BookManager.getCurrentBook(this);
    }

    @Override
    protected void onDestroy() {
        unbindService(m.mediaServiceConnection);
        stopService(m.mediaServiceIntent);
        super.onDestroy();
    }

    /**
     * 获取媒体服务Connection
     * @return 媒体服务Connection
     */
    private ServiceConnection getMediaServiceConnection() {
        if (m.mediaServiceConnection == null) {
            m.mediaServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MediaService.MediaServiceBinder binder = (MediaService.MediaServiceBinder) service;
                    m.mediaPlayer = binder.getMediaPlayer();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
        }

        return m.mediaServiceConnection;
    }

    /**
     * 获取媒体服务Intent
     * @return 媒体服务Intent
     */
    private Intent getMediaServiceIntent() {
        if (m.mediaServiceIntent == null) {
            m.mediaServiceIntent = new Intent(this, MediaService.class);
        }

        return m.mediaServiceIntent;
    }

    /**
     * 获取媒体播放器
     * @return 媒体播放器
     */
    private IBookMediaPlayer getMediaPlayer() {
        return m.mediaPlayer;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体服务Intent
         */
        Intent mediaServiceIntent;

        /**
         * 媒体服务连接
         */
        ServiceConnection mediaServiceConnection;

        /**
         * 媒体播放器
         */
        IBookMediaPlayer mediaPlayer;
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
         * 有声书页视图页
         */
        BookImageViewPager bookPageViewPager;

        Button rjYuWen1aButton;
        Button rjYuWen1bButton;

    }
}
