package com.study91.audiobook.book;

import android.content.Context;

/**
 * 有声书管理器
 */
public class BookManager {
    /**
     * 创建书
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @return 书
     */
    public static IBook createBook(Context context, int bookID) {
        return new Book(context, bookID);
    }

    /**
     * 创建目录
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @param index 目录索引
     * @return 目录
     */
    public static IBookCatalog createCatalog(Context context, int bookID, int index) {
        return new BookCatalog(context, bookID, index);
    }

    /**
     * 创建页
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @param pageNumber 页码
     * @return 页
     */
    public static IBookPage createPage(Context context, int bookID, int pageNumber) {
        return new BookPage(context, bookID, pageNumber);
    }
}
