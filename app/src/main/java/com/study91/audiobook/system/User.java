package com.study91.audiobook.system;

import android.content.Context;
import android.database.Cursor;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.dict.LoopMode;

/**
 * 用户
 */
class User implements IUser {
    private Field m = new Field(); //私有字段

    /**
     * 应用程序上下文
     * @param context 应用程序上下文
     * @param userID 用户ID
     */
    User(Context context, int userID) {
        m.context = context;
        load(userID); //载入
    }

    @Override
    public int getUserID() {
        return m.userID;
    }

    @Override
    public String getUserName() {
        return m.userName;
    }

    @Override
    public boolean isTest() {
        return m.isTest;
    }

    @Override
    public void setCurrentBook(int bookID) {
        if (bookID != m.currentBookID) {
            m.currentBookID = bookID;
            update();
        }
    }

    @Override
    public IBook getCurrentBook() {
        //当前打开的书为null或缓存的书ID（m.currentBookID）与当前打开的书ID不同时执行
        if (m.currentBook == null || m.currentBookID != m.currentBook.getBookID()) {
            IData data = null; //数据对象
            Cursor cursor = null; //数据指针

            try {
                IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
                data = DataManager.createData(config.getBookDataSource()); //创建数据对象
                String sql = "SELECT [BookID] FROM [Book] WHERE [BookID] = " + m.currentBookID; //查询字符串
                cursor = data.query(sql);

                //如果书ID不存在，将第一个本书的ID作为默认的当前打开的书ID
                if (cursor.getCount() == 0) {
                    sql = "SELECT [BookID] FROM [Book] ORDER BY [BookID] LIMIT 1";
                    cursor = data.query(sql);

                    if (cursor.getCount() == 1) {
                        cursor.moveToFirst();
                        int bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
                        setCurrentBook(bookID); //重新设置当前打开的书
                    }
                }

                m.currentBook = BookManager.getBook(getContext(), m.currentBookID);
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.currentBook;
    }

    @Override
    public void setAudioVolume(float volume) {
        m.audioVolume = volume;
    }

    @Override
    public float getAudioVolume() {
        return m.audioVolume;
    }

    @Override
    public void setMusicVolume(float volume) {
        m.musicVolume = volume;
    }

    @Override
    public float getMusicVolume() {
        return m.musicVolume;
    }

    @Override
    public LoopMode getAudioLoopMode() {
        return m.audioLoopMode;
    }

    @Override
    public LoopMode getMusicLoopMode() {
        return m.musicLoopMode;
    }

    @Override
    public void update() {
        IData data = null;

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
            data = DataManager.createData(config.getUserDataSource()); //获取数据对象

            //更新字符串
            String sql = "UPDATE [User] " +
                    "SET " +
                    "[CurrentBook] = " + getCurrentBookID() + "," +
                    "[AudioVolume] = " + getAudioVolume() + "," +
                    "[MusicVolume] = " + getMusicVolume() + " " +
                    "WHERE " +
                    "[UserID] = " + getUserID();

            data.execute(sql); //执行更新
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取当前打开的书ID
     * @return 当前打开的书ID
     */
    private int getCurrentBookID() {
        return m.currentBookID;
    }

    /**
     * 载入
     * @param userID 用户ID
     */
    private void load(int userID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取全局配置

            data = DataManager.createData(config.getUserDataSource()); //创建数据对象
            String sql = "SELECT * FROM [User] WHERE [UserID] = " + userID; //查询字符串
            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.userID = userID;
                m.userName = cursor.getString(cursor.getColumnIndex("UserName")); //用户名
                m.isTest = cursor.getInt(cursor.getColumnIndex("IsTest")) != 0; //是否测试用户
                m.currentBookID = cursor.getInt(cursor.getColumnIndex("CurrentBook")); //当前打开的书ID
                m.audioVolume = cursor.getFloat(cursor.getColumnIndex("AudioVolume")); //语音音量
                m.musicVolume = cursor.getFloat(cursor.getColumnIndex("MusicVolume")); //背景音乐音量

                //语音循环模式
                int audioLoopMode = cursor.getInt(cursor.getColumnIndex("AudioLoopMode"));
                m.audioLoopMode = LoopMode.values()[audioLoopMode];

                //音乐循环模式
                int musicLoopMode = cursor.getInt(cursor.getColumnIndex("MusicLoopMode"));
                m.musicLoopMode = LoopMode.values()[musicLoopMode];
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
         * 用户ID
         */
        int userID;

        /**
         * 用户名
         */
        String userName;

        /**
         * 是否测试用户
         */
        boolean isTest;

        /**
         * 当前打开的书ID
         */
        int currentBookID;

        /**
         * 当前打开的书
         */
        IBook currentBook;

        /**
         * 语音音量
         */
        float audioVolume;

        /**
         * 音乐音量
         */
        float musicVolume;

        /**
         * 语音循环模式
         */
        LoopMode audioLoopMode;

        /**
         * 音乐循环模式
         */
        LoopMode musicLoopMode;
    }
}
