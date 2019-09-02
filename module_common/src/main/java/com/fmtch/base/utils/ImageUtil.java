package com.fmtch.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

public class ImageUtil {
    /**
     * Bitmap保存成File
     *
     * @param bitmap input bitmap
     * @param name   output file's name
     * @return String output file's path
     */
    public static String bitmap2File(Context context, Bitmap bitmap, String name) {
        return bitmap2File(context, bitmap, name, 100);
    }

    public static String bitmap2File(Context context, Bitmap bitmap, String name, int quality) {
        String dir = context.getExternalCacheDir().getAbsolutePath() + "/image/";
        String path = context.getExternalCacheDir().getAbsolutePath() + "/image/" + name + ".jpg";

        FileOutputStream fOut;
        try {
            File file = new File(dir);

            if (!file.exists()) {
                file.mkdir();
            }
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            return null;
        }
        return path;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap toTurn(String path) {
        Matrix matrix = new Matrix();
        matrix.postRotate(readPictureDegree(path));
        Bitmap img = BitmapFactory.decodeFile(path);
        int width = (int) (img.getWidth());
        int height = (int) (img.getHeight());
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    /**
     * 旋转图片
     *
     * @param path
     * @param rotate
     * @return
     */
    public static Bitmap toTurn(String path, int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap img = BitmapFactory.decodeFile(path);
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    /**
     * 将图片摆正(旋转角度0)
     *
     * @param path
     * @return
     */
    public static String rotatePic(Context context, @Nonnull String path) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        int rotate = readPictureDegree(path);
        if (rotate != 0) {
            bitmap = toTurn(path, rotate);
        }
        long l = System.currentTimeMillis();
        if (bitmap != null) {
            return bitmap2File(context, bitmap, l + "");
        }
        return path;
    }

    /**
     * 按照给定的宽，进行等比例压缩
     *
     * @param bitmap
     * @param scaledWidth
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int scaledWidth) {
        if (null == bitmap || scaledWidth <= 0) {
            return null;
        }

        Bitmap result;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        int scaledHeight = (int) (scaledWidth * 1.0f / widthOrg * heightOrg);
        try {
            result = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * 按照给定的宽，进行等比例压缩
     *
     * @param bitmap
     * @param scaledWith
     * @return
     */
    public static Bitmap scaleBitmapWithW(Bitmap bitmap, int scaledWith) {
        return scaleBitmap(bitmap, scaledWith);
    }

    /**
     * 按照给定的高，进行等比例压缩
     *
     * @param bitmap
     * @param scaledHeight
     * @return
     */
    public static Bitmap scaleBitmapWithH(Bitmap bitmap, int scaledHeight) {
        if (null == bitmap || scaledHeight <= 0) {
            return null;
        }

        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        int scaledWidth = (int) (scaledHeight * 1.0f / heightOrg * widthOrg);
        try {
            return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] decodeBitmap(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new FileInputStream(path);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
            double scale = getScaling(opts.outWidth * opts.outHeight,
                    1024 * 600);
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
                    (int) (opts.outWidth * scale),
                    (int) (opts.outHeight * scale), true);
            bmp.recycle();
            baos = new ByteArrayOutputStream();
            bmp2.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            bmp2.recycle();
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        return baos.toByteArray();
    }

    private static double getScaling(int src, int des) {
        /**
         * 48 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比 49
         */
        double scale = Math.sqrt((double) des / (double) src);
        return scale;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }


    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 图片旋转
     *
     * @param bit     旋转原图像
     * @param degrees 旋转度数
     * @return 旋转之后的图像
     */
    public static Bitmap rotateImage(Bitmap bit, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);
        return tempBitmap;
    }

    /**
     * 翻转图像
     *
     * @param bit 翻转原图像
     * @param x   翻转X轴
     * @param y   翻转Y轴
     * @return 翻转之后的图像
     * <p/>
     * 说明:
     * (1,-1)上下翻转
     * (-1,1)左右翻转
     */
    public static Bitmap reverseImage(Bitmap bit, int x, int y) {
        Matrix matrix = new Matrix();
        matrix.postScale(x, y);

        Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);
        return tempBitmap;
    }

    /**
     * 删除文件
     *
     * @param name
     */
    public static void deleteImage(Context context, String name) {
        String url = context.getExternalCacheDir().getAbsolutePath() + "/image/" + name + ".jpg";
        File file = new File(url);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public static void deleteFile(String url) {
        File file = new File(url);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public static boolean isGif(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String[] spilt = path.split("\\.");
        boolean gif = false;
        if (spilt.length > 0) {
            int lastIndex = spilt.length - 1;
            if (spilt[lastIndex].toUpperCase().equals("GIF")) {
                gif = true;
            }
        }
        return gif;
    }

    public static boolean isLargeImage(int width, int height) {
        if (height > 3 * width) {
            return true;
        }
        return false;
    }

    public static boolean isLargeImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outHeight > 3000 && options.outHeight > 3 * options.outWidth) {
            return true;
        }
        if (options.outWidth > 3000 && options.outWidth > 3 * options.outHeight) {
            return true;
        }
        return false;
    }

    public static String getFileNameNoEx(String filename) {
        if (filename == null) {
            return "";
        }
        File file = new File(filename);
        return file.getName().replaceAll("[.][^.]+$", "");
    }

    /**
     * 保存Bitmap为文件
     *
     * @param bitmap
     * @param imageURL
     */
    public static void saveBitmapToFile(Bitmap bitmap, String imageURL) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        File file = new File(imageURL);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap base64ToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String File2Base64(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public static String File2Base64(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new FileInputStream(path);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
//            double scale = getScaling(opts.outWidth * opts.outHeight,
//                    1024 * 600);
//            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
//                    (int) (opts.outWidth * scale),
//                    (int) (opts.outHeight * scale), true);
//            bmp.recycle();
            baos = new ByteArrayOutputStream();
            int options = 100;

            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);

            byte[] buffer = baos.toByteArray();
            //将字节换成KB
            int mid = buffer.length / 1024;
            while (mid > 40) {
                baos.reset();
                options -= setSubstractSize(mid);
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                buffer = baos.toByteArray();
                mid = buffer.length / 1024;
            }
            bmp.recycle();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

    /**
     * 根据图片的大小设置压缩的比例，提高速度
     *
     * @param imageMB
     * @return
     */
    private static int setSubstractSize(int imageMB) {

        if (imageMB > 1000) {
            return 60;
        } else if (imageMB > 750) {
            return 40;
        } else if (imageMB > 500) {
            return 20;
        } else {
            return 10;
        }

    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
