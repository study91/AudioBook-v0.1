package com.study91.audiobook.book.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ServiceConnection;
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
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.system.SystemManager;

import java.util.List;

/**
 * 目录视图适配器
 */
class BookCatalogViewAdapter extends BaseExpandableListAdapter {
    private UI ui = new UI(); //私有界面
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public BookCatalogViewAdapter(Context context) {
        m.context = context; //应用程序上下文
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

        IBook book = SystemManager.getUser(getContext()).getCurrentBook(); //全局书
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

        //设置当前项背景色
        if (catalog.getIndex() == book.getCurrentAudio().getIndex()) {
            view.setBackgroundResource(R.color.catalog_group_current); //设置背景色
            ui.group.titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE); //当前项标题设置为超长滚动
        }

        ui.group.titleTextView.setText(catalog.getTitle()); //目录标题

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //载入列表子视图布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_child_view, parent, false);

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
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
         * 媒体播放器
         */
        IBookMediaPlayer mediaPlayer;
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
             * 充许播放按钮
             */
            Button allowPlayAudioButton;
        }
    }
}
