package com.fmtch.base.rxjava;


import android.util.Log;

//import com.fmtch.base.config.KeyConstant;
//import com.fmtch.base.event.EventBean;
//import com.fmtch.base.event.EventType;
import com.fmtch.base.interfaces.ApiCallBack;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.pojo.response.BaseResultEntity;
//import com.fmtch.base.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;


public class ObserverCallBack<T> implements Observer<T> {
    private ApiCallBack<T> apiCallBack;   //回调监听
    protected Disposable disposable;


    public ObserverCallBack(ApiCallBack<T> apiCallBack) {
        this.apiCallBack = apiCallBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T t) {
        if (t instanceof BaseResultEntity) {
            BaseResultEntity<T> entity = (BaseResultEntity<T>) t;
            if (entity.getCode().equals("200")) {
                Log.e("aa", "成功了 ");
            } else if (entity.getCode().equals("205")) {
                //登录失效改成未登录状态
            }
        }

        apiCallBack.onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        apiCallBack.onFailure();
        //释放CompositeDispose，否则会造成内存泄漏
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        apiCallBack.onCompleted();
        //释放CompositeDispose，否则会造成内存泄漏
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
