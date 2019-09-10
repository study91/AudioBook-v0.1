package com.study91.audiobook.media.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaClient;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.MediaTools;
import com.study91.audiobook.ui.PageActivity;

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

        getMediaClient().register(); //注册媒体客户端
        getMediaClient().setOnReceiver(new OnMediaClientBroadcastReceiver()); //设置媒体客户端广播接收器

        //载入界面控件
        ui.audioPositionTextView = (TextView) findViewById(R.id.audioPositionTextView); //语音位置文本框
        ui.audioLengthTextView = (TextView) findViewById(R.id.audioLengthTextView); //语音长度文本框
        ui.audioSeekBar = (SeekBar) findViewById(R.id.mediaSeekBar); //语音拖动条
        ui.iconImageView = (ImageView) findViewById(R.id.iconImageView); //图标
        ui.pageTextView = (TextView) findViewById(R.id.pageTextView); //页号文本框
        ui.bookNameTextView = (TextView) findViewById(R.id.bookNameTextView); //书名文本框
        ui.audioTitleTextView = (TextView) findViewById(R.id.audioTitleTextView); //标题文本框
        ui.playButton = (Button) findViewById(R.id.playButton); //播放按钮
        ui.synchronizationButton = (Button) findViewById(R.id.synchronizationButton); //同步按钮
        ui.fullScreenButton = (Button) findViewById(R.id.fullScreenButton); //全屏按钮
        ui.volumeButton = (Button) findViewById(R.id.volumeButton); //音量按钮
        ui.catalogButton = (Button) findViewById(R.id.catalogButton); //目录按钮

        //设置事件监听器
        ui.iconImageView.setOnClickListener(new OnIconClickListener()); //图标单击
        ui.audioTitleTextView.setOnClickListener(new OnTitleClickListener()); //标题单击
        ui.bookNameTextView.setOnClickListener(new OnTitleClickListener()); //书名单击

//        if(isInEditMode()) return; //解决可视化编辑器无法识别自定义控件的问题（此语句现在可以不用）

        //设置控件事件
        ui.playButton.setOnClickListener(new OnPlayButtonClickListener()); //播放按钮单击事件
        ui.volumeButton.setOnClickListener(new OnVolumeButtonClickListener()); //音量按钮单击事件
        ui.audioSeekBar.setOnSeekBarChangeListener(new OnAudioSeekBarChangeListener()); //语音拖动条改变事件
    }

    @Override
    protected void onDetachedFromWindow() {
        getMediaClient().unregister(); //注销媒体客户端
        super.onDetachedFromWindow();
    }

    /**
     * 显示页
     */
    private void showPage() {
        Intent intent = new Intent(getContext(), PageActivity.class);
        getContext().startActivity(intent);
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 图标单击事件监听器
     */
    private class OnIconClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            showPage(); //显示页
        }
    }

    /**
     * 标题单击事件监听器
     */
    private class OnTitleClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            showPage(); //显示页
        }
    }

    /**
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer();

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
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

                getMediaClient().getMediaPlayer().seekTo(progress);
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
     * 媒体客户端广播接收器
     */
    private class OnMediaClientBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer(); //获取媒体播放器

            ui.audioPositionTextView.setText(MediaTools.parseTime(mediaPlayer.getPosition())); //设置语音位置文本
            ui.audioLengthTextView.setText(MediaTools.parseTime(mediaPlayer.getLength())); //设置语音长度文本
            ui.audioSeekBar.setMax(mediaPlayer.getLength()); //设置语音进度条最大值
            ui.audioSeekBar.setProgress(mediaPlayer.getPosition()); //设置语音进度条播放位置

            //设置书名称
            IBook book = SystemManager.getUser(getContext()).getCurrentBook();
            ui.bookNameTextView.setText("【" + book.getBookName() + "】");

            //设置标题内容
            String title = mediaPlayer.getAudioTitle().trim().replace("　", "");
            ui.audioTitleTextView.setText(title);

            //设置图标
            if (mediaPlayer.getIconDrawable() != null) {
                ui.iconImageView.setImageDrawable(mediaPlayer.getIconDrawable());
            }

            //设置播放图标
            if (mediaPlayer.isPlaying()) {
                ui.playButton.setBackgroundResource(R.drawable.media_player_pause);
            } else {
                ui.playButton.setBackgroundResource(R.drawable.media_player_play);
            }
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体客户端
         */
        MediaClient mediaClient;
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
         * 书名称文本框
         */
        private TextView bookNameTextView;

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
