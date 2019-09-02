package com.fmtch.module_bourse.listener;

/**
 * Created by wtc on 2019/5/7
 */
public interface DataChangeListener<T> {

    void onDataChange(T data);

}
