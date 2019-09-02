package com.fmtch.base.intercepter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //加入请求头信息
        Request request = chain.request();
        request.newBuilder().addHeader("token","xxxx").build();
        return chain.proceed(request);
    }
}
