package com.study91.audiobook.book.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.system.IUser;
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
        setAdapter(new BookImageViewPagerAdapter()); //设置适配器

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 获取当前打开的书
     * @return 当前打开的书
     */
    private IBook getBook() {
        IUser user = SystemManager.getUser(getContext()); //获取全局用户
        return user.getCurrentBook();
    }

    /**
     * 图片视图页适配器
     */
    private class BookImageViewPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            IBookPage page = getBook().getPages().get(position); //获取当前显示页

            ZoomImageView imageView = new ZoomImageView(getContext());
            imageView.setImageDrawable(page.getImageDrawable());
            imageView.setOnSingleTapListener(new OnSingleTapListener() {
                @Override
                public void onSingleTap() {
                    if (getOnSingleTapListener() != null) {
                        getOnSingleTapListener().onSingleTap();
                    }
                }
            });
            
            container.addView(imageView);

            return imageView;
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
     * 私有字段类
     */
    private class Field {
        /**
         * 单击事件监听器
         */
        OnSingleTapListener onSingleTapListener;
    }
}
