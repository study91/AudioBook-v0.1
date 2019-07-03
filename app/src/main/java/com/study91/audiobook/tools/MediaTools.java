package com.study91.audiobook.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 媒体工具类
 */
public class MediaTools {
    /**
     * 解释为时间字符串
     * @param time 时间
     * @return 时间字符串
     */
    public static String parseTime(long time) {
        //初始化Formatter的转换格式。
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
        return dateFormat.format(time);
    }

    /**
     * 解析时间为长整型
     * @param time 时间字符串
     * @return 时间
     */
    public static long parseTime(String time) {
        long result = -1;

        if (time != null && time.trim().length() > 0) {
            String timeString = time.replace(".", ":"); //将时间字符串中的“.”替换成“:”，以便于进行字符串分隔
            String[] timeArray = timeString.split(":"); //用字符“:”将时间字符串分隔为时间数组

            if (timeArray.length == 3) {
                long min = Long.parseLong(timeArray[0]); //提取分钟值
                long sec = Long.parseLong(timeArray[1]); //提取秒值
                long ms = Long.parseLong(timeArray[2]); //提取毫秒值

                // 时间格式：00:17.04的计算方式
                if (timeArray[2].length() == 2) {
                    result = (min * 60 + sec) * 1000 + ms * 10;
                }

                // 时间格式：00:04.415的计算方式
                if (timeArray[2].length() == 3) {
                    result = (min * 60 + sec) * 1000 + ms;
                }
            }
        }

        return result;
    }
}
