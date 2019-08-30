package com.study91.audiobook.book.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
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
    }

    @Override
    protected void onDetachedFromWindow() {
        getBookCatalogViewAdapter().release(); //释放目录视图适配器资源
        super.onDetachedFromWindow();
    }

    /**
     * 获取目录视图适配器
     * @return 目录视图适配器
     */
    private BookCatalogViewAdapter getBookCatalogViewAdapter() {
        if (m.bookCatalogViewAdapter == null) {
            m.bookCatalogViewAdapter = new BookCatalogViewAdapter(getContext());
        }

        return m.bookCatalogViewAdapter;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 目录视图适配器
         */
        BookCatalogViewAdapter bookCatalogViewAdapter;
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
