package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.study91.audiobook.R;
import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.dict.ContentType;
import com.study91.audiobook.dict.DictManager;
import com.study91.audiobook.dict.FullMode;
import com.study91.audiobook.dict.IDict;
import com.study91.audiobook.dict.LinkMode;
import com.study91.audiobook.dict.SoundType;
import com.study91.audiobook.system.IConfig;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.ImageTools;

import java.util.ArrayList;
import java.util.List;

/**
 * 书
 */
class Book implements IBook {
    private String TAG = "Book";
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param bookID 书ID
     */
    Book(Context context, int bookID) {
        m.context = context; //应用程序上下文
        load(bookID); //载入
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public String getBookName() {
        return m.bookName;
    }

    @Override
    public String getBookDepict() {
        return m.bookDepict;
    }

    @Override
    public String getPublish() {
        if (m.publish == null) {
            IDict dict = DictManager.createDict(getContext());
            m.publish = dict.getValue("Publish", m.publishID);
        }

        return m.publish;
    }

    @Override
    public String getGrade() {
        if (m.grade == null) {
            IDict dict = DictManager.createDict(getContext());
            m.grade = dict.getValue("Grade", m.gradeID);
        }

        return m.grade;
    }

    @Override
    public String getSubject() {
        if (m.subject == null) {
            IDict dict = DictManager.createDict(getContext());
            m.subject = dict.getValue("Subject", m.subjectID);
        }

        return m.subject;
    }

    @Override
    public String getTerm() {
        if (m.term == null) {
            IDict dict = DictManager.createDict(getContext());
            m.term = dict.getValue("Term", m.termID);
        }

        return m.term;
    }

    @Override
    public ContentType getContentType() {
        return m.contentType;
    }

    @Override
    public SoundType getSoundType() {
        return m.soundType;
    }

    @Override
    public FullMode getFullMode() {
        return m.fullMode;
    }

    @Override
    public LinkMode getTitleLinkMode() {
        return m.titleLinkMode;
    }

    @Override
    public LinkMode getIconLinkMode() {
        return m.iconLinkMode;
    }

    @Override
    public boolean allowSync() {
        return m.allowSync;
    }

    @Override
    public boolean syncEnable() {
        return m.syncEnable;
    }

    @Override
    public void moveToNextAudio() {
        IBookCatalog currentAudio = getCurrentAudio(); //获取当前语音
        IBookCatalog lastAudio = getLastAudio(); //获取最后一个语音

        if (currentAudio.getIndex() == lastAudio.getIndex()) {
            //如果当前语音是最后一个语音时执行
            setCurrentAudio(getFirstAudio()); //将第一个语音设置为当前语音
        } else {
            //当前语音不是最后一个语音时执行
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表

            //遍历所有目录，查找并设置下一个语音目录为当前目录
            for (int i = 0; i < catalogs.size(); i++) {
                IBookCatalog catalog = catalogs.get(i); //获取目录

                //目录有语音、充许播放语音且目录索引大于当前语音索引时执行
                if (catalog.hasAudio() &&
                        catalog.allowPlayAudio() &&
                        catalog.getIndex() > currentAudio.getIndex()) {
                    setCurrentAudio(catalog); //重置当前语音
                    break;
                }
            }
        }
    }

    @Override
    public void setCurrentAudio(IBookCatalog catalog) {
        //缓存的当前语音索引不等于参数语音目录索引时执行
        if (m.currentAudioIndex != catalog.getIndex()) {
            m.currentAudioIndex = catalog.getIndex(); //缓存当前语音索引
            updateCurrentAudio(catalog.getIndex()); //更新当前语音
        }

        m.currentAudio = catalog;
    }

    @Override
    public IBookCatalog getCurrentAudio() {
        //如果当前语音目录为空或缓存的当前语音索引不等于当前语音索引时检查当前语音
        if (m.currentAudio == null || m.currentAudioIndex != m.currentAudio.getIndex()) {
            checkCurrentAudio(); //检查当前语音
        }

        return m.currentAudio;
    }

    @Override
    public void setCurrentPage(int pageNumber) {
        if (pageNumber != m.currentPageNumber) {
            m.currentPageNumber = pageNumber; //缓存当前页码

            IData data = null; //数据对象

            try {
                IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
                data = DataManager.createData(config.getBookDataSource()); //创建数据对象

                //更新数据库
                String sql = "UPDATE [Book] SET [CurrentPage] = " + m.currentPageNumber + " " +
                        "WHERE [BookID] = " + getBookID();
                data.execute(sql); //执行更新
            } finally {
                if(data != null) data.close(); //关闭数据对象
            }
        }
    }

    @Override
    public IBookPage getCurrentPage() {
        //当前显示页为null或缓存当前页码（m.currentPageNumber）与当前显示页码不同时执行
        if (m.currentPage == null || m.currentPageNumber != m.currentPage.getPageNumber()) {
            IData data = null; //数据对象
            Cursor cursor = null; //数据指针

            try {
                IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
                data = DataManager.createData(config.getBookDataSource()); //创建数据对象

                ///查询当前显示页是否存在
                // 查询字符串
                String sql = "SELECT [PageNumber] FROM [BookPage] " +
                        "WHERE " +
                        "[BookID] = " + getBookID() + " AND " +
                        "[PageNumber] = " + m.currentPageNumber;

                cursor = data.query(sql);

                if (cursor.getCount() == 1) {
                    //页存在
                    m.currentPage = BookManager.createPage(getContext(), getBookID(), m.currentPageNumber);
                } else {
                    //页不存在，将第一页设置为当前显示页
                    sql = "SELECT [PageNumber] FROM [BookPage] " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " " +
                            "ORDER BY [PageNumber] LIMIT 1";

                    cursor = data.query(sql);

                    if (cursor.getCount() == 1) {
                        cursor.moveToFirst();

                        //设置当前显示页
                        int pageNumber = cursor.getInt(cursor.getColumnIndex("PageNumber")); //当前显示页
                        setCurrentPage(pageNumber);
                        m.currentPage = BookManager.createPage(getContext(), getBookID(), pageNumber);
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.currentPage;
    }

    @Override
    public String getCoverFilename() {
        String coverFilename = null;

        if (m.coverFilename != null) {
            coverFilename = getImagePath() + m.coverFilename + ".jpg";
        }

        return coverFilename;
    }

    @Override
    public Drawable getCoverDrawable() {
        if (m.coverDrawable == null) {
            m.coverDrawable = ImageTools.getDrawable(getContext(), getCoverFilename());
        }

        return m.coverDrawable;
    }

    @Override
    public String getIconFilename() {
        String iconFilename = null;

        if (m.coverFilename != null) {
            iconFilename = getIconPath() + m.coverFilename + ".png";
        }

        return iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        if (m.iconDrawable == null) {
            m.iconDrawable = ImageTools.getDrawable(getContext(), getCoverFilename());
        }

        return m.iconDrawable;
    }

    @Override
    public boolean isFavorite() {
        return m.isFavorite;
    }

    @Override
    public String getPackageName() {
        return m.packageName;
    }

    @Override
    public String getAudioPath() {
        String audioPath = getContext().getString(R.string.audio_path);
        audioPath = audioPath.replace("[PACKAGE]", getPackageName());
        return audioPath;
    }

    @Override
    public String getImagePath() {
        String imagePath = getContext().getString(R.string.image_path);
        imagePath = imagePath.replace("[PACKAGE]", getPackageName());
        return imagePath;
    }

    @Override
    public String getIconPath() {
        String iconPath = getContext().getString(R.string.icon_path);
        iconPath = iconPath.replace("[PACKAGE]", getPackageName());
        return iconPath;
    }

    @Override
    public List<IBookCatalog> getCatalogs() {
        if (m.catalogs == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
                data = DataManager.createData(config.getBookDataSource()); //创建数据对象

                //查询字符串
                String sql = "SELECT [Index] FROM [BookCatalog] " +
                        "WHERE [BookID] = " + getBookID() + " ORDER BY [Index]";

                cursor = data.query(sql); //查询数据

                if (cursor.getCount() > 0) {
                    m.catalogs = new ArrayList<>(); //实例化目录列表

                    //遍历目录并添加到目录列表
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int index = cursor.getInt(cursor.getColumnIndex("Index")); //目录索引
                        IBookCatalog catalog = BookManager.createCatalog(getContext(), getBookID(), index); //创建目录
                        m.catalogs.add(catalog); //添加到集合
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.catalogs;
    }

    @Override
    public List<IBookPage> getPages() {
        if (m.pages == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
                data = DataManager.createData(config.getBookDataSource()); //创建数据对象

                //查询字符串
                String sql = "SELECT [PageNumber] FROM [BookPage] " +
                        "WHERE [BookID] = " + getBookID() + " ORDER BY [PageNumber]";

                cursor = data.query(sql); //查询数据

                if (cursor.getCount() > 0) {
                    m.pages = new ArrayList<>(); //实例化页列表

                    //遍历页并添加到页列表
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int pageNumber = cursor.getInt(cursor.getColumnIndex("PageNumber")); //页
                        IBookPage page = BookManager.createPage(getContext(), getBookID(), pageNumber); //创建页
                        m.pages.add(page); //添加到集合
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.pages;
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
     * 载入
     * @param bookID 书ID
     */
    private void load(int bookID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
            data = DataManager.createData(config.getBookDataSource()); //创建数据对象

            //查询字符串
            String sql = "SELECT * FROM [Book] WHERE [BookID] = " + bookID;
            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.bookID = bookID; //书ID
                m.bookName = cursor.getString(cursor.getColumnIndex("BookName")); //书名称
                m.bookDepict = cursor.getString(cursor.getColumnIndex("BookDepict")); //书描述
                m.allowSync = cursor.getInt(cursor.getColumnIndex("AllowSync")) != 0; //是否充许同步
                m.syncEnable = cursor.getInt(cursor.getColumnIndex("SyncEnable")) != 0; //同步开关
                m.isFavorite = cursor.getInt(cursor.getColumnIndex("IsFavorite")) != 0; //是否收藏
                m.coverFilename = cursor.getString(cursor.getColumnIndex("CoverFilename")); //封面图片文件名
                m.packageName = cursor.getString(cursor.getColumnIndex("PackageName")); //包名

                m.publishID = cursor.getInt(cursor.getColumnIndex("Publish")); //出版社
                m.gradeID = cursor.getInt(cursor.getColumnIndex("Grade")); //年级
                m.subjectID = cursor.getInt(cursor.getColumnIndex("Subject")); //科目
                m.termID = cursor.getInt(cursor.getColumnIndex("Term")); //学期

                int contentType = cursor.getInt(cursor.getColumnIndex("ContentType")); //内容类型
                int soundType = cursor.getInt(cursor.getColumnIndex("SoundType")); //声音类型
                int fullMode = cursor.getInt(cursor.getColumnIndex("FullMode")); //全屏模式
                int titleLinkMode = cursor.getInt(cursor.getColumnIndex("TitleLinkMode")); //标题链接模式
                int iconLinkMode = cursor.getInt(cursor.getColumnIndex("IconLinkMode")); //图标链接模式
                m.contentType = ContentType.values()[contentType]; //内容类型
                m.soundType = SoundType.values()[soundType]; //声音类型
                m.fullMode = FullMode.values()[fullMode]; //全屏模式
                m.titleLinkMode = LinkMode.values()[titleLinkMode]; //标题链接模式
                m.iconLinkMode = LinkMode.values()[iconLinkMode]; //图标链接模式

                m.currentAudioIndex = cursor.getInt(cursor.getColumnIndex("CurrentAudio")); //当前语音目录索引
                m.currentPageNumber = cursor.getInt(cursor.getColumnIndex("CurrentPage")); //当前显示页码
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 检查当前语音
     */
    private void checkCurrentAudio() {
        List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表

        //遍历检查所有目录
        for (int i = 0; i < catalogs.size(); i++) {
            IBookCatalog catalog = catalogs.get(i); //目录

            if (catalog.getIndex() <= m.currentAudioIndex) {
                //目录索引小于或等于当前语音目录索引时执行（表示当前语音目录存在）
                if (catalog.hasAudio() && catalog.allowPlayAudio() &&
                        catalog.getIndex() == m.currentAudioIndex) {
                    setCurrentAudio(catalog); //设置当前语音目录
                    break;
                }
            } else {
                //目录索引大于当前语音目录索引时执行（表示当前语音目录不存在）
                if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                    setCurrentAudio(catalog); //重置当前语音
                    break;
                }
            }
        }
    }

    /**
     * 更新当前语音
     * @param index 语音索引
     */
    private void updateCurrentAudio(int index) {
        IData data = null; //数据对象

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取系统配置
            data = DataManager.createData(config.getBookDataSource()); //创建数据对象

            //更新数据库
            String sql = "UPDATE [Book] SET [CurrentAudio] = " + index + " " +
                    "WHERE [BookID] = " + getBookID();
            data.execute(sql); //执行更新
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 获取第一个语音目录
     * @return 第一个语音目录
     */
    private IBookCatalog getFirstAudio() {
        //如果第一个语音目录为空，遍历查找第一个语音目录
        if (m.firstAudio == null) {
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表

            for (int i = 0; i < catalogs.size(); i++) {
                IBookCatalog catalog = catalogs.get(i); //目录

                //查找到第一个语音时退出遍历
                if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                    m.firstAudio = catalog;
                    break;
                }
            }
        }

        return m.firstAudio;
    }

    /**
     * 获取最后一个语音目录
     * @return 最后一个语音目录
     */
    private IBookCatalog getLastAudio() {
        //如果最后一个语音目录为空，遍历查找最后一个语音目录
        if (m.firstAudio == null) {
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表

            for (int i = catalogs.size() - 1; i > 0; i--) {
                IBookCatalog catalog = catalogs.get(i); //目录

                //查找到最后一个语音时退出遍历
                if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                    m.lastAudio = catalog;
                    break;
                }
            }
        }

        return m.lastAudio;
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
         * 书ID
         */
        int bookID;

        /**
         * 书名称
         */
        String bookName;

        /**
         * 书描述
         */
        String bookDepict;

        /**
         * 出版社ID
         */
        int publishID;

        /**
         * 出版社
         */
        String publish;

        /**
         * 年级ID
         */
        int gradeID;

        /**
         * 年级
         */
        String grade;

        /**
         * 科目ID
         */
        int subjectID;

        /**
         * 科目
         */
        String subject;

        /**
         * 科目ID
         */
        int termID;

        /**
         * 学期
         */
        String term;

        /**
         * 内容类型
         */
        ContentType contentType;

        /**
         * 声音类型
         */
        SoundType soundType;

        /**
         * 全屏模式
         */
        FullMode fullMode;

        /**
         * 标题链接模式
         */
        LinkMode titleLinkMode;

        /**
         * 图标链接模式
         */
        LinkMode iconLinkMode;

        /**
         * 是否充许同步
         */
        boolean allowSync;

        /**
         * 同步开关
         */
        boolean syncEnable;

        /**
         * 当前语音目录索引
         */
        int currentAudioIndex;

        /**
         * 第一个语音目录
         */
        IBookCatalog firstAudio;

        /**
         * 当前语音目录
         */
        IBookCatalog currentAudio;

        /**
         * 最后一个语音目录
         */
        IBookCatalog lastAudio;

        /**
         * 当前显示页码
         */
        int currentPageNumber;

        /**
         * 当前显示页
         */
        IBookPage currentPage;

        /**
         * 封面图片文件名
         */
        String coverFilename;

        /**
         * 封面Drawable
         */
        Drawable coverDrawable;

        /**
         * 图标Drawable
         */
        Drawable iconDrawable;

        /**
         * 是否收藏
         */
        boolean isFavorite;

        /**
         * 包名
         */
        String packageName;

        /**
         * 目录集合
         */
        List<IBookCatalog> catalogs;

        /**
         * 页集合
         */
        List<IBookPage> pages;
    }
}
