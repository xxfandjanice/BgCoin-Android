package com.fmtch.base.net;


import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.utils.Consts;
import com.fmtch.base.R;
import com.fmtch.base.log.KLog;
import com.fmtch.base.net.dutil.DUtil;
import com.fmtch.base.net.dutil.callback.SimpleUploadCallback;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.Response;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.response.OssTokenResponse;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.ImageUtil;
import com.fmtch.base.utils.LubanUtils;
import com.fmtch.base.widget.dialog.ShowDialog;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;


import java.io.File;
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.OnCompressListener;

public class RequestUtil {

    public static void requestPost(String api, SuperRequest request, OnResponseListener listener, Context mContext) {
        requestPost(api, request, listener, mContext, false);
    }

    public static void requestPost(String api, SuperRequest request, OnResponseListener listener, Context mContext, boolean loading) {
        requestPost(api, request, listener, mContext, loading, "");
    }

    public static void requestPost(final String api, final SuperRequest request, final OnResponseListener listener, Context mContext, final boolean loading, String content) {
        showLoading(mContext, loading, content);
        HttpUtil.getApiService()
                .postApi(api, request)
                .subscribeOn(Schedulers.io())
                .compose(((BaseActivity) mContext).<Response<SuperResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SuperResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<SuperResponse> response) {
                        if (loading)
                            dismissLoading();
                        if (listener != null) {
                            listener.onNext(response);
                            SuperResponse superResponse = response.getData();
                            listener.onNext(superResponse);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loading)
                            dismissLoading();
                        KLog.d("\n" + e.getCause() + "\n" + e.getMessage());
                        KLog.d(api);
                        KLog.json(new Gson().toJson(request));
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }
                });
    }

    public static void requestGet(String api, OnResponseListener listener, Context mContext) {
        requestGet(api, listener, mContext, false);
    }

    public static void requestGet(String api, OnResponseListener listener, Context mContext, boolean loading) {
        requestGet(api, listener, mContext, loading, "");
    }

    public static void requestGet(final String api, final OnResponseListener listener, Context mContext, final boolean loading, String content) {
        showLoading(mContext, loading, content);
        HttpUtil.getApiService()
                .getApi(api)
                .subscribeOn(Schedulers.io())
                .compose(((BaseActivity) mContext).<Response<SuperResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SuperResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<SuperResponse> response) {
                        if (loading)
                            dismissLoading();
                        if (listener != null) {
                            listener.onNext(response);
                            SuperResponse superResponse = response.getData();
                            listener.onNext(superResponse);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loading)
                            dismissLoading();
                        KLog.d("\n" + e.getCause() + "\n" + e.getMessage());
                        KLog.d(api);
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }
                });
    }

    public static void requestListGet(String api, OnResponseListener listener, Context mContext) {
        requestListGet(api, listener, mContext, false);
    }

    public static void requestListGet(String api, OnResponseListener listener, Context mContext, boolean loading) {
        requestListGet(api, listener, mContext, loading, "");
    }

    public static void requestListGet(final String api, final OnResponseListener listener, Context mContext, final boolean loading, String content) {
        showLoading(mContext, loading, content);
        HttpUtil.getApiService()
                .getListApi(api)
                .subscribeOn(Schedulers.io())
                .compose(((BaseActivity) mContext).<Response<List<SuperResponse>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<List<SuperResponse>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<List<SuperResponse>> response) {
                        if (loading)
                            dismissLoading();
                        if (listener != null) {
                            List<SuperResponse> responses = response.getData();
                            listener.onNextList(response);
                            if (responses == null || responses.isEmpty()) {
                                listener.onListEmpty();
                            } else {
                                listener.onNextList(responses);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loading)
                            dismissLoading();
                        KLog.d("\n" + e.getCause() + "\n" + e.getMessage());
                        KLog.d(api);
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }
                });
    }

    public static void requestDelete(String api, SuperRequest request, OnResponseListener listener, Context mContext, boolean loading) {
        requestDelete(api, request, listener, mContext, loading, "");
    }

    public static void requestDelete(final String api, final SuperRequest request, final OnResponseListener listener, Context mContext, final boolean loading, String content) {
        showLoading(mContext, loading, content);
        HttpUtil.getApiService()
                .deleteApi(api, request)
                .subscribeOn(Schedulers.io())
                .compose(((BaseActivity) mContext).<Response<SuperResponse>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SuperResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<SuperResponse> response) {
                        if (loading)
                            dismissLoading();
                        if (listener != null) {
                            listener.onNext(response);
                            SuperResponse superResponse = response.getData();
                            listener.onNext(superResponse);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loading)
                            dismissLoading();
                        KLog.d("\n" + e.getCause() + "\n" + e.getMessage());
                        KLog.d(api);
                        KLog.json(new Gson().toJson(request));
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }
                });
    }

    public static void uploadFile(final Context mContext, final String filePath, final OnResponseListener listener) {
        showLoading(mContext, true, mContext.getResources().getString(R.string.loading_upload));
        LubanUtils.compress(mContext, filePath, new OnCompressListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(final File file) {
                //处理照片的旋转角度
                String rotateFilePath = ImageUtil.rotatePic(mContext, file.getAbsolutePath());
                final File rotateFile = new File(rotateFilePath);
                //图片压缩完成，开始上传
                HttpUtil.getApiService()
                        .getOssToken(API.GET_OSS_TOKEN)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Response<OssTokenResponse>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Response<OssTokenResponse> response) {
                                final OssTokenResponse ossToken = response.getData();
                                if (ossToken == null)
                                    return;
                                final String fileName = "android_" + System.currentTimeMillis() + new Random().nextInt(10000000) + ".jpg";
                                try {
                                    DUtil.initFormUpload()
                                            .url(ossToken.getHost())
                                            .addParam("OSSAccessKeyId", ossToken.getId())
                                            .addParam("policy", ossToken.getPolicy())
                                            .addParam("Signature", ossToken.getSignature())
                                            .addParam("key", fileName)
                                            .addParam("success_action_status", "200")
                                            .addFile("file", fileName,rotateFile)
                                            .fileUploadBuild()
                                            .upload(new SimpleUploadCallback() {

                                                @Override
                                                public void onFinish(String response) {
                                                    super.onFinish(response);
                                                    dismissLoading();
                                                    if (listener != null) {
                                                        SuperResponse superResponse = new SuperResponse();
                                                        superResponse.setDownload_url(ossToken.getHost() + "/" + fileName);
                                                        listener.onNext(superResponse);
                                                    }
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    super.onError(error);
                                                    dismissLoading();
                                                }

                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dismissLoading();
                                    if (listener != null) {
                                        listener.onError(e);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissLoading();
                                KLog.d("\n" + e.getCause() + "\n" + e.getMessage());
                                if (listener != null) {
                                    listener.onError(e);
                                }
                            }

                            @Override
                            public void onComplete() {
                                if (listener != null) {
                                    listener.onCompleted();
                                }
                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                dismissLoading();
            }
        });

    }


    private static void showLoading(Context mContext, boolean loading, String content) {
        if (loading) {
            if (TextUtils.isEmpty(content)) {
                content = mContext.getResources().getString(R.string.loading);
            }
            ShowDialog.showDialog(mContext, content, true, null);
        }
    }

    private static void dismissLoading() {
        ShowDialog.dissmiss();
    }

}
