package com.fmtch.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Glide 加载 简单判空封装 防止异步加载数据时调用Glide 抛出异常
 * Created by Li_Xavier on 2017/6/20 0020.
 */
public class GlideLoadUtils {
    private String TAG = "ImageLoader";

    /**
     * 借助内部类 实现线程安全的单例模式
     * 属于懒汉式单例，因为Java机制规定，内部类SingletonHolder只有在getInstance()
     * 方法第一次调用的时候才会被加载（实现了lazy），而且其加载过程是线程安全的。
     * 内部类加载的时候实例化一次instance。
     */
    public GlideLoadUtils() {
    }

    private static class GlideLoadUtilsHolder {
        private final static GlideLoadUtils INSTANCE = new GlideLoadUtils();
    }

    public static GlideLoadUtils getInstance() {
        return GlideLoadUtilsHolder.INSTANCE;
    }

    /**
     * Glide 加载 简单判空封装 防止异步加载数据时调用Glide 抛出异常
     *
     * @param context
     * @param url           加载图片的url地址  String
     * @param imageView     加载图片的ImageView 控件
     * @param default_image 图片展示错误的本地图片 id
     */
    public void glideLoad(Context context, String url, ImageView imageView, int default_image) {
        if (context != null) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .error(default_image)
                    .dontAnimate()
                    .into(imageView);
        } else {
            Log.e(TAG, "Picture loading failed,context is null");
        }
    }

    public void glideLoad(Activity activity, String url, ImageView imageView, BitmapTransformation transformation, int default_image) {
        if (activity != null && !activity.isDestroyed()) {
            DrawableRequestBuilder<String> builder = Glide.with(activity)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .dontAnimate();
            if (default_image != -1) {
                builder.error(default_image)
                        .placeholder(default_image);
            }
            if (transformation != null) {
                builder.transform(transformation);
            }
            builder.into(imageView);
        } else {
            Log.e(TAG, "Picture loading failed,activity is Destroyed");
        }
    }

    public void glideLoad(Activity activity, String url, ImageView imageView, int default_image) {
        glideLoad(activity, url, imageView, null, default_image);
    }

    public void glideLoad(Activity activity, String url, ImageView imageView) {
        glideLoad(activity, url, imageView, null, -1);
    }

    public void glideLoad(Fragment fragment, String url, ImageView imageView, int default_image) {
        if (fragment != null && fragment.getActivity() != null) {
            Glide.with(fragment)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(default_image)
                    .centerCrop()
                    .error(default_image)
                    .dontAnimate()
                    .into(imageView);
        } else {
            Log.e(TAG, "Picture loading failed,fragment is null");
        }
    }

    public void glideLoad(android.app.Fragment fragment, String url, ImageView imageView, int default_image) {
        if (fragment != null && fragment.getActivity() != null) {
            Glide.with(fragment)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(default_image)
                    .centerCrop()
                    .error(default_image)
                    .dontAnimate()
                    .into(imageView);
        } else {
            Log.e(TAG, "Picture loading failed,android.app.Fragment is null");
        }
    }
}
