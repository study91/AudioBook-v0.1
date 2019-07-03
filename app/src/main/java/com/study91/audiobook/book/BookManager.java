package com.study91.audiobook.book;

import android.content.Context;

/**
 * 有声书管理器
 */
public class BookManager {
    /**
     * 获取书
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @return 书
     */
    public static IBook getBook(Context context, int bookID) {
        return new Book(context, bookID);
    }

    /**
     * 获取目录
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @param index 目录索引
     * @return 目录
     */
    public static IBookCatalog getCatalog(Context context, int bookID, int index) {
        return new BookCatalog(context, bookID, index);
    }

    /**
     * 获取页
     * @param context 应用程序上下文
     * @param bookID 书ID
     * @param pageNumber 页码
     * @return 页
     */
    public static IBookPage getPage(Context context, int bookID, int pageNumber) {
        return new BookPage(context, bookID, pageNumber);
    }
}
