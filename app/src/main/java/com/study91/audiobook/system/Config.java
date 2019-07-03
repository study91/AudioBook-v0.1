package com.study91.audiobook.system;

import android.content.Context;

import com.study91.audiobook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 全局配置（单例模式）
 */
class Config implements IConfig {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public Config(Context context) {
        m.context = context;
    }

    @Override
    public void init() {
        checkDatabase(getAssertUserDataSource(), getUserDataSource()); //检查用户数据源
        checkDatabase(getAssertBookDataSource(), getBookDataSource()); //检查书数据源
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
    public String getUserDataSource() {
        if (m.userDataSource == null) {
            String filename = getFilename(getAssertUserDataSource());
            m.userDataSource = getContext().getDatabasePath(filename).getAbsolutePath();
        }

        return m.userDataSource;
    }

    @Override
    public int getUserID() {
        return Integer.parseInt(getValue("UserID"));
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
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
     * 获取Assert资源用户数据源
     * @return Assert资源用户数据源
     */
    private String getAssertUserDataSource() {
        if (m.assertUserDataSource == null) {
            m.assertUserDataSource = getValue("UserDataSource");
        }

        return m.assertUserDataSource;
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
         * 用户数据源
         */
        String userDataSource;

        /**
         * 用户ID
         */
        int userID;

        /**
         * 用户
         */
        IUser user;

        /**
         * Assert资源配置文件名
         */
        String assertConfigFilename;

        /**
         * Assert资源有声书数据源
         */
        String assertBookDataSource;

        /**
         * Assert资源用户数据源
         */
        String assertUserDataSource;
    }
}
