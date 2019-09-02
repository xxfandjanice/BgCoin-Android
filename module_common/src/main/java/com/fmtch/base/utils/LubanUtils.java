package com.fmtch.base.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by wtc on 2018/9/19
 */

public class LubanUtils {

    public static void compress(Context context, @Nonnull String path, OnCompressListener listener) {
        String desDirPath = context.getExternalCacheDir().getPath() + File.separator + "image";
        File desDir = new File(desDirPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        Luban.with(context)
                .load(path)
                .ignoreBy(100)
                .setTargetDir(desDir.getAbsolutePath())   //压缩后图片存放路径
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif") || path.toLowerCase().endsWith(".mp4"));
                    }
                })
//                .setRenameListener()   //重命名
                .setCompressListener(listener)
                .launch();
    }

    public static void compress(Context context, @Nonnull List<String> paths, OnCompressListener listener) {
        String desDirPath = context.getExternalCacheDir().getPath() + File.separator + "image";
        File desDir = new File(desDirPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        Luban.with(context)
                .load(paths)
                .ignoreBy(100)
                .setTargetDir(desDir.getAbsolutePath())   //压缩后图片存放路径
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif") || path.toLowerCase().endsWith(".mp4"));
                    }
                })
//                .setRenameListener()   //重命名
                .setCompressListener(listener)
                .launch();
    }
}
