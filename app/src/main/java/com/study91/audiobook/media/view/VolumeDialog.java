package com.study91.audiobook.media.view;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.dict.SoundType;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaService;
import com.study91.audiobook.system.IConfig1;
import com.study91.audiobook.system.SystemManager;

/**
 * 音量对话框类
 */
class VolumeDialog extends Dialog {
    private final String TAG = "VolumeDialog";
    private Field m = new Field(); //私有变量
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    VolumeDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.volume_dialog); //加载对话框布局
        bindMediaService(); //绑定媒体服务
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); //获取窗口属性

        DisplayMetrics displayMetrics = new DisplayMetrics(); //获取显示
        WindowManager windowManager = getWindow().getWindowManager(); //获取窗口管理器
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int rotation = windowManager.getDefaultDisplay().getRotation(); //获取屏幕方向
        switch(rotation) {
            case Surface.ROTATION_0: //竖屏
                layoutParams.width = (int)(displayMetrics.widthPixels * 0.8);
                break;
            case Surface.ROTATION_90: //横屏
                layoutParams.width = (int)(displayMetrics.heightPixels * 0.8);
                break;
            case Surface.ROTATION_180: //竖屏
                layoutParams.width = (int)(displayMetrics.widthPixels * 0.8);
                break;
            case Surface.ROTATION_270: //横屏
                layoutParams.width = (int)(displayMetrics.heightPixels * 0.8);
                break;
        }

        getWindow().setAttributes(layoutParams);

        //语音音量拖动条
        ui.audioVolumeTextView = (TextView) findViewById(R.id.audioVolumeTextView);
        ui.audioVolumeSeekBar = (SeekBar) findViewById(R.id.audioVolumeSeekBar);
        ui.audioVolumeSeekBar.setOnSeekBarChangeListener(new OnAudioVolumeSeekBarChangeListener());

        int audioVolume = (int)(getConfig().getAudioVolume() * 100); //获取语音音量
        ui.audioVolumeSeekBar.setProgress(audioVolume); //设置语音进度条
        ui.audioVolumeTextView.setText(audioVolume + ""); //设置语音进度文本


        //背景音乐音量拖动条
        ui.musicVolumeTextView = (TextView) findViewById(R.id.musicVolumeTextView);
        ui.musicVolumeSeekBar = (SeekBar) findViewById(R.id.musicVolumeSeekBar);
        ui.musicVolumeSeekBar.setOnSeekBarChangeListener(new OnMusicVolumeSeekBarChangeListener());

        int musicVolume = (int)(getConfig().getMusicVolume() * 100); //获取背景音乐音量
        ui.musicVolumeSeekBar.setProgress(musicVolume); //设置背景音乐进度条
        ui.musicVolumeTextView.setText(musicVolume + ""); //设置背景音乐进度文本

        ui.musicLayout = (RelativeLayout) findViewById(R.id.musicLayout); //背景音乐布局

        //只有语音时，不显示背景音乐音量拖动条
        if (getMediaPlayer().getSoundType() == SoundType.ONLY_AUDIO) {
            ui.musicLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        if(hasChanged()) getConfig().update(); //更新配置
        unbindMediaService(); //取消媒体服务绑定
        super.onStop();
    }

    /**
     * 获取媒体播放器
     * @return 媒体播放器
     */
    private IBookMediaPlayer getMediaPlayer() {
        return m.mediaPlayer;
    }

    /**
     * 绑定媒体服务
     */
    private void bindMediaService() {
        //创建媒体服务Intent
        Intent intent = new Intent(getContext(), MediaService.class);

        //实例化媒体服务连接
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

        //绑定媒体服务
        getContext().bindService(intent, m.mediaServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 取消媒体服务绑定
     */
    private void unbindMediaService() {
        getContext().unbindService(m.mediaServiceConnection);
    }

    /**
     * 获取全局配置
     * @return 全局配置
     */
    private IConfig1 getConfig() {
        return SystemManager.getSystemConfig(getContext());
    }

    /**
     * 是否有改变
     * @return true=有改变，false=没有改变
     */
    private boolean hasChanged() {
        return m.changed;
    }

    /**
     * 语音音量拖动条改变事件监听器
     */
    private class OnAudioVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float volume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100; //获取进度条语音音量值
            getMediaPlayer().setAudioVolume(volume); //设置媒体播放器语音音量
            getConfig().setAudioVolume(volume); //设置全局配置语音音量

            //刷新语音音量值
            int audioVolume = (int)(volume * 100);
            ui.audioVolumeTextView.setText(audioVolume + "");

            m.changed = true; //设置为有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * 音乐音量拖动条改变事件监听器
     */
    private class OnMusicVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float volume = ((float)ui.musicVolumeSeekBar.getProgress()) / 100; //获取进度条背景音乐音量值
            getMediaPlayer().setMusicVolume(volume); //设置媒体播放器背景音乐音量
            getConfig().setMusicVolume(volume); //设置全局配置背景音乐音量

            //刷新背景音乐音量值
            int musicVolume = (int)(volume * 100);
            ui.musicVolumeTextView.setText(musicVolume + "");

            m.changed = true; //设置为有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否有改变
         */
        boolean changed = false;

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
     * 界面类
     */
    private class UI {
        /**
         * 语音音量进度条
         */
        SeekBar audioVolumeSeekBar;

        /**
         * 音乐音量进度条
         */
        SeekBar musicVolumeSeekBar;

        /**
         * 语音音量文本框
         */
        TextView audioVolumeTextView;

        /**
         * 背景音乐音量文本框
         */
        TextView musicVolumeTextView;

        /**
         * 背景音乐布局
         */
        RelativeLayout musicLayout;
    }
}
