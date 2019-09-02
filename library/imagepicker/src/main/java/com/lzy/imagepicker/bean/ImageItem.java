package com.lzy.imagepicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * ================================================
 * ��    �ߣ�jeasonlzy������Ң Github��ַ��https://github.com/jeasonlzy0216
 * ��    ����1.0
 * �������ڣ�2016/5/19
 * ��    ����ͼƬ��Ϣ
 * �޶���ʷ��
 * ================================================
 */
public class ImageItem implements Serializable, Parcelable {

    public String name;       //ͼƬ������
    public String path;       //ͼƬ��·��
    public long size;         //ͼƬ�Ĵ�С
    public int width;         //ͼƬ�Ŀ��
    public int height;        //ͼƬ�ĸ߶�
    public String mimeType;   //ͼƬ������
    public long addTime;      //ͼƬ�Ĵ���ʱ��

    /** ͼƬ��·���ʹ���ʱ����ͬ����Ϊ��ͬһ��ͼƬ */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem) o;
            return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
        }

        return super.equals(o);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
    }

    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
