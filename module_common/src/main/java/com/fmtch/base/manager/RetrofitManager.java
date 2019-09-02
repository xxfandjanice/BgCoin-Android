package com.fmtch.base.manager;

import android.util.Log;

import com.fmtch.base.BuildConfig;
import com.fmtch.base.log.KLog;
import com.fmtch.base.net.API;
import com.fmtch.base.utils.CLog;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StringUtil;
import com.fmtch.base.utils.StringUtils;
import com.google.gson.GsonBuilder;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final String TAG = "API_LOG";
    public static String BASE_URL = "http://192.168.2.144:81";//测试服务器
    //声明Retrofit对象
    private Retrofit mRetrofit;

    //手动创建一个OkHttpClient
    private OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)  //失败重连
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        try {
                            String token = SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, "") + "";
                            Request.Builder builder = chain.request().newBuilder();
                            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
                            builder.addHeader("Authorization", "Bearer " + token);
                            return chain.proceed(builder.build());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Request.Builder builder = chain.request().newBuilder();
                            return chain.proceed(builder.build());
                        }
                    }
                })
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Charset charset = Charset.forName("UTF-8");
//                        Buffer buffer = new Buffer();
//                        Request request = chain.request();
//                        RequestBody requestBody = request.body();
//
//                        Response response = chain.proceed(request);
//
//                        ResponseBody responseBody = response.body();
//                        BufferedSource source = responseBody.source();
//
//                        Headers headers = request.headers();
//                        if (headers != null) {
//                            int responseHeadersLength = headers.size();
//
//                            CLog.d(TAG, "╔════════════════════════════════════════════════════════════════════════════════════════");
//                            CLog.d(TAG, String.format("║ 请求地址 %s", request.url()));
//                            CLog.d(TAG, String.format("║ 请求方式 %s", request.method()));
//                            CLog.d(TAG, "╟────────────────────────────────────────────────────────────────────────────────────────");
//                            for (int i = 0; i < responseHeadersLength; i++) {
//                                String headerName = headers.name(i);
//                                String headerValue = headers.get(headerName);
//                                CLog.d(TAG, String.format("║ 请求头: Key: %s Value: %s", headerName, headerValue));
//                            }
//                        }
//
//                        CLog.d(TAG, "╟────────────────────────────────────────────────────────────────────────────────────────");
//                        if (requestBody != null) {
//                            requestBody.writeTo(buffer);
//                            CLog.d(TAG, String.format("║ 请求参数 %s", buffer.readString(charset)));
//                            CLog.d(TAG, "╟────────────────────────────────────────────────────────────────────────────────────────");
//                        }
//
//                        if (source == null) {
//                            CLog.d(TAG, String.format("║ 访问错误码 %s", response.code()));
//                        } else {
//                            source.request(Long.MAX_VALUE); // Buffer the entire body.
//                            Buffer bufferS = source.buffer();
//                            MediaType contentType = responseBody.contentType();
//                            if (contentType != null) {
//                                String json = bufferS.clone().readString(contentType.charset(charset));
//                                CLog.d(TAG, "║ 返回数据");
//                                String[] con = StringUtil.formatJson(json).split("\n");
//                                for (String line : con) {
//                                    CLog.d(TAG, "║" + line);
//                                }
//                            }
//                        }
//                        CLog.d(TAG, "╚════════════════════════════════════════════════════════════════════════════════════════");
//                        return response;
//                    }
//                })
                .build();
    }

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static RetrofitManager getInstance() {
        return Holder.mInstance;
    }

    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

    private static class Holder {
        private static final RetrofitManager mInstance = new RetrofitManager();
    }
}
