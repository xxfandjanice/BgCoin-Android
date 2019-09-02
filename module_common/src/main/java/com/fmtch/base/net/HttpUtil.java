package com.fmtch.base.net;

import com.fmtch.base.log.KLog;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class HttpUtil {

	private HttpUtil() {

	}

	public static ApiService getApiService() {
		return getApiService(RetrofitManager.BASE_URL, null, ApiService.class);
	}

	public static ApiService getApiService(ProgressListener listener) {
		ApiService service = (ApiService) getApiService(RetrofitManager.BASE_URL, listener, (Class) ApiService.class);
		return service;
	}

	public static <T> T getApiService(String baseUrl, ProgressListener listener, Class<T> clazz) {

		OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
		okHttpClient.connectTimeout(20, TimeUnit.SECONDS);
		okHttpClient.readTimeout(20, TimeUnit.SECONDS);
		okHttpClient.writeTimeout(20, TimeUnit.SECONDS);
		//错误重连
		okHttpClient.retryOnConnectionFailure(true);
		BasicParamsInterceptor.Builder interceptorBuilder = null;
		if (listener != null) {
			interceptorBuilder = new BasicParamsInterceptor.Builder(listener);
		} else {
			interceptorBuilder = new BasicParamsInterceptor.Builder();
		}
		String token = SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN,"") + "";
		KLog.e(token);
		interceptorBuilder.addHeaderParam("Content-Type","application/x-www-form-urlencoded");
		interceptorBuilder.addHeaderParam("Authorization", "Bearer " + token);

		BasicParamsInterceptor build = interceptorBuilder.build();

		okHttpClient.addInterceptor(build);

		OkHttpClient client = okHttpClient.build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(JsonConverterFactory.create()) // 自定义加密
				// .addConverterFactory(GsonConverterFactory.create())
				.addConverterFactory(FileConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(client)
				.build();

		return retrofit.create(clazz);
	}
}
