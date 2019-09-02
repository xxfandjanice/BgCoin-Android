package com.fmtch.base.net.dutil;

import android.content.Context;

import com.fmtch.base.net.dutil.download.DBuilder;
import com.fmtch.base.net.dutil.upload.DirectUploadBuilder;
import com.fmtch.base.net.dutil.upload.FormUploadBuilder;


public class DUtil {

    /**
     * 下载
     *
     * @param context
     * @return
     */
    public static DBuilder init(Context context) {
        return new DBuilder(context);
    }

    /**
     * 表单式文件上传
     *
     * @return
     */
    public static FormUploadBuilder initFormUpload() {
        return new FormUploadBuilder();
    }

    /**
     * 将文件作为请求体上传
     *
     * @return
     */
    public static DirectUploadBuilder initUpload() {
        return new DirectUploadBuilder();
    }
}
