package com.study91.audiobook.book.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.dict.ReceiverAction;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaService;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.ui.PageActivity;

import java.util.List;

/**
 * 目录视图适配器（注：退出时必须执行 release()方法 释放资源）
 */
class BookCatalogViewAdapter extends BaseExpandableListAdapter {
    private UI ui = new UI(); //私有界面
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    BookCatalogViewAdapter(Context context) {
        m.context = context; //应用程序上下文
        bindMediaService(); //绑定媒体服务
        registerMediaBroadcastsReceiver(); //注册媒体广播接收器
    }

    /**
     * 释放资源
     */
    public void release() {
        unbindMediaService(); //取消绑定媒体服务
        unregisterMediaBroadcastReceiver(); //注销媒体广播接收器
    }

    @Override
    public int getGroupCount() {
        return getCatalogs().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getCatalogs().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getCatalogs().get(groupPosition).getIndex();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //载入列表组布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_group_view, parent, false);

        IBook book = getBook(); //全局书
        List<IBookCatalog> catalogs = book.getCatalogs(); //目录集合
        IBookCatalog catalog = catalogs.get(groupPosition); //目录

        //加载控件
        ui.group.iconImageView = (ImageView) view.findViewById(R.id.iconImageView); //图标
        ui.group.pageTextView = (TextView) view.findViewById(R.id.pageTextView); //页码
        ui.group.titleTextView = (TextView) view.findViewById(R.id.titleTextView); //标题
        ui.group.playButton = (Button) view.findViewById(R.id.playButton); //播放按钮
        ui.group.loopImageView = (ImageView) view.findViewById(R.id.loopImageView); //循环图标

        ui.group.iconImageView.setImageDrawable(catalog.getIconDrawable()); //设置图标

        //播放按钮（注：如果语音开关值为false时，不显示播放按钮）
        ui.group.playButton.setFocusable(false);
        if (!catalog.hasAudio()) {
            ui.group.playButton.setVisibility(View.INVISIBLE);
        }

        //设置单击事件监听器
        ui.group.iconImageView.setOnClickListener(new OnDisplayButtonClickListener(catalog)); //图标
        ui.group.playButton.setOnClickListener(new OnPlayButtonClickListener(catalog)); //播放

        //设置当前项背景色
        if (catalog.getIndex() == book.getCurrentAudio().getIndex()) {
            view.setBackgroundResource(R.color.catalog_group_current); //设置背景色
            ui.group.titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE); //当前项标题设置为超长滚动
            if (isPlaying()) { //播放状态
                ui.group.playButton.setBackgroundResource(R.drawable.catalog_group_pause); //暂停图标
            } else { //暂停状态
                ui.group.playButton.setBackgroundResource(R.drawable.catalog_group_play); //播放图标
            }
        }

        if (catalog.hasAudio() && catalog.allowPlayAudio()) {
            if (catalog.getIndex() == getBook().getFirstAudio().getIndex()) {
                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_first); //复读起点语音图标
            } else if (catalog.getIndex() == getBook().getLastAudio().getIndex()) {
                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_last); //复读终点语音图标
            } else {
                ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_middle); //复读中间语音图标
            }

            //复读起点和复读终点相同时，不显示循环图标
            if (book.getFirstAudio().getIndex() == book.getLastAudio().getIndex()) {
                ui.group.loopImageView.setVisibility(View.INVISIBLE); //不显示循环图标
            }
        }

        ui.group.titleTextView.setText(catalog.getTitle()); //目录标题

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //载入列表子视图布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_child_view, parent, false);

        IBook book = getBook(); //全局书
        List<IBookCatalog> catalogs = book.getCatalogs(); //目录集合
        IBookCatalog catalog = catalogs.get(groupPosition); //目录

        //载入控件
        ui.child.firstButton = (Button) view.findViewById(R.id.firstButton); //复读起点按钮
        ui.child.lastButton = (Button) view.findViewById(R.id.lastButton); //复读终点按钮
        ui.child.displayButton = (Button) view.findViewById(R.id.displayButton); //显示按钮
        ui.child.explainButton = (Button) view.findViewById(R.id.explainButton); //详解按钮
        ui.child.playEnableButton = (Button) view.findViewById(R.id.playEnableButton); //播放开关按钮

        //设置事件监听器
        ui.child.firstButton.setOnClickListener(new OnFirstButtonClickListener(catalog)); //复读起点单击事件
        ui.child.lastButton.setOnClickListener(new OnLastButtonClickListener(catalog)); //复读终点单击事件
        ui.child.displayButton.setOnClickListener(new OnDisplayButtonClickListener(catalog)); //显示按钮单击事件
        ui.child.playEnableButton.setOnClickListener(new OnPlayEnableButtonClickListener(catalog)); //复读开关单击事件

        //没有解释的目录，关闭详解按钮
        if (!catalog.hasExplain()) {
            ui.child.explainButton.setVisibility(View.GONE);
        }

        if (catalog.hasAudio()) {
            if (catalog.getIndex() < book.getCurrentAudio().getIndex()) { //页号小于当前目录页号
                ui.child.lastButton.setVisibility(View.GONE); //复读终点按钮禁用
                ui.child.firstButton.setVisibility(View.VISIBLE); //复读起点按钮可用
            } else if (catalog.getIndex() > book.getCurrentAudio().getIndex()) { //页号大于当前目录页号
                ui.child.lastButton.setVisibility(View.VISIBLE); //复读终点按钮可用
                ui.child.firstButton.setVisibility(View.GONE); //复读起点按钮禁用
            } else { //页号等于当前目录页号
                ui.child.playEnableButton.setEnabled(false); //播放开关按钮禁用
                ui.child.playEnableButton.setVisibility(View.GONE);
            }

            if (catalog.getIndex() == getBook().getFirstAudio().getIndex()) {
                ui.child.firstButton.setVisibility(View.GONE); //复读起点按钮禁用
            }

            if (catalog.getIndex() == getBook().getLastAudio().getIndex()) {
                ui.child.lastButton.setVisibility(View.GONE); //复读终点按钮禁用
            }

            if(catalog.allowPlayAudio()) { //播放开关打开
                ui.child.playEnableButton.setBackgroundResource(R.drawable.catalog_child_cancel);
            } else { //播放开关关闭
                ui.child.playEnableButton.setBackgroundResource(R.drawable.catalog_child_add);
            }
        } else {
            //没有语音的目录，关闭复读起点、复读终点、播放开关按钮
            ui.child.firstButton.setVisibility(View.GONE); //禁用复读起点按钮
            ui.child.lastButton.setVisibility(View.GONE); //禁用复读终点按钮
            ui.child.playEnableButton.setVisibility(View.GONE); //禁用播放开关按钮
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
                m.bookMediaPlayer = binder.getMediaPlayer();
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

                    IBook book = getBook(); //全局书
                    IBookMediaPlayer mediaPlayer = getBookMediaPlayer(); //书媒体播放器

                    if (getCurrentAudioIndex() != book.getCurrentAudio().getIndex()) {
                        //当前语音索引发生变化
                        setCurrentAudioIndex(book.getCurrentAudio().getIndex()); //重置当前语音索引
                        isRefresh = true;
                    } else if (isPlaying() != mediaPlayer.isPlaying()) {
                        //播放状态发生变化
                        setIsPlaying(mediaPlayer.isPlaying()); //缓存是否正在播放状态
                        isRefresh = true;
                    }

                    //刷新列表
                    if (isRefresh) {
                        notifyDataSetChanged();
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
     * 获取书媒体播放器
     * @return 书媒体播放器
     */
    private IBookMediaPlayer getBookMediaPlayer() {
        return m.bookMediaPlayer;
    }

    /**
     * 设置是否正在播放
     * @param isPlaying true=正播放，false=没有播放
     */
    private void setIsPlaying(boolean isPlaying) {
        m.isPlaying = isPlaying;
    }

    /**
     * 是否正在播放
     * @return true=正播放，false=没有播放
     */
    private boolean isPlaying() {
        return m.isPlaying;
    }

    /**
     * 设置当前语音索引
     * @param index 索引
     */
    private void setCurrentAudioIndex(int index) {
        m.currentAudioIndex = index;
    }

    /**
     * 获取当前语音索引
     * @return 当前语音索引
     */
    private int getCurrentAudioIndex() {
        return m.currentAudioIndex;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取全局书
     * @return 全局书
     */
    private IBook getBook() {
        return SystemManager.getUser(getContext()).getCurrentBook(); //全局书
    }

    /**
     * 获取列表集合
     * @return 列表集合
     */
    private List<IBookCatalog> getCatalogs() {
        if (m.catalogs == null) {
            m.catalogs = SystemManager.getUser(getContext()).getCurrentBook().getCatalogs();
        }

        return m.catalogs;
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
         * 是否正在播放（true=正播放，false=没有播放）
         */
        boolean isPlaying;

        /**
         * 当前语音索引
         */
        int currentAudioIndex;

        /**
         * 书列表集合
         */
        List<IBookCatalog> catalogs;

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
         * 目录组项
         */
        CatalogGroup group = new CatalogGroup();

        /**
         * 目录子项
         */
        CatalogChild child = new CatalogChild();

        /**
         * 目录组项类
         */
        private class CatalogGroup {
            /**
             * 图标
             */
            ImageView iconImageView;

            /**
             * 索引
             */
            TextView pageTextView;

            /**
             * 标题
             */
            TextView titleTextView;

            /**
             * 循环图标
             */
            ImageView loopImageView;

            /**
             * 播放按钮
             */
            Button playButton;
        }

        /**
         * 目录子项类
         */
        private class CatalogChild {
            /**
             * 复读起点按钮
             */
            Button firstButton;

            /**
             * 复读终点按钮
             */
            Button lastButton;

            /**
             * 显示按钮
             */
            Button displayButton;

            /**
             * 解释按钮
             */
            Button explainButton;

            /**
             * 播放开关按钮
             */
            Button playEnableButton;
        }
    }

    /**
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        OnPlayButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog;
        }

        @Override
        public void onClick(View v) {
            IBookMediaPlayer mediaPlayer = getBookMediaPlayer(); //书媒体播放器

            if (getCurrentAudioIndex() == getCatalog().getIndex()) {
                //点击的是当前目录的播放按钮
                if (isPlaying()) {
                    mediaPlayer.pause(); //如果正在播放，就暂停播放
                } else {
                    mediaPlayer.play(); //如果暂停播放，就开始播放
                }
            } else {
                //点击的不是当前目录的播放按钮
                IBook book = getBook(); //全局书
                IBookCatalog catalog = getCatalog(); //当前目录
                book.setCurrentAudio(catalog); //重置当前语音目录

                //重置媒体播放器语音文件并播放语音
                mediaPlayer.setAudioFile(catalog.getAudioFilename(), catalog.getTitle(), catalog.getIconFilename());
                mediaPlayer.play(); //播放当前语音
            }
        }

        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 复读起点按钮单击事件监听器
     */
    private class OnFirstButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnFirstButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //有声书内容
        }

        @Override
        public void onClick(View v) {
            getBook().setFirstAudio(getCatalog()); //重置复读起点
            notifyDataSetChanged();
        }


        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 复读终点按钮单击事件监听器
     */
    private class OnLastButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnLastButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //有声书内容
        }

        @Override
        public void onClick(View v) {
            getBook().setLastAudio(getCatalog()); //重置复读终点
            notifyDataSetChanged();
        }

        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 播放开关按钮单击事件监听器
     */
    private class OnPlayEnableButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnPlayEnableButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //目录
        }

        @Override
        public void onClick(View v) {
            getBook().resetAudioPlayEnable(getCatalog()); //重置语音播放开关
            notifyDataSetChanged(); //刷新
        }

        /**
         * 获取目录
         *
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 有声书内容
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 显示按钮单击事件监听器
     */
    private class OnDisplayButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnDisplayButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //目录
        }

        @Override
        public void onClick(View v) {
            getBook().setCurrentPage(getCatalog().getPageNumber()); //重置当前显示页
            Intent intent = new Intent(getContext(), PageActivity.class);
            getContext().startActivity(intent);
        }

        /**
         * 获取目录
         *
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 有声书内容
             */
            IBookCatalog catalog;
        }
    }
}
