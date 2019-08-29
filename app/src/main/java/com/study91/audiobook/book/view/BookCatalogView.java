package com.study91.audiobook.book.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.dict.ReceiverAction;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaService;
import com.study91.audiobook.system.SystemManager;

import java.util.List;

/**
 * 目录视图
 */
public class BookCatalogView extends RelativeLayout {
    private UI ui = new UI(); //界面
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public BookCatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.catalog_view, this);

        ui.listView = (ExpandableListView) findViewById(R.id.catalogExpandableListView); //获取列表视图
        ui.listView.setGroupIndicator(null); //去掉默认的下拉箭头图标
        ui.listView.setAdapter(getBookCatalogViewAdapter()); //设置目录视图适配器

        IBook book = SystemManager.getUser(getContext()).getCurrentBook(); //全局书
        ui.listView.setSelection(book.getCurrentAudio().getPosition()); //设置列表选择项

        //设置列表视图组项展开事件监听器
        ui.listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                List<IBookCatalog> catalogs = SystemManager.getUser(getContext()).getCurrentBook().getCatalogs(); //目录集合

                for (int i = 0; i < catalogs.size(); i++) {
                    if (groupPosition != i && ui.listView.isGroupExpanded(groupPosition)) {
                        ui.listView.collapseGroup(i);
                    }
                }
            }
        });

        bindMediaService(); //绑定媒体服务
        registerMediaBroadcastsReceiver(); //注册媒体广播接收器
    }

    @Override
    protected void onDetachedFromWindow() {
        unregisterMediaBroadcastReceiver(); //注销媒体广播接收器
        unbindMediaService(); //取消媒体服务绑定

        super.onDetachedFromWindow();
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
                    boolean isRefresh = false;

                    IBook book = SystemManager.getUser(getContext()).getCurrentBook(); //全局书
                    if (m.currentAudioIndex != book.getCurrentAudio().getIndex()) {
                        m.currentAudioIndex = book.getCurrentAudio().getIndex(); //重置当前语音索引
                        isRefresh = true;
                        Log.d("BookCatalogView", "当前语音=" + book.getCurrentAudio().getTitle());
                    } else if (m.isPlay != m.mediaPlayer.isPlaying()) {
                        m.isPlay = m.mediaPlayer.isPlaying(); //缓存是否正在播放状态
                        isRefresh = true;
                        Log.d("BookCatalogView", "是否播放=" + m.isPlay);
                    }

                    //刷新列表
                    if (isRefresh) {
                        m.adapter.notifyDataSetChanged();
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
     * 获取目录视图适配器
     * @return 目录视图适配器
     */
    private BookCatalogViewAdapter getBookCatalogViewAdapter() {
        if (m.adapter == null) {
            m.adapter = new BookCatalogViewAdapter(getContext());
        }

        return m.adapter;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 当前语音索引
         */
        int currentAudioIndex;

        /**
         * 是否正在播放
         */
        boolean isPlay;

        /**
         * 目录视图适配器
         */
        BookCatalogViewAdapter adapter;

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
         * 列表视图
         */
        ExpandableListView listView;
    }
}
