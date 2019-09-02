package com.lzy.imagepicker.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 *
 * Bitmap�����࣬��Ҫ�ǽ��������ת������
 *
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-03-20  13:27
 */

public class BitmapUtil {

    private BitmapUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * ��ȡͼƬ����ת�Ƕ�
     *
     * @param path ͼƬ����·��
     * @return ͼƬ����ת�Ƕ�
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // ��ָ��·���¶�ȡͼƬ������ȡ��EXIF��Ϣ
            ExifInterface exifInterface = new ExifInterface(path);
            // ��ȡͼƬ����ת��Ϣ
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

    /**
     * ��ͼƬ����ָ���ĽǶȽ�����ת
     *
     * @param bitmap ��Ҫ��ת��ͼƬ
     * @param degree ָ������ת�Ƕ�
     * @return ��ת���ͼƬ
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // ������ת�Ƕȣ�������ת����
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // ��ԭʼͼƬ������ת���������ת�����õ��µ�ͼƬ
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * ��ȡ������Ҫ���������ת�Ƕȵ�Uri
     * @param activity  �����Ļ���
     * @param path      ·��
     * @return          ������Uri
     */
    public static Uri getRotatedUri(Activity activity, String path){
        int degree = BitmapUtil.getBitmapDegree(path);
        if (degree != 0){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap newBitmap = BitmapUtil.rotateBitmapByDegree(bitmap,degree);
            return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(),newBitmap,null,null));
        }else{
            return Uri.fromFile(new File(path));
        }
    }

    /**
     * ��ͼƬ����ָ���ĽǶȽ�����ת
     *
     * @param path   ��Ҫ��ת��ͼƬ��·��
     * @param degree ָ������ת�Ƕ�
     * @return ��ת���ͼƬ
     */
    public static Bitmap rotateBitmapByDegree(String path, int degree) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return rotateBitmapByDegree(bitmap,degree);
    }

}
