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
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaClient;
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
        getMediaClient().register();

        ui.rjYuWen1aButton = (Button) findViewById(R.id.rjYuWen1aButton); //显示页按钮
        ui.rjYuWen1aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IUser user = SystemManager.getUser(getApplicationContext());
                user.setCurrentBook(1);

                IBook book = user.getCurrentBook(); //获取当前打开的书
                IBookCatalog currentAudio = book.getCurrentAudio(); //获取当前语音目录

                //设置语音文件
                IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer();
                mediaPlayer.setAudioFile(
                        currentAudio.getAudioFilename(),
                        currentAudio.getTitle(),
                        currentAudio.getIconFilename());
                mediaPlayer.setSoundType(book.getSoundType());
                mediaPlayer.play();
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
                IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer();
                mediaPlayer.setAudioFile(
                        currentAudio.getAudioFilename(),
                        currentAudio.getTitle(),
                        currentAudio.getIconFilename());
                mediaPlayer.setSoundType(book.getSoundType());
                mediaPlayer.play();
            }
        });

        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout); //全屏布局

        m.mediaClient = new MediaClient(this);
        m.mediaClient.register();
    }

    @Override
    protected void onDestroy() {
        getMediaClient().unregister();
        stopService(m.mediaServiceIntent);
        super.onDestroy();
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
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(this);
        }

        return m.mediaClient;
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
         * 媒体客户端
         */
        MediaClient mediaClient;
    }

    /**
     * 私有界面类
     */
    private class UI {
        RelativeLayout fullLayout;
        Button rjYuWen1aButton;
        Button rjYuWen1bButton;
    }
}
