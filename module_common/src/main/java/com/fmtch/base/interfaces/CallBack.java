package com.fmtch.base.interfaces;

public interface CallBack<T> {
    void onSuccess(T t);
    void onFailure(String message);
}
