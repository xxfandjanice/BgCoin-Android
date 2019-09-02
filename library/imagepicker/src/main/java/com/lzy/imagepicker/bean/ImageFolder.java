package com.lzy.imagepicker.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * ��    �ߣ�jeasonlzy������Ң Github��ַ��https://github.com/jeasonlzy0216
 * ��    ����1.0
 * �������ڣ�2016/5/19
 * ��    ����ͼƬ�ļ���
 * �޶���ʷ��
 * ================================================
 */
public class ImageFolder implements Serializable {

    public String name;  //��ǰ�ļ��е�����
    public String path;  //��ǰ�ļ��е�·��
    public ImageItem cover;   //��ǰ�ļ�����ҪҪ��ʾ������ͼ��Ĭ��Ϊ�����һ��ͼƬ
    public ArrayList<ImageItem> images;  //��ǰ�ļ���������ͼƬ�ļ���

    /** ֻҪ�ļ��е�·����������ͬ������Ϊ����ͬ���ļ��� */
    @Override
    public boolean equals(Object o) {
        try {
            ImageFolder other = (ImageFolder) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
