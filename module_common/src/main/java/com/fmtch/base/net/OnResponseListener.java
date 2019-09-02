package com.fmtch.base.net;



import com.fmtch.base.net.response.Response;
import com.fmtch.base.net.response.SuperResponse;

import java.io.File;
import java.util.List;

public interface OnResponseListener {

	void onStart();

	void onCompleted();

	void onError(Throwable e);

	void onNext(Response<SuperResponse> response);

	void onNext(SuperResponse response);

	void onResponseError(int code, String msg);

	void onNextList(Response<List<SuperResponse>> response);

	void onNextList(List<SuperResponse> response);

	void onNextListMaxTotal(String max_total);

	void onListEmpty();

	void downloadFile(File file, String url);

	void downloadProgress(long total, long progress, boolean idDone, String url);

	void downloadComplete();

	void downloadFileEmpty(String url);
}
