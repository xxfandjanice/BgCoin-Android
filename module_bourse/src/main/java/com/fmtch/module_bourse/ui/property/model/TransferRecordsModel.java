package com.fmtch.module_bourse.ui.property.model;


import android.view.View;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.TransferRecordsActivity;
import com.fmtch.module_bourse.ui.property.fragment.PropertyFragment;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TransferRecordsModel {

    private TransferRecordsActivity view;
    private int page = 1;
    private int limit = 15;

    private int type;//      1：充币    2：提币   3：划转

    public TransferRecordsModel(TransferRecordsActivity view, int type) {
        this.view = view;
        this.type = type;
    }

    public void getRecords(final boolean isRefresh, String coin_id) {
        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }
        Map<String, String> map = new HashMap<>();
        map.put("coin_id", coin_id);
        //      1：充币    2：提币   3：划转
//        if (type == 3) {
//            //21或31：转入    12或13：转出
//            map.put("type", "21,31,12,13");
//        } else {
//            map.put("type", String.valueOf(type));
//        }
        map.put("page", String.valueOf(page));
        map.put("per_page", String.valueOf(limit));
        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<List<AccountDetailBean>>> observable = null;
        switch (type) {
            case 1:
                observable = apiService.getRechargeLog(map);
                break;
            case 2:
                observable = apiService.getBringLog(map);
                break;
            case 3:
                //划转记录需要传账户类型
                map.put("type",view.account_type);
                observable = apiService.getTransferLog(map);
                break;
        }

        if (observable == null) {
            return;
        }
        observable.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<AccountDetailBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<AccountDetailBean>>() {
                    @Override
                    public void onSuccess(List<AccountDetailBean> data) {
                        if (data == null) {
                            return;
                        }
                        if (isRefresh) {
                            view.refreshLayout.finishRefresh(true);
                            view.list.clear();
                            if (data.size() == 0) {
                                view.adapter.showEmptyPage();
                            } else {
                                view.list.addAll(data);
                                view.adapter.setNewData(view.list);
                            }
                        } else {
                            if (data.size() == 0) {
                                page--;
                                view.refreshLayout.finishLoadMoreWithNoMoreData();
                            } else {
                                view.list.addAll(data);
                                view.refreshLayout.finishLoadMore(true);
                                view.adapter.setNewData(view.list);
                            }
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        if (isRefresh) {
                            view.refreshLayout.finishRefresh(false);
                        } else {
                            page--;
                            view.refreshLayout.finishLoadMore(false);
                        }
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        if (isRefresh) {
                            view.refreshLayout.finishRefresh(false);
                        } else {
                            page--;
                            view.refreshLayout.finishLoadMore(false);
                        }
                    }
                });
    }
}
