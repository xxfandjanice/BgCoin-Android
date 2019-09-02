package com.fmtch.module_bourse.ui.property.model;

import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.AccountDetailActivity;
import com.fmtch.module_bourse.ui.property.fragment.PropertyFragment;
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

/**
 * Created by wtc on 2019/5/21
 */
public class AccountDetailModel {

    private AccountDetailActivity view;
    private int page = 1;
    private int limit = 15;

    public AccountDetailModel(AccountDetailActivity view) {
        this.view = view;
    }

    public void getData(final boolean isRefresh, String type) {

        if (isRefresh) {
            page = 1;
        } else {
            ++page;
        }

        Map<String, String> map = new HashMap<>();
        if (view.accountBean != null && !TextUtils.isEmpty(view.accountBean.getCoin_id())) {
            map.put("coin_id", view.accountBean.getCoin_id());
        }
        //总账户 操作类型: 1：充币 2：提币 3：转入 4：转出   5：系统
        //币币账户 操作类型：1：转入 2：转出 3：买入挂单 4：买入撤销 5：卖出挂单 6：卖出撤销 7：买入收入 8：买入支出 9：卖出收入 10：卖出支出 11：差价返还
        //法币账户 操作类型：1：转入 2：转出 3：卖出挂单 4：卖出撤销 5：手续费返还 6：普通卖出 7：取消订单 8：卖出成交 9：卖出手续费 10：买入成交 11：买入手续费
        map.put("type", type);
        map.put("page", page + "");
        map.put("per_page", limit + "");

        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<List<AccountDetailBean>>> observable = null;
        if (view.accountType == PropertyFragment.PROPERTY_MY_ACCOUNT) {
            observable = apiService.getMyWalletLogNew(map);
        } else if (view.accountType == PropertyFragment.PROPERTY_COIN_COIN_ACCOUNT) {
            observable = apiService.getCoinCoinLogNew(map);
        } else if (view.accountType == PropertyFragment.PROPERTY_PARIS_COIN_ACCOUNT) {
            observable = apiService.getParisCoinLogNew(map);
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

    public void getCoinInfo(int accpunt_type,String coin_id) {
        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<AccountBean>> observable;
        //账户类型(总账户0 币币账户1 法币账户2)
        switch (accpunt_type) {
            case 0:
                observable = apiService.getCoinList(coin_id);
                break;
            case 1:
                observable = apiService.getCoinCoinData(coin_id);
                break;
            case 2:
                observable = apiService.getParisCoinData(coin_id);
                break;
            default:
                observable = apiService.getCoinList(coin_id);
                break;
        }
        observable.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<AccountBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    public void onSuccess(AccountBean data) {
                        if (data == null) {
                            return;
                        }
                        view.accountBean = data;
                        view.updateTopView();
                    }
                });
    }
}
