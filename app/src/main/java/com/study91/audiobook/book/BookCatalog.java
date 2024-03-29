package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.dict.DisplayMode;
import com.study91.audiobook.dict.FamiliarLevel;
import com.study91.audiobook.system.IConfig;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.ImageTools;
import com.study91.audiobook.tools.MediaTools;

import java.util.List;

/**
 * 目录
 */
class BookCatalog implements IBookCatalog {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @param index 目录索引
     */
    BookCatalog(Context context, int bookID, int index) {
        m.context = context; //应用程序上下文
        load(bookID, index); //载入
    }

    @Override
    public int getCatalogID() {
        return m.catalogID;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public int getIndex() {
        return m.index;
    }

    @Override
    public int getPosition() {
        List<IBookCatalog> catalogs = getBook().getCatalogs(); //目录集合

        //遍历查询当前语音索引在目录中的位置
        for (int i = 0; i < catalogs.size(); i++) {
            IBookCatalog catalog = catalogs.get(i); //目录

            if (catalog.getIndex() == getIndex()) {
                m.position = i;
                break;
            }
        }

        return m.position;
    }

    @Override
    public int getPageNumber() {
        return m.pageNumber;
    }

    @Override
    public IBookPage getPage() {
        IBookPage page = null;

        //遍历查找目录页
        List<IBookPage> pages = getBook().getPages();
        for (IBookPage bookPage : pages) {
            if (bookPage.getPageNumber() == getPageNumber()) {
                page = bookPage;
                break;
            }
        }

        return page;
    }

    @Override
    public String getTitle() {
        return m.title;
    }

    @Override
    public boolean hasExplain() {
        return m.hasExplain;
    }

    @Override
    public boolean hasAudio() {
        return m.hasAudio;
    }

    @Override
    public void setAudioPlayEnable(boolean value) {
        m.allowPlayAudio = value;
    }

    @Override
    public boolean allowPlayAudio() {
        return m.allowPlayAudio;
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

    @Override
    public long getAudioEndTime() {
        long audioEndTime = 0;

        if (m.audioEndTime != null) {
            audioEndTime = MediaTools.parseTime(m.audioEndTime);
        }

        return audioEndTime;
    }

    @Override
    public DisplayMode getIconDisplayMode() {
        return m.iconDisplayMode;
    }

    @Override
    public String getIconFilename() {
        String iconFilename = null;

        if (m.iconFilename != null) {
            iconFilename = getBook().getIconPath() + m.iconFilename + ".png";
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
    public FamiliarLevel getFamiliarLevel() {
        return m.familiarLevel;
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
        return SystemManager.getUser(getContext()).getCurrentBook();
    }

    /**
     * 载入
     * @param bookID 书ID
     * @param index 目录索引
     */
    private void load(int bookID, int index) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取系统配置
            data = DataManager.createData(config.getBookDataSource()); //创建数据对象

            //查询字符串
            String sql = "SELECT * FROM [BookCatalog] " +
                    "WHERE " +
                    "[BookID] = " + bookID + " AND " +
                    "[Index] = " + index;

            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.bookID = bookID; //书ID
                m.index = index; //目录索引

                m.catalogID = cursor.getInt(cursor.getColumnIndex("CatalogID")); //目录ID
                m.pageNumber = cursor.getInt(cursor.getColumnIndex("PageNumber")); //页码
                m.title = cursor.getString(cursor.getColumnIndex("Title")); //目录标题
                m.hasAudio = cursor.getInt(cursor.getColumnIndex("HasAudio")) != 0; //是否有语音
                m.hasExplain = cursor.getInt(cursor.getColumnIndex("HasExplain")) != 0; //是否有解释

                if (m.hasAudio) {
                    m.allowPlayAudio = cursor.getInt(cursor.getColumnIndex("AllowPlayAudio")) != 0; //是否充许播放语音
                    m.audioFilename = cursor.getString(cursor.getColumnIndex("AudioFilename")); //语音文件名
                    m.audioStartTime = cursor.getString(cursor.getColumnIndex("AudioStartTime")); //语音开始时间
                    m.audioEndTime = cursor.getString(cursor.getColumnIndex("AudioEndTime")); //语音结束时间
                }

                int iconDisplayMode = cursor.getInt(cursor.getColumnIndex("IconDisplayMode")); //图标显示模式
                int familiarLevel = cursor.getInt(cursor.getColumnIndex("FamiliarLevel")); //熟悉级别

                m.iconDisplayMode = DisplayMode.values()[iconDisplayMode]; //图标显示模式
                m.familiarLevel = FamiliarLevel.values()[familiarLevel]; //熟悉级别
                m.iconFilename = cursor.getString(cursor.getColumnIndex("IconFilename")); //图标文件名
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
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
         * 目录ID
         */
        int catalogID;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 书
         */
        IBook book;

        /**
         * 目录索引
         */
        int index;

        /**
         * 目录位置
         */
        int position;

        /**
         * 目录页码
         */
        int pageNumber;

        /**
         * 目录标题
         */
        String title;

        /**
         * 是否有解释
         */
        boolean hasExplain;

        /**
         * 是否有语音
         */
        boolean hasAudio;

        /**
         * 是否充许播放语音
         */
        boolean allowPlayAudio;

        /**
         * 语音文件名
         */
        String audioFilename;

        /**
         * 语音开始时间
         */
        String audioStartTime;

        /**
         * 语音结束时间
         */
        String audioEndTime;

        /**
         * 图标显示模式
         */
        DisplayMode iconDisplayMode;

        /**
         * 图标文件名
         */
        String iconFilename;

        /**
         * 图标Drawable
         */
        Drawable iconDrawable;

        /**
         * 熟悉级别
         */
        FamiliarLevel familiarLevel;
    }
}
