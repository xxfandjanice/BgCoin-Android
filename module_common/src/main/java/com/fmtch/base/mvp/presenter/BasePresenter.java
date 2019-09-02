package com.fmtch.base.mvp.presenter;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Presenter的基本接口，定义Presenter共有的方法
 */
public abstract class BasePresenter<M,V> {
    protected M model;
    protected V view;

    public void attachView(V view) {
        this.view = view;
        if(model == null) {
            model = createModel();
        }
    }

    public void detachView() {
        view = null;
        onDestroy();
    }

    public void  addSubscription(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    protected abstract M createModel();

    public void onDestroy() {

    }
}
