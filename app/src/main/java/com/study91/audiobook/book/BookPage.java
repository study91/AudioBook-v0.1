package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.system.IConfig;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.ImageTools;
import com.study91.audiobook.tools.MediaTools;

import java.util.List;

/**
 * 页
 */
class BookPage implements IBookPage {
    private Field m = new Field(); //私有变量

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @param pageNumber 页码
     */
    BookPage(Context context, int bookID, int pageNumber) {
        m.context = context; //应用程序上下文
        load(bookID, pageNumber); //载入
    }

    @Override
    public int getPageID() {
        return 0;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public int getPageNumber() {
        return m.pageNumber;
    }

    @Override
    public int getPosition() {
        List<IBookPage> pages = getBook().getPages(); //页集合

        //遍历查询当前页在页集合中的位置
        for (int i = 0; i < pages.size(); i++) {
            IBookPage page = pages.get(i); //页

            if (page.getPageNumber() == getPageNumber()) {
                m.position = i;
                break;
            }
        }
        return m.position;
    }

    @Override
    public IBookCatalog getCatalog() {
        IBookCatalog bookCatalog = null;

        IBook book = getBook();
        List<IBookCatalog> catalogs = book.getCatalogs();

        //遍历查找当前页所属的目录
        for (int i = catalogs.size(); i > 0; i--) {
            IBookCatalog catalog = catalogs.get(i);
            if (catalog.getPageNumber() <= getPageNumber()) {
                bookCatalog = catalog;
                break;
            }
        }

        return bookCatalog;
    }

    @Override
    public String getImageFilename() {
        String imageFilename = null;

        if (m.imageFilename != null) {
            imageFilename = getBook().getImagePath() + m.imageFilename + ".jpg";
        }
        return imageFilename;
    }

    @Override
    public Drawable getImageDrawable() {
        if (m.imageDrawable == null) {
            m.imageDrawable = ImageTools.getDrawable(getContext(), getImageFilename());
        }

        return m.imageDrawable;
    }

    @Override
    public String getIconFilename() {
        String iconFilename = null;

        if (m.imageFilename != null) {
            iconFilename = getBook().getIconPath() + m.imageFilename + ".png";
        }

        return iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        if (m.iconDrawable == null) {
            m.iconDrawable = ImageTools.getDrawable(getContext(), getIconFilename());
        }

        return m.iconDrawable;
    }

    @Override
    public boolean hasAudio() {
        return m.hasAudio;
    }

    @Override
    public String getAudioFilename() {
        String audioFilename = null;

        if (m.audioFilename != null) {
            audioFilename = getBook().getAudioPath() + m.audioFilename + ".mp3";
        }

        return audioFilename;
    }

    @Override
    public long getAudioStartTime() {
        long audioStartTime = 0;

        if (m.audioStartTime != null) {
            audioStartTime = MediaTools.parseTime(m.audioStartTime);
        }

        return audioStartTime;
    }

    /**
     * 获取应用程序上下文
     *
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
        return SystemManager.getUser(getContext()).getCurrentBook();
    }

    /**
     * 载入
     * @param bookID 书ID
     * @param pageNumber 页码
     */
    private void load(int bookID, int pageNumber) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取系统配置
            data = DataManager.createData(config.getBookDataSource()); //创建数据对象

            //查询字符串
            String sql = "SELECT * FROM [BookPage] " +
                    "WHERE " +
                    "[BookID] = " + bookID + " AND " +
                    "[PageNumber] = " + pageNumber;

            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.bookID = bookID; //书ID
                m.pageNumber = pageNumber; //页码

                m.pageID = cursor.getInt(cursor.getColumnIndex("PageID")); //页ID
                m.hasAudio = cursor.getInt(cursor.getColumnIndex("HasAudio")) != 0; //是否有语音

                //图片及图标文件名
                m.imageFilename = cursor.getString(cursor.getColumnIndex("ImageFilename")); //图片文件名

                if (m.hasAudio) {
                    m.audioFilename = cursor.getString(cursor.getColumnIndex("AudioFilename")); //语音文件名
                    m.audioStartTime = cursor.getString(cursor.getColumnIndex("AudioStartTime")); //语音开始时间
                }
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 私有字段类
     */
    private class Field{
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 页ID
         */
        int pageID;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 书
         */
        IBook book;

        /**
         * 页码
         */
        int pageNumber;

        /**
         * 页位置
         */
        int position;

        /**
         * 图片文件名
         */
        String imageFilename;

        /**
         * 图片Drawable
         */
        Drawable imageDrawable;

        /**
         * 图标Drawable
         */
        Drawable iconDrawable;

        /**
         * 是否有语音
         */
        boolean hasAudio;

        /**
         * 语音文件名
         */
        String audioFilename;

        /**
         * 语音开始时间
         */
        String audioStartTime;
    }
}
