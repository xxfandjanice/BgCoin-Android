package com.fmtch.base.net.impl;


import com.fmtch.base.net.OnResponseListener;
import com.fmtch.base.net.response.Response;
import com.fmtch.base.net.response.SuperResponse;

import java.io.File;
import java.util.List;


public class OnResponseListenerImpl
		implements OnResponseListener {


	@Override
	public void onStart() {

	}

	@Override
	public void onCompleted() {

	}

	@Override
	public void onError(Throwable e) {

	}

	@Override
	public void onNext(Response<SuperResponse> response) {

	}

	@Override
	public void onNext(SuperResponse response) {

	}

	@Override
	public void onResponseError(int code, String msg) {

	}

	@Override
	public void onNextList(Response<List<SuperResponse>> response) {

	}

	@Override
	public void onNextList(List<SuperResponse> responses) {

	}

	@Override
	public void onNextListMaxTotal(String max_total) {

	}


	@Override
	public void onListEmpty() {

	}

	@Override
	public void downloadFile(File file, String url) {

	}

	@Override
	public void downloadProgress(long total, long progress, boolean idDone, String url) {

	}

	@Override
	public void downloadComplete() {

	}

	@Override
	public void downloadFileEmpty(String urll) {

	}
}
