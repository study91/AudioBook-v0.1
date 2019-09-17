package com.study91.audiobook.book.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.media.MediaClient;
import com.study91.audiobook.system.SystemManager;

/**
 * 书图片视图页
 */
public class BookImageViewPager extends ViewPager {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public BookImageViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public BookImageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        getMediaClient().unregister(); //注销媒体客户端
        super.onDetachedFromWindow();
    }

    /**
     * 设置单击事件监听器
     * @param listener 单击事件监听器
     */
    public void setOnSingleTapListener(OnSingleTapListener listener) {
        m.onSingleTapListener = listener;
    }

    /**
     * 获取单击事件监听器
     * @return 单击事件监听器
     */
    private OnSingleTapListener getOnSingleTapListener() {
        return m.onSingleTapListener;
    }

    /**
     * 初始化
     */
    private void init() {
        getMediaClient().register(); //注册媒体客户端
//        getMediaClient().setOnReceiver(new OnMediaClientBroadcastReceiver()); //设置媒体客户端广播接收器
        setAdapter(new BookImageViewPagerAdapter()); //设置适配器
        setCurrentItem(getBook().getCurrentPage().getPosition()); //设置当前显示页
        addOnPageChangeListener(new OnBookPageChangeListener()); //添加页改变事件监听器
    }

    /**
     * 获取当前打开的书
     * @return 当前打开的书
     */
    private IBook getBook() {
        return SystemManager.getUser(getContext()).getCurrentBook();
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 图片视图页适配器
     */
    private class BookImageViewPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            IBookPage page = getBook().getPages().get(position); //获取当前显示页

            ZoomImageView zoomImageView = new ZoomImageView(getContext()); //实例化图片缩放控件
            zoomImageView.setImageDrawable(page.getImageDrawable()); //设置图片Drawable

            //设置单击事件监听器
            zoomImageView.setOnSingleTapListener(new OnSingleTapListener() {
                @Override
                public void onSingleTap() {
                    if (getOnSingleTapListener() != null) {
                        getOnSingleTapListener().onSingleTap();
                    }
                }
            });

            container.addView(zoomImageView);

            return zoomImageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return getBook().getPages().size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    /**
     * 页改变事件监听器
     */
    private class OnBookPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            IBookPage currentPage = getBook().getPages().get(position); //获取当前显示页

            //如果页码有变化，重置当前页
            if (getBook().getCurrentPage().getPageNumber() != currentPage.getPageNumber()) {
                getBook().setCurrentPage(currentPage);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * TODO 媒体客户端广播接收器可以删除
     */
    private class OnMediaClientBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentPageNumber = getBook().getCurrentPage().getPageNumber(); //获取当前页码

            //如果页码有变化，重置当前显示页
            if (currentPageNumber != m.currentPageNumber) {
                m.currentPageNumber = currentPageNumber;
                setCurrentItem(getBook().getCurrentPage().getPosition());
            }
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 当前页码
         */
        int currentPageNumber;

        /**
         * 单击事件监听器
         */
        OnSingleTapListener onSingleTapListener;

        /**
         * 媒体客户端
         */
        MediaClient mediaClient;
    }
}
