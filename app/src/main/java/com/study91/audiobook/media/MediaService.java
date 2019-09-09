package com.study91.audiobook.media;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.system.IUser;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.ui.MainActivity;
import com.study91.audiobook.R;
import com.study91.audiobook.dict.ReceiverAction;
import com.study91.audiobook.tools.MediaTools;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 媒体服务
 */
public class MediaService extends Service {
    private final int TIMER_PERIOD = 250; //定时器时间周期（以毫秒为单位）
    private final int MEDIA_PLAYER_NOTIFICATION_ID = 73; //媒体消息ID
    private Field m = new Field(); //私有变量

    @Override
    public IBinder onBind(Intent intent) {
        return new MediaServiceBinder(); //返回媒体Binder
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerPhoneStateListener(); //注册电话状态监听器
        registerHeadsetPlugReceiver(); //注册耳机广播接收器

        //初始化当前打开的书
        IUser user = SystemManager.getUser(this); //获取全局用户
        getMediaPlayer().setAudioVolume(user.getAudioVolume()); //设置媒体播放器语音音量
        getMediaPlayer().setMusicVolume(user.getMusicVolume()); //设置媒体播放器音乐音量

        IBook book = user.getCurrentBook(); //获取当前打开的书
        IBookCatalog currentAudio = book.getCurrentAudio(); //获取当前语音目录

        Log.d("Test", "书：" + book.getBookName());
        if (currentAudio == null) {
            Log.d("Test", "当前语音为空");
        } else {
            Log.d("Test", "书：" + book.getBookName());
            Log.d("Test", "语音目录：" + currentAudio.getTitle());
        }

        //设置语音文件
        getMediaPlayer().setAudioFile(
                currentAudio.getAudioFilename(),
                currentAudio.getTitle(),
                currentAudio.getIconFilename());
        getMediaPlayer().setSoundType(book.getSoundType());
    }

    @Override
    public void onDestroy() {
        stopTimer(); //停止定时器
        cancelNotification(); //取消通知
        unregisterHeadsetPlugReceiver(); //注销耳机广播接收器
        if(m.mediaPlayer != null) getMediaPlayer().release(); //释放书媒体播放器
        super.onDestroy();
    }

    /**
     * 获取媒体播放器
     * @return 书媒体播放器
     */
    private IBookMediaPlayer getMediaPlayer() {
        if (m.mediaPlayer == null) {
            m.mediaPlayer = new BookMediaPlayer(this);
            m.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startTimer(); //启动定时器
                }
            });

            //设置播放完成事件监听器
            m.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopTimer(); //停止定时器

                    //获取全局书
                    IBook currentBook = SystemManager.getUser(
                            getApplicationContext()).getCurrentBook();

                    currentBook.moveToNextAudio(); //移动到下一个语音目录
                    IBookCatalog currentAudio = currentBook.getCurrentAudio(); //获取当前语音

                    //设置语音文件
                    m.mediaPlayer.setAudioFile(
                            currentAudio.getAudioFilename(),
                            currentAudio.getTitle(),
                            currentAudio.getIconFilename());

                    m.mediaPlayer.play(); //播放
                    refresh(); //刷新
                    startTimer(); //启动定时器
                }
            });
        }

        return m.mediaPlayer;
    }

    /**
     * 启动定时器
     */
    private void startTimer() {
        if (m.timer == null) {
            m.timer = new Timer();
        }

        if (m.timerTask == null) {
            m.timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (m.mediaPlayer != null) {
                        refresh(); //刷新
                    }
                }
            };
        }

        if (!m.timeStarted) {
            m.timer.schedule(m.timerTask, 0, TIMER_PERIOD);
            m.timeStarted = true;
        }
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        //停止定时器任务
        if (m.timerTask != null) {
            m.timerTask.cancel();
            m.timerTask = null;
        }

        //停止定时器
        if (m.timer != null) {
            m.timer.cancel();
            m.timer = null;
        }

        m.timeStarted = false;
    }

    /**
     * 刷新
     */
    private void refresh() {
        //发送广播（刷新任务由接收广播的客户端进行处理
        Intent intent = new Intent(ReceiverAction.CLIENT.toString());
        sendBroadcast(intent);

        //显示通知
        showNotification();
    }

    /**
     * 注册电话状态监听器
     */
    private void registerPhoneStateListener() {
        m.telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE); //获取电话管理器

        if (m.telephonyManager != null) {
            //开始监听电话状态
            m.phoneStateListener = new CustomPhoneStateListener(); //实例化电话状态监听器
            m.telephonyManager.listen(m.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    /**
     * 注册耳机广播接收器
     */
    private void registerHeadsetPlugReceiver() {
        m.headsetPlugReceiver = new HeadSetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(m.headsetPlugReceiver, intentFilter);
    }

    /**
     * 注销耳机广播接收器
     */
    private void unregisterHeadsetPlugReceiver() {
        if (m.headsetPlugReceiver != null) {
            unregisterReceiver(m.headsetPlugReceiver);
        }
    }

    /**
     * 显示通知
     */
    private void showNotification() {
        String CHANNEL_ID = getPackageName();
        String CHANNEL_NAME = "Channel " + getPackageName();
        NotificationChannel notificationChannel;

        //SDK >= 26 时才创建 NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        //创建Intent
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        //创建PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher); //设置通知小图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)); //设置通知大图标
        builder.setContentIntent(pendingIntent); //设置通知内容Intent
        builder.setContentTitle(getMediaPlayer().getAudioTitle()); //设置通知标题

        int position = getMediaPlayer().getPosition(); //媒体播放位置
        if (position >= 0) {
            int length = getMediaPlayer().getLength(); //媒体长度
            builder.setContentText(MediaTools.parseTime(position) + "/" + MediaTools.parseTime(length)); //设置通知内容
            builder.setProgress(length, position, false); //设置通知进度条
        }

        //显示为前台通知，避免服务被清除（需要在AndroidManifest.xml中添加FOREGROUND_SERVICE权限）
        startForeground(MEDIA_PLAYER_NOTIFICATION_ID, builder.build());
    }

    /**
     * 取消通知
     */
    private void cancelNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(MEDIA_PLAYER_NOTIFICATION_ID);
    }

    /**
     * 媒体服务Binder
     */
    public class MediaServiceBinder extends Binder {
        /**
         * 获取媒体播放器
         * @return 媒体播放器
         */
        public IBookMediaPlayer getMediaPlayer() {
            return MediaService.this.getMediaPlayer();
        }
    }

    /**
     * 客户电话状态监听器
     */
    private class CustomPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: //电话挂断
                    //因为来电暂停时继续播放
                    if (m.callPaused) {
                        getMediaPlayer().play();
                        m.callPaused = false; //设置为不是来电暂停状态
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING: //电话响铃
                    //暂停播放
                    if (getMediaPlayer().isPlaying()) {
                        getMediaPlayer().pause();
                        m.callPaused = true; //设置为来电暂停状态
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //来电接通 或者 去电，去电接通  但是没法区分
                    //暂停播放
                    if (getMediaPlayer().isPlaying()) {
                        getMediaPlayer().pause();
                        m.callPaused = true; //设置为来电暂停状态
                    }
                    break;
            }
        }
    }

    /**
     * 耳机广播接收器
     */
    private class HeadSetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                //蓝牙断开时暂停播放
                if (getMediaPlayer().isPlaying()) {
                    getMediaPlayer().pause();
                }
            }
        }
    }

    /**
     * 私有变量类
     */
    private class Field {
        /**
         * 书媒体播放器
         */
        IBookMediaPlayer mediaPlayer;

        /**
         * 定时器
         */
        Timer timer;

        /**
         * 定时器任务
         */
        TimerTask timerTask;

        /**
         * 定时器是否启动（true=已启动，false=未启动）
         */
        boolean timeStarted = false;

        /**
         * 来电暂停状态（默认为false）
         */
        boolean callPaused = false;

        /**
         * 电话状态监听器
         */
        PhoneStateListener phoneStateListener;

        /**
         * 电话管理器
         */
        TelephonyManager telephonyManager;

        /**
         * 耳机广播接收器
         */
        BroadcastReceiver headsetPlugReceiver;
    }
}
