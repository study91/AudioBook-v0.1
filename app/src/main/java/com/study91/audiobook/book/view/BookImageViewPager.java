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
        if (listener != null) {
            listener.onSingleTap();
        }
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
//                IBook book = BookManager.getCurrentBook(getContext());
//                IBookPage page = book.getPages().get(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //setCurrentItem(0);
    }

    private IBook getBook() {
        IUser user = SystemManager.getUser(getContext()); //获取全局用户
        IBook book = user.getCurrentBook(); //获取用户当前打开的书
        return book;
    }

    /**
     * 图片视图页适配器
     */
    private class BookImageViewPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            IBookPage page = getBook().getPages().get(position); //获取当前显示页

//            ImageView imageView = new ImageView(getContext());
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setImageDrawable(page.getImageDrawable());
//            container.addView(imageView);
            ZoomImageView imageView = new ZoomImageView(getContext());
            imageView.setImageDrawable(page.getImageDrawable());
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
}
