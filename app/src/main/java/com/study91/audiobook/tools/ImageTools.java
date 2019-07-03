package com.study91.audiobook.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类
 */
public class ImageTools {
    /**
     * 获取位图
     * @param bitmap 位图
     * @param width 宽度
     * @param height 高度
     * @return 位图
     */
    public static Bitmap getBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        //Log.e("ImageTools", "图片宽度" + w + ",screenWidth=" + width);

        Matrix matrix = new Matrix();
        float scaleWidth = (float) width / w;
        float scaleHeight = (float) height / h;

        // scale = scale < scale2 ? scale : scale2;

        //保证图片不变形 matrix.postScale(scaleWidth, scaleWidth);
        //图片拉伸 matrix.postScale(scaleWidth, scaleHeight);
        matrix.postScale(scaleWidth, scaleHeight);

        // w,h是原图的属性.
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 获取图片Drawable
     * @param context 应用程序上下文
     * @param filename 文件名（注：带完整路径的文件名）
     * @return 图片Drawable
     */
    public static Drawable getDrawable(Context context, String filename) {
        Drawable drawable = null;

        if (filename != null) {
            //TODO 需要校验文件是否存在
            InputStream inputStream = getInputStream(context, filename);
            drawable = Drawable.createFromStream(inputStream, null);
        }

        return drawable;
    }

    /**
     * 获取输入流
     * @param context 应用程序上下文
     * @param filename 文件名（注：带完整路径的文件名）
     * @return 输入流
     */
    private static InputStream getInputStream(Context context, String filename) {
        InputStream inputStream = null;

        if (filename != null) {
            //TODO 需要校验文件是否存在
            try {
                inputStream = context.getAssets().open(filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return inputStream;
    }
}
