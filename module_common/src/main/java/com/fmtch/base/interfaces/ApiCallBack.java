package com.fmtch.base.interfaces;

public interface ApiCallBack<T> {
    void onSuccess(T response);

    void onFailure();

    void onCompleted();
}
