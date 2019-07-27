package com.study91.audiobook.book.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;

/**
 * 目录视图
 */
public class BookCatalogView extends RelativeLayout {
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public BookCatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.book_catalog_view, this);

        ui.listView = (ExpandableListView) findViewById(R.id.catalogExpandableListView); //获取列表视图
        ui.listView.setGroupIndicator(null); //去掉默认的下拉箭头图标
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