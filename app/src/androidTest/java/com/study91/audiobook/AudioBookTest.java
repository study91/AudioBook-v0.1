package com.study91.audiobook;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.system.IConfig;
import com.study91.audiobook.system.IUser;
import com.study91.audiobook.system.SystemManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * 测试选项
 */
@RunWith(AndroidJUnit4.class)
public class AudioBookTest {
    private final String TAG = "AudioBookTest";

    @Test
    public void test() {
        Log.d(TAG, "********** 系统配置 **********");
        IConfig config = SystemManager.getConfig(getContext());
        config.init();
        testConfig(config);

        Log.d(TAG, "\n********** 用户信息 **********");
        IUser user = SystemManager.getUser(getContext());
        testUser(user);

        Log.d(TAG, "\n********** 当前打开的书 **********");
        user.setCurrentBook(5);
        IBook book = user.getCurrentBook();
        testBook(book);

        Log.d(TAG, "\n********** 当前语音目录 **********");
//        book.setCurrentAudio(11);
        IBookCatalog currentAudio = book.getCurrentAudio();
        testCatalog(currentAudio);

        Log.d(TAG, "\n********** 当前显示页 **********");
//        book.setCurrentPage(30);
        IBookPage currentPage = book.getCurrentPage();
        testPage(currentPage);

        Log.d(TAG, "\n********** 所有目录 **********");
        List<IBookCatalog> catalogs = book.getCatalogs();
        testCatalos(catalogs);

        Log.d(TAG, "\n********** 所有页 **********");
        List<IBookPage> pages = book.getPages();
        testPages(pages);
    }

    private void testConfig(IConfig config) {
        Log.d(TAG, "书数据源=" + config.getBookDataSource());
        Log.d(TAG, "用户数据源=" + config.getUserDataSource());
    }

    private void testUser(IUser user) {
        Log.d(TAG, "用户ID=" + user.getUserID());
        Log.d(TAG, "用户名=" + user.getUserName());
        Log.d(TAG, "是否测试用户=" + user.isTest());
        Log.d(TAG, "语音音量=" + (int)(user.getAudioVolume() * 100) + "%");
        Log.d(TAG, "音乐音量=" + (int)(user.getMusicVolume() * 100) + "%");
        Log.d(TAG, "语音循环模式=" + user.getAudioLoopMode().getString(getContext()));
        Log.d(TAG, "音乐循环模式=" + user.getMusicLoopMode().getString(getContext()));
    }

    private void testBook(IBook book) {
        Log.d(TAG, "书ID=" + book.getBookID());
        Log.d(TAG, "书名称=" + book.getBookName());
        Log.d(TAG, "书描述=" + book.getBookDepict());
        Log.d(TAG, "出版社=" + book.getPublish());
        Log.d(TAG, "年级=" + book.getGrade());
        Log.d(TAG, "学科=" + book.getSubject());
        Log.d(TAG, "学期=" + book.getTerm());
        Log.d(TAG, "内容类型=" + book.getContentType().getString(getContext()));
        Log.d(TAG, "声音类型=" + book.getSoundType().getString(getContext()));
        Log.d(TAG, "全屏模式=" + book.getFullMode().getString(getContext()));
        Log.d(TAG, "标题链接模式=" + book.getTitleLinkMode().getString(getContext()));
        Log.d(TAG, "图标链接模式=" + book.getIconLinkMode().getString(getContext()));
        Log.d(TAG, "是否充许同步=" + book.allowSync());
        Log.d(TAG, "同步开关=" + book.syncEnable());
        Log.d(TAG, "封面图片文件名=" + book.getCoverFilename());
        Log.d(TAG, "是否收藏=" + book.isFavorite());
        Log.d(TAG, "包名=" + book.getPackageName());
        Log.d(TAG, "语音文件路径=" + book.getAudioPath());
        Log.d(TAG, "图片文件路径=" + book.getImagePath());
        Log.d(TAG, "图标文件路径=" + book.getIconPath());
    }

    private void testCatalog(IBookCatalog catalog) {
        Log.d(TAG, "索引=" + catalog.getIndex());
        Log.d(TAG, "页码=" + catalog.getPageNumber());
        Log.d(TAG, "标题=" + catalog.getTitle());
        Log.d(TAG, "是否有语音=" + catalog.hasAudio());
        Log.d(TAG, "是否充许播放语音=" + catalog.allowPlayAudio());
        Log.d(TAG, "语音文件名=" + catalog.getAudioFilename());
        Log.d(TAG, "语音开始时间=" + catalog.getAudioStartTime());
        Log.d(TAG, "语音结束时间=" + catalog.getAudioEndTime());
        Log.d(TAG, "图标显示模式=" + catalog.getIconDisplayMode().getString(getContext()));
        Log.d(TAG, "图标文件名=" + catalog.getIconFilename());
        Log.d(TAG, "熟悉级别=" + catalog.getFamiliarLevel().getString(getContext()));
    }

    private void testPage(IBookPage page) {
        Log.d(TAG, "页码=" + page.getPageNumber());
        Log.d(TAG, "图片文件名=" + page.getImageFilename());
        Log.d(TAG, "图标文件名=" + page.getIconFilename());
        Log.d(TAG, "是否有语音=" + page.hasAudio());
        Log.d(TAG, "语音文件名=" + page.getAudioFilename());
        Log.d(TAG, "语音开始时间=" + page.getAudioStartTime());
    }

    private void testCatalos(List<IBookCatalog> catalogs) {
        for (IBookCatalog catalog : catalogs) {
            Log.d(TAG, catalog.getPageNumber() + "." + catalog.getTitle());
        }
    }

    private void testPages(List<IBookPage> pages) {
        for (IBookPage page : pages) {
            Log.d(TAG, page.getPageNumber() + "." + page.getImageFilename());
        }
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
