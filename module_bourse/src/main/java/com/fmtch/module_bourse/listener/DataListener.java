package com.fmtch.module_bourse.listener;

import com.fmtch.module_bourse.http.BaseResponse;

/**
 * Created by wtc on 2019/8/9
 */
public interface DataListener<T> {

    void onSuccess(T data);

    void onFail(BaseResponse response);

    void onException(Throwable e);
}
