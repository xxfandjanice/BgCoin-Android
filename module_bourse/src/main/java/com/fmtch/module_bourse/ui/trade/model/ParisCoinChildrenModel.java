package com.fmtch.module_bourse.ui.trade.model;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.bean.ParisCoinMarketBean;
import com.fmtch.module_bourse.bean.RiseFallBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.trade.fragment.ParisCoinChildrenFragment;
import com.fmtch.module_bourse.utils.LogUtils;
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
public class ParisCoinChildrenModel {

    private ParisCoinChildrenFragment view;
    private int page = 1;
    private int limit = 15;

    public ParisCoinChildrenModel(ParisCoinChildrenFragment view) {
        this.view = view;
    }

    public void getData(final boolean isRefresh,String min,String payMethod) {
        if (view.coinBean == null) {
            return;
        }
        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }
        Map<String, String> map = new HashMap<>();
        map.put("coin_id", view.coinBean.getId() + ""); //币种id
        map.put("side", view.tradeType + "");           //商家出售:SELL     商家购买:BUY
        map.put("min_number", min);                     //最小交易金额
        map.put("payment", payMethod);                  //收款方式:Bankcard：银行卡 Alipay:支付宝  Wechat:微信
        map.put("perPage", limit + "");                 //每一页条数，默认15
        map.put("page", page + "");                     //条数
        RetrofitManager.getInstance().create(ApiService.class)
                .getParisMarketList(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<ParisCoinMarketBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ParisCoinMarketBean>>() {
                    @Override
                    public void onSuccess(List<ParisCoinMarketBean> data) {
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
     * @param isRefresh  刷新/加载更多
     * @param isSuccess  是否成功
     * @param data       数据
     */
    private void refreshOrLoadMore(boolean isRefresh, boolean isSuccess, List<ParisCoinMarketBean> data) {
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


}
