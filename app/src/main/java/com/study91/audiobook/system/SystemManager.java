package com.study91.audiobook.system;

import android.content.Context;

/**
 * 系统管理器
 */
public class SystemManager {
    private static IConfig1 systemConfig; //系统配置

    private static IConfig config; //全局配置
    private static IUser user; //全局用户

    /**
     * 获取全局配置
     * @param context 应用程序上下文
     * @return 全局配置
     */
    public static IConfig getConfig(Context context) {
        if (config == null) {
            config = new Config(context);
        }

        return config;
    }

    /**
     * 获取全局用户
     * @param context 应用程序上下文
     * @return 全局用户
     */
    public static IUser getUser(Context context) {
        if (user == null) {
            IConfig config = getConfig(context);
            user = new User(context, config.getUserID());
        }

        return user;
    }

    /**
     * 获取系统配置
     * @param context 应用程序上下文
     * @return 系统配置
     */
    public static IConfig1 getSystemConfig(Context context) {
        if (systemConfig == null) {
            systemConfig = new Config1(context);
        }

        return systemConfig;
    }

    /**
     * 创建权限
     * @return 权限
     */
    public static IPermission createPermission() {
        return new Permission();
    }
}
