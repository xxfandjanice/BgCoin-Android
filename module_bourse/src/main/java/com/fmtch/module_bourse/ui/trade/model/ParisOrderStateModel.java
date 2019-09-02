package com.fmtch.module_bourse.ui.trade.model;

import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.bean.ParisOrderStateBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.ParisCoinOrderActivity;
import com.fmtch.module_bourse.ui.trade.fragment.ParisCoinOrderStateFragment;
import com.fmtch.module_bourse.utils.ConstUtils;
import com.fmtch.module_bourse.utils.TimeUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/6/25
 */
public class ParisOrderStateModel {

    private ParisCoinOrderStateFragment view;
    private int page = 1;
    private int limit = 15;
    private Disposable disposable;

    public ParisOrderStateModel(ParisCoinOrderStateFragment view) {
        this.view = view;
    }

    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }
        Map<String, String> map = new HashMap<>();
        //0：已取消订单 1：未完成订单 4：已完成订单
        if (view.orderStateType == ParisCoinOrderActivity.UNFINISHED) {
            map.put("status", "1");
        } else if (view.orderStateType == ParisCoinOrderActivity.FINISHED) {
            map.put("status", "4");
        } else if (view.orderStateType == ParisCoinOrderActivity.CANCEL) {
            map.put("status", "0");
        }
        map.put("page", page + "");
        map.put("per_page", limit + "");
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisOrders(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<ParisOrderStateBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ParisOrderStateBean>>() {
                    @Override
                    public void onSuccess(List<ParisOrderStateBean> data) {
                        if (data == null) {
                            return;
                        }
                        refreshOrLoadMore(isRefresh, true, data);
                        //未完成订单,开启本地倒计时
                        if (disposable == null || disposable.isDisposed()) {
                            disposable = startCountdown();
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        refreshOrLoadMore(isRefresh, false, null);
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        refreshOrLoadMore(isRefresh, false, null);
                    }

                });

    }


    /**
     * @param isRefresh 刷新/加载更多
     * @param isSuccess 是否成功
     * @param data      数据
     */
    private void refreshOrLoadMore(boolean isRefresh, boolean isSuccess, List<ParisOrderStateBean> data) {
        if (isRefresh) {
            view.refreshLayout.finishRefresh(isSuccess);
            if (isSuccess) {
                view.list.clear();
                if (data.size() == 0) {
                    view.adapter.showEmptyPage();
                } else {
                    view.list.addAll(data);
                    calculateRemainingTime();
                    view.adapter.setNewData(view.list);
                }
            }
        } else {
            if (isSuccess) {
                view.list.addAll(data);
                calculateRemainingTime();
                if (data.size() < limit) {
                    if (data.size() == 0) {
                        page--;
                    }
                    view.refreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    view.refreshLayout.finishRefresh(true);
                }
                view.adapter.setNewData(view.list);
            } else {
                page--;
                view.refreshLayout.finishLoadMore(false);
            }
        }
    }

    /**
     * 计算未完成订单的剩余支付时间
     */
    private void calculateRemainingTime() {
        if (view.list == null || view.list.size() == 0 || view.orderStateType != ParisCoinOrderActivity.UNFINISHED) {
            return;
        }
        for (ParisOrderStateBean bean : view.list) {
            if (!TextUtils.isEmpty(bean.getPayment_at())) {
                bean.setRemainingTime(bean.getLimit_finish_time());
            } else {
                bean.setRemainingTime(bean.getLimit_pay_time());
            }
        }
    }

    /**
     * 未完成订单,开启倒计时
     */
    private Disposable startCountdown() {
        if (view.list == null || view.list.size() == 0 || view.orderStateType != ParisCoinOrderActivity.UNFINISHED) {
            return null;
        }
        return Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        //剩余时间减1
                        for (ParisOrderStateBean stateBean : view.list) {
                            stateBean.setRemainingTime(stateBean.getRemainingTime() - 1);
                        }
                        view.adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 关闭倒计时
     */
    public void closeCountDown() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

}
