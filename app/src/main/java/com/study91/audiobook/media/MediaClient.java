package com.study91.audiobook.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.study91.audiobook.dict.ReceiverAction;

/**
 * 媒体客户端
 * 说明：使用前必须执行 register() 方法进行注册，退出时必须执行 unregister() 方法注销
 */
public class MediaClient {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public MediaClient(Context context) {
        m.context = context; //应用程序上下文
    }

    /**
     * 注册媒体客户端
     */
    public void register() {
        bindMediaService(); //绑定媒体服务
    }

    /**
     * 注销媒体客户端
     */
    public void unregister() {
        //注销客户端广播接收器
        if (m.receiver != null) {
            getContext().unregisterReceiver(m.receiver);
        }

        //取消绑定媒体服务
        if (m.serviceConnection != null) {
            getContext().unbindService(m.serviceConnection);
        }
    }

    /**
     * 获取媒体播放器
     * @return 媒体播放器
     */
    public IBookMediaPlayer getMediaPlayer() {
        return m.mediaPlayer;
    }

    /**
     * 设置客户端广播接收器
     * @param receiver 客户端广播接收器
     */
    public void setOnReceiver(BroadcastReceiver receiver) {
        //注册客户端广播接收器
        if (receiver != null) {
            IntentFilter intentFilter = new IntentFilter(ReceiverAction.CLIENT.toString());
            getContext().registerReceiver(receiver, intentFilter);
            m.receiver = receiver;
        }
    }

    /**
     * 绑定媒体服务
     */
    private void bindMediaService() {
        //创建媒体服务Intent
        Intent intent = new Intent(getContext(), MediaService.class);

        //实例化媒体服务连接
        m.serviceConnection = new ServiceConnection() {
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
        getContext().bindService(intent, m.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 媒体播放器
         */
        IBookMediaPlayer mediaPlayer;

        /**
         * 客户端广播接收器
         */
        BroadcastReceiver receiver;

        /**
         * 服务端连接
         */
        ServiceConnection serviceConnection;
    }
}
