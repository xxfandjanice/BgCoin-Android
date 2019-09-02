package com.fmtch.module_bourse.http;

import android.net.ParseException;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.router.RouterMap;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;


/**
 * 请求结果作统一处理
 * Created by wtc on 2017/11/30.
 */

public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {

    /**
     * 请求数据成功 且响应码为200
     *
     * @param data 服务器返回的数据
     */
    public abstract void onSuccess(T data);

    /**
     * 服务器返回数据，但响应码不为200
     *
     * @param response 服务器返回的数据
     */
    public void onFail(BaseResponse response) {
        //为某些特定code做统一处理(如:token失效)
        if (response.getCode() == 401) {
            EventBus.getDefault().post(new EventBean<>(EventType.USER_EXIT));
            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
        } else {
            ToastUtils.showShortToast(TextUtils.isEmpty(response.getMessage()) ? "请求错误" : response.getMessage());
        }
    }

    /**
     * 请求异常
     *
     * @param e 错误信息
     */
    public void onException(Throwable e) {
    }


    @Override
    public void onNext(BaseResponse<T> response) {
        if (response.getCode() == HttpConstants.SUCCESS_CODE) {
            onSuccess(response.getData());
        } else {
            onFail(response);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        if (e instanceof HttpException) {     //   HTTP错误
            try {
                HttpException exception = (HttpException) e;
                ResponseBody errorBody = exception.response().errorBody();
                String s;
                if (errorBody == null) {
                    return;
                }
                s = errorBody.string();
                Gson gson = new Gson();
                BaseResponse response = gson.fromJson(s, BaseResponse.class);
                //请求失败
                onFail(response);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e instanceof ConnectException || e instanceof UnknownHostException) {   //   连接错误
            onException(e);
            ToastUtils.showShortToast("网络连接异常");
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(e);
            ToastUtils.showShortToast("网络连接异常");
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {   //  解析错误
            onException(e);
            ToastUtils.showShortToast("数据解析错误");
        } else {
            onException(e);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onComplete() {
    }

}
