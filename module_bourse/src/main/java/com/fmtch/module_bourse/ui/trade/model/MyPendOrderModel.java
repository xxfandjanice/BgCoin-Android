package com.fmtch.module_bourse.ui.trade.model;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.bean.MyPendOrderBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.MyPendOrderActivity;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/20
 */
public class MyPendOrderModel {

    private MyPendOrderActivity view;
    private int page = 1;
    private int limit = 15;

    public MyPendOrderModel(MyPendOrderActivity view) {
        this.view = view;
    }

    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }
        Map<String, String> map = new HashMap<>();
        map.put("status", "0");
        map.put("page", page + "");
        map.put("per_page", limit + "");
        RetrofitManager.getInstance().create(ApiService.class)
                .getMyPendOrders(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<MyPendOrderBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MyPendOrderBean>>() {
                    @Override
                    public void onSuccess(List<MyPendOrderBean> data) {
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
                    public void onException(Throwable connectError) {
                        super.onException(connectError);
                        if (isRefresh) {
                            view.refreshLayout.finishRefresh(false);
                        } else {
                            page--;
                            view.refreshLayout.finishLoadMore(false);
                        }
                    }
                });

    }

    /**
     * 撤单
     */
    public void cancelOrder(final int position) {
        MyPendOrderBean orderBean = view.list.get(position);
        RetrofitManager.getInstance().create(ApiService.class)
                .cancelSingleOrder(orderBean.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showShortToast(R.string.cancel_order_success);
                        view.list.remove(position);
                        view.adapter.notifyItemRemoved(position);
                        if (position != view.list.size()) {
                            view.adapter.notifyItemRangeChanged(position, view.list.size() - position);
                        }
                    }
                });

    }
}
