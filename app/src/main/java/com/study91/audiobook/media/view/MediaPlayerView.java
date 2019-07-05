package com.study91.audiobook.media.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.dict.ReceiverAction;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaService;
import com.study91.audiobook.tools.MediaTools;

public class MediaPlayerView extends RelativeLayout {
    private UI ui = new UI(); //界面
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public MediaPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.media_player_view, this);

        //载入界面控件
        ui.audioPositionTextView = (TextView) findViewById(R.id.audioPositionTextView); //语音位置文本框
        ui.audioLengthTextView = (TextView) findViewById(R.id.audioLengthTextView); //语音长度文本框
        ui.audioSeekBar = (SeekBar) findViewById(R.id.mediaSeekBar); //语音拖动条
        ui.iconImageView = (ImageView) findViewById(R.id.iconImageView); //图标按钮
        ui.pageTextView = (TextView) findViewById(R.id.pageTextView); //页号文本框
        ui.audioTitleTextView = (TextView) findViewById(R.id.audioTitleTextView); //标题文本框
        ui.playButton = (Button) findViewById(R.id.playButton); //播放按钮
        ui.synchronizationButton = (Button) findViewById(R.id.synchronizationButton); //同步按钮
        ui.fullScreenButton = (Button) findViewById(R.id.fullScreenButton); //全屏按钮
        ui.volumeButton = (Button) findViewById(R.id.volumeButton); //音量按钮
        ui.catalogButton = (Button) findViewById(R.id.catalogButton); //目录按钮

//        if(isInEditMode()) return; //解决可视化编辑器无法识别自定义控件的问题（此语句现在可以不用）

        bindMediaService(); //绑定媒体服务
        registerMediaBroadcastsReceiver(); //注册媒体广播接收器

        //设置控件事件
        ui.playButton.setOnClickListener(new OnPlayButtonClickListener()); //播放按钮单击事件
        ui.volumeButton.setOnClickListener(new OnVolumeButtonClickListener()); //音量按钮单击事件
        ui.audioSeekBar.setOnSeekBarChangeListener(new OnAudioSeekBarChangeListener()); //语音拖动条改变事件
    }

    @Override
    protected void onDetachedFromWindow() {
        unregisterMediaBroadcastReceiver(); //注销媒体广播接收器
        unbindMediaService(); //取消媒体服务绑定
        super.onDetachedFromWindow();
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
     * 注册媒体广播接收器
     */
    private void registerMediaBroadcastsReceiver() {
        if (m.mediaBroadcastReceiver == null) {
            m.mediaBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //MediaService.MediaServiceBinder binder = getMediaServiceBinder(); //媒体服务绑定

                    ui.audioPositionTextView.setText(MediaTools.parseTime(getMediaPlayer().getPosition())); //设置语音位置文本
                    ui.audioLengthTextView.setText(MediaTools.parseTime(getMediaPlayer().getLength())); //设置语音长度文本
                    ui.audioSeekBar.setMax(getMediaPlayer().getLength()); //设置语音进度条最大值
                    ui.audioSeekBar.setProgress(getMediaPlayer().getPosition()); //设置语音进度条播放位置

                    //设置标题内容
                    String title = getMediaPlayer().getAudioTitle().trim().replace("　", "");
                    ui.audioTitleTextView.setText(title);

                    //设置图标
                    if (getMediaPlayer().getIconDrawable() != null) {
                        ui.iconImageView.setImageDrawable(getMediaPlayer().getIconDrawable());
                    }

                    //设置播放图标
                    if (getMediaPlayer().isPlaying()) {
                        ui.playButton.setBackgroundResource(R.drawable.media_player_pause);
                    } else {
                        ui.playButton.setBackgroundResource(R.drawable.media_player_play);
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ReceiverAction.CLIENT.toString());
            getContext().registerReceiver(m.mediaBroadcastReceiver, intentFilter);
        }
    }

    /**
     * 注销媒体广播接收器
     */
    private void unregisterMediaBroadcastReceiver() {
        if (m.mediaBroadcastReceiver != null) {
            getContext().unregisterReceiver(m.mediaBroadcastReceiver);
        }
    }

    /**
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (getMediaPlayer().isPlaying()) {
                getMediaPlayer().pause();
            } else {
                getMediaPlayer().play();
            }
        }
    }

    /**
     * 音量按钮单击事件监听器
     */
    private class OnVolumeButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            VolumeDialog dialog = new VolumeDialog(getContext(), R.style.VolumeDialogTheme);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    /**
     * 语音拖动条改变事件监听器
     */
    private class OnAudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                getMediaPlayer().seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体服务连接
         */
        ServiceConnection mediaServiceConnection;

        /**
         * 媒体广播接收器
         */
        BroadcastReceiver mediaBroadcastReceiver;

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
         * 语音位置文本框
         */
        private TextView audioPositionTextView;

        /**
         * 语音长度文本框
         */
        private TextView audioLengthTextView;

        /**
         * 语音拖动条
         */
        private SeekBar audioSeekBar;

        /**
         * 图标
         */
        private ImageView iconImageView;

        /**
         * 页号文本框
         */
        private TextView pageTextView;

        /**
         * 语音标题文本框
         */
        private TextView audioTitleTextView;

        /**
         * 播放按钮
         */
        private Button playButton;

        /**
         * 同步按钮
         */
        private Button synchronizationButton;

        /**
         * 全屏按钮
         */
        private Button fullScreenButton;

        /**
         * 音量按钮
         */
        private Button volumeButton;

        /**
         * 目录按钮
         */
        private Button catalogButton;
    }
}
