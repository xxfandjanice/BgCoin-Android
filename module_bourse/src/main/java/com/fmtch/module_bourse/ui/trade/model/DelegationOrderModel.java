package com.fmtch.module_bourse.ui.trade.model;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.widget.dialog.ShowDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.DelegationOrderBean;
import com.fmtch.module_bourse.bean.ParisCoinMarketBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.activity.DelegationOrderActivity;
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
public class DelegationOrderModel {

    private DelegationOrderActivity view;
    private int page = 1;
    private int limit = 15;

    public DelegationOrderModel(DelegationOrderActivity view) {
        this.view = view;
    }

    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("per_page", limit + "");
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisDelegationOrders(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<DelegationOrderBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<DelegationOrderBean>>() {
                    @Override
                    public void onSuccess(List<DelegationOrderBean> data) {
                        if (data == null) {
                            return;
                        }
                        refreshOrLoadMore(isRefresh, true, data);
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
    private void refreshOrLoadMore(boolean isRefresh, boolean isSuccess, List<DelegationOrderBean> data) {
        if (isRefresh) {
            view.refreshLayout.finishRefresh(isSuccess);
            if (isSuccess) {
                view.list.clear();
                if (data.size() == 0) {
                    view.adapter.showEmptyPage();
                } else {
                    view.list.addAll(data);
                    view.adapter.setNewData(view.list);
                }
            }
        } else {
            if (isSuccess) {
                view.list.addAll(data);
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
     * 获取全部暂停开关状态
     */
    public void getPauseSwitchState() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisOrderSwitchState()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Integer>() {
                    @Override
                    public void onSuccess(Integer isPause) {
                        view.isPauseAll = isPause == 1;
                        view.ivSwitch.setImageResource(isPause == 1 ? R.mipmap.icon_switch_off : R.mipmap.icon_switch_on);
                    }
                });
    }


    /**
     * 撤单
     */
    public void cancelOrder(final int position) {
        final ShowDialog dialog = ShowDialog.showDialog(view, view.getResources().getString(R.string.loading), true, null);
        DelegationOrderBean orderBean = view.list.get(position);
        RetrofitManager.getInstance().create(ApiService.class)
                .cancelDelegationOrder(String.valueOf(orderBean.getId()))
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        dialog.dismiss();
                        ToastUtils.showShortToast(R.string.cancel_order_success);
                        view.adapter.remove(position);
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        dialog.dismiss();
                    }

                    @Override
                    public void onException(Throwable e) {
                        super.onException(e);
                        dialog.dismiss();
                    }
                });

    }

    /**
     * 全部暂停
     */
    public void pauseOrder() {
        final ShowDialog dialog = ShowDialog.showDialog(view, view.getResources().getString(R.string.loading), true, null);
        RetrofitManager.getInstance().create(ApiService.class)
                .pauseDelegationOrder()
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Integer>() {
                    @Override
                    public void onSuccess(Integer isPause) {
                        dialog.dismiss();
                        view.isPauseAll = isPause == 1;
                        view.ivSwitch.setImageResource(isPause == 1 ? R.mipmap.icon_switch_off : R.mipmap.icon_switch_on);
                        for (DelegationOrderBean delegationOrderBean : view.list) {
                            delegationOrderBean.setIs_pause(isPause);
                        }
                        view.adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        dialog.dismiss();
                    }

                    @Override
                    public void onException(Throwable e) {
                        super.onException(e);
                        dialog.dismiss();
                    }
                });

    }
}
