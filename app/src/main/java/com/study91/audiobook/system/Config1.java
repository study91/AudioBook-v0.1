package com.study91.audiobook.system;

import android.content.Context;
import android.database.Cursor;

import com.study91.audiobook.R;
import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 全局配置（单例模式）
 */
class Config1 implements IConfig1 {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public Config1(Context context) {
        m.context = context;
    }

    @Override
    public void init() {
        checkDatabase(); //检查数据库
        load(getOptionID()); //载入配置
    }

    @Override
    public int getOptionID() {
        if (m.optionID == -1) {
            m.optionID = Integer.parseInt(getValue("OptionID"));
        }

        return m.optionID;
    }

    @Override
    public String getOptionName() {
        return m.optionName;
    }

    @Override
    public String getBookDataSource() {
        if (m.bookDataSource == null) {
            String filename = getFilename(getAssertBookDataSource());
            m.bookDataSource = getContext().getDatabasePath(filename).getAbsolutePath();
        }

        return m.bookDataSource;
    }

    @Override
    public String getBookFilePath() {
        return getContext().getString(R.string.book_file_path) + getContext().getPackageName() + "/";
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public int getBookID() {
        return m.bookID;
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
    public LoopMode audioLoopMode() {
        return null;
    }

    @Override
    public LoopMode musicLoopMode() {
        return null;
    }

    @Override
    public void update() {
        IData data = null;

        try {
            data = DataManager.createData(getOptionDataSource()); //获取数据对象

            //更新字符串
            String sql = "UPDATE [Option] " +
                    "SET " +
                    "[BookID] = " + getBookID() + "," +
                    "[AudioVolume] = '" + getAudioVolume() + "'," +
                    "[MusicVolume] = '" + getMusicVolume() + "' " +
                    "WHERE " +
                    "[OptionID] = " + getOptionID();

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
     * 载入配置
     * @param optionID 选项ID
     */
    private void load(int optionID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            data = DataManager.createData(getOptionDataSource()); //创建数据对象
            String sql = "SELECT * FROM [Option] WHERE [OptionID] = " + optionID; //查询字符串
            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.optionName = cursor.getString(cursor.getColumnIndex("OptionName")); //配置名称
                m.audioVolume = cursor.getFloat(cursor.getColumnIndex("AudioVolume")); //语音音量
                m.musicVolume = cursor.getFloat(cursor.getColumnIndex("MusicVolume")); //背景音乐音量

                //检查书ID
                int bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
                checkBookID(bookID); //检查有声书ID
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 检查数据库
     */
    private void checkDatabase() {
        checkDatabase(getAssertOptionDataSource(), getOptionDataSource()); //检查配置数据源
        checkDatabase(getAssertBookDataSource(), getBookDataSource()); //检查有声书数据源
    }

    /**
     * 检查书ID的有效性（注：如果书ID无效，设置第一本书的ID为当前书ID）
     * @param bookID 书ID
     */
    private void checkBookID(int bookID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            data = DataManager.createData(getBookDataSource()); //创建数据对象
            String sql = "SELECT [BookID] FROM [Book] WHERE [BookID] = " + bookID; //查询字符串
            cursor = data.query(sql);

            //如果书ID不存在，将第一个本书的ID作为默认的当前正在阅读的书ID
            if (cursor.getCount() == 0) {
                sql = "SELECT [BookID] FROM [Book] ORDER BY [BookID] LIMIT 1";
                cursor = data.query(sql);

                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
                    update(); //更新
                }
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 检查数据源
     * @param source 源文件
     * @param target 目标文件
     */
    private void checkDatabase(String source, String target) {
        File file = new File(target); //实例化文件对象

        //如果目标文件不存在，复制Asserts资源文件到数据库目录中
        if (!file.exists()) {
            String path = file.getParent(); //获取路径字符串
            file = new File(path);

            //如果目录不存在，先创建目录
            if (!file.exists()) {
                file.mkdirs(); //创建目录
            }

            //复制数据库
            try {
                InputStream inputStream = getContext().getAssets().open(source); //输入流
                OutputStream outputStream = new FileOutputStream(target); //输出流

                byte[] buffer = new byte[8192];//定义缓冲区，缓冲区大小设置为8M
                int count;

                while((count = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, count);
                }

                inputStream.close(); //关闭输入流
                outputStream.close(); //关闭输出流
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取字符串值
     * @param key 键
     * @return 字符串
     */
    private String getValue(String key) {
        return getProperties().getProperty(key).trim();
    }

    /**
     * 获取属性
     * @return 属性
     */
    private Properties getProperties() {
        if (m.properties == null) {
            try {
                InputStream inputStream = getContext().getAssets().open(getAssertConfigFilename());
                m.properties = new Properties();
                m.properties.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return m.properties;
    }

    /**
     * 获取配置文件名
     * @return 配置文件名
     */
    private String getAssertConfigFilename() {
        if (m.assertConfigFilename == null) {
            m.assertConfigFilename = getContext().getResources().getString(R.string.config_filename);
        }

        return m.assertConfigFilename;
    }

    /**
     * 获取Assert资源有声书数据源
     * @return Assert资源有声书数据源
     */
    private String getAssertBookDataSource() {
        if (m.assertBookDataSource == null) {
            m.assertBookDataSource = getValue("BookDataSource");
        }

        return m.assertBookDataSource;
    }

    /**
     * 获取配置数据源
     * @return 配置数据源
     */
    private String getOptionDataSource() {
        if (m.optionDataSource == null) {
            String filename = getFilename(getAssertOptionDataSource());
            m.optionDataSource = getContext().getDatabasePath(filename).getAbsolutePath();
        }

        return m.optionDataSource;
    }

    /**
     * 获取Assert资源选项数据源
     * @return Assert资源选项数据源
     */
    private String getAssertOptionDataSource() {
        if (m.assertOptionDataSource == null) {
            m.assertOptionDataSource = getValue("OptionDataSource");
        }

        return m.assertOptionDataSource;
    }

    /**
     * 获取文件名
     * @param fullFilename 带路径的完整文件名
     * @return 文件名
     */
    private String getFilename(String fullFilename) {
        String filename = null;

        int start=fullFilename.lastIndexOf("/"); //获取最后一个"/"的位置
        int end=fullFilename.length(); //获取最后一个字符的位置
        if(start!=-1 && end!=-1){
            filename = fullFilename.substring(start+1,end); //提取文件名
        }

        return filename;
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
         * 属性
         */
        Properties properties;

        /**
         * 有声书数据源
         */
        String bookDataSource;

        /**
         * 选项数据源
         */
        String optionDataSource;

        /**
         * Assert资源配置文件名
         */
        String assertConfigFilename;

        /**
         * Assert资源有声书数据源
         */
        String assertBookDataSource;

        /**
         * Assert资源选项数据源
         */
        String assertOptionDataSource;

        /**
         * 配置ID
         */
        int optionID = -1;

        /**
         * 配置名称
         */
        String optionName;

        /**
         * 有声书ID
         */
        int bookID;

        /**
         * 语音音量
         */
        float audioVolume;

        /**
         * 背景音乐音量
         */
        float musicVolume;
    }
}
