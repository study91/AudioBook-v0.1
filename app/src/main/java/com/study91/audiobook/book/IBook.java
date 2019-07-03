package com.study91.audiobook.book;

import android.graphics.drawable.Drawable;

import com.study91.audiobook.dict.ContentType;
import com.study91.audiobook.dict.FullMode;
import com.study91.audiobook.dict.LinkMode;
import com.study91.audiobook.dict.SoundType;

import java.util.List;

/**
 * 书接口
 */
public interface IBook {
    /**
     * 获取书ID
     * @return 书ID
     */
    int getBookID();

    /**
     * 获取书名称
     * @return 书名称
     */
    String getBookName();

    /**
     * 获取书描述
     * @return 书描述
     */
    String getBookDepict();

    /**
     * 获取出版社
     * @return 出版社
     */
    String getPublish();

    /**
     * 获取年级
     * @return 年级
     */
    String getGrade();

    /**
     * 获取学科
     * @return 学科
     */
    String getSubject();

    /**
     * 获取学期
     * @return 学期
     */
    String getTerm();

    /**
     * 获取内容类型
     * @return 内容类型
     */
    ContentType getContentType();

    /**
     * 获取声音类型
     * @return 声音类型
     */
    SoundType getSoundType();

    /**
     * 获取全屏模式
     * @return 全屏模式
     */
    FullMode getFullMode();

    /**
     * 获取标题链接模式
     * @return 标题链接模式
     */
    LinkMode getTitleLinkMode();

    /**
     * 获取图标链接模式
     * @return 图标链接模式
     */
    LinkMode getIconLinkMode();

    /**
     * 是否充许同步
     * @return true=充许同步 false=不能同步
     */
    boolean allowSync();

    /**
     * 同步开关
     * @return true=打开同步 false=关闭同步
     */
    boolean syncEnable();

    /**
     * 设置当前语音目录
     * @param index 语音目录索引
     */
    void setCurrentAudio(int index);

    /**
     * 获取当前语音目录
     * @return 当前语音目录
     */
    IBookCatalog getCurrentAudio();

    /**
     * 设置当前显示页
     * @param pageNumber 页码
     */
    void setCurrentPage(int pageNumber);

    /**
     * 获取当前显示页
     * @return 当前显示页
     */
    IBookPage getCurrentPage();

    /**
     * 获取封面文件名
     * @return 封面文件名
     */
    String getCoverFilename();

    /**
     * 获取封面Drawable
     * @return 封面Drawable
     */
    Drawable getCoverDrawable();

    /**
     * 获取图标文件名
     * @return 图标文件名
     */
    String getIconFilename();

    /**
     * 获取图标Drawable
     * @return 图标Drawable
     */
    Drawable getIconDrawable();

    /**
     * 是否收藏
     * @return true=收藏，false=未收藏
     */
    boolean isFavorite();

    /**
     * 获取包名
     * @return 包名
     */
    String getPackageName();

    /**
     * 获取语音文件路径
     * @return 语音文件路径
     */
    String getAudioPath();

    /**
     * 获取图片文件路径
     * @return 图片文件路径
     */
    String getImagePath();

    /**
     * 获取图标文件路径
     * @return 图标文件路径
     */
    String getIconPath();

    /**
     * 获取目录集合
     * @return 目录集合
     */
    List<IBookCatalog> getCatalogs();

    /**
     * 获取页集合
     * @return 页集合
     */
    List<IBookPage> getPages();
}
