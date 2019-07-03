package com.study91.audiobook.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据
 */
class SQLiteData implements IData {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param filename 数据库文件名
     */
    public SQLiteData(String filename) {
        m.filename = filename; //数据库文件名
    }

    @Override
    public Cursor query(String sql) {
        return getSqliteDatabase().rawQuery(sql, null);
    }

    @Override
    public void execute(String sql) {
        getSqliteDatabase().execSQL(sql);
    }

    @Override
    public void close() {
        //如果数据库不等于null，关闭数据库
        if(m.sqliteDatabase != null) {
            m.sqliteDatabase.close();
        }
    }

    /**
     * 获取数据库文件名
     * @return 数据库文件名
     */
    private String getDatabaseFilename() {
        return m.filename;
    }

    /**
     * 获取SQlite数据库
     * @return SQlite数据库
     */
    private SQLiteDatabase getSqliteDatabase() {
        if (m.sqliteDatabase == null) {
            //打开数据库
            m.sqliteDatabase = SQLiteDatabase.openDatabase(
                    getDatabaseFilename(),
                    null,
                    SQLiteDatabase.OPEN_READWRITE);
        }

        return m.sqliteDatabase;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 数据库文件名
         */
        private String filename;

        /**
         * SQLite数据库
         */
        private SQLiteDatabase sqliteDatabase;
    }
}
