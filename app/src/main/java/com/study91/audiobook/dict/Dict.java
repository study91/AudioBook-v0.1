package com.study91.audiobook.dict;

import android.content.Context;
import android.database.Cursor;

import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.system.IConfig;
import com.study91.audiobook.system.SystemManager;

/**
 * 字典
 */
class Dict implements IDict {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public Dict(Context context) {
        m.context = context;
    }

    @Override
    public String getValue(String type, int id) {
        String result = "";

        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IConfig config = SystemManager.getConfig(getContext()); //获取系统配置
            data = DataManager.createData(config.getBookDataSource()); //数据对象

            //查询字符串
            String sql = "SELECT * FROM [Dict] " +
                    "WHERE [DictType] = '" + type + "' AND [DictID] = " + id;

            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndex("DictValue")); //字典值
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }

        return result;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;
    }
}
