package com.fmtch.module_bourse.ui.property.model;


import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.TransferCoinActivity;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TransferCoinModel {

    private TransferCoinActivity view;

    public TransferCoinModel(TransferCoinActivity view) {
        this.view = view;
    }

    public void getCoinList(int accpunt_type) {
        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<ArrayList<AccountBean>>> observable;
        //账户类型(总账户0 币币账户1 法币账户2)
        switch (accpunt_type) {
            case 0:
                observable = apiService.getCoinList();
                break;
            case 1:
                observable = apiService.getCoinCoinListData();
                break;
            case 2:
                observable = apiService.getParisCoinListData();
                break;
            default:
                observable = apiService.getCoinList();
                break;
        }
        observable.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<ArrayList<AccountBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ArrayList<AccountBean>>() {
                    @Override
                    public void onSuccess(ArrayList<AccountBean> data) {
                        if (data == null || data.size() < 1) {
                            return;
                        }
                        view.coinList = data;
                        //按字母排序
                        Collections.sort(view.coinList, new AccountBean());

                        for (AccountBean accountBean : view.coinList) {
                            if (accountBean.getCoin() != null) {
                                AccountBean.Coin coin = accountBean.getCoin();
                                int status;
                                if (view.type == 4) {
                                    //支持币币交易
                                    status = coin.getIs_spot();
                                } else {
                                    //支持法币交易
                                    status = coin.getIs_otc();
                                }
                                if (status == 1) {
                                    view.coinInfo = accountBean;
                                    if (!TextUtils.isEmpty(coin.getName())) {
                                        view.tvSelectCoin.setText(coin.getName());
                                        view.tvMaxTransfer.setText(String.format(view.getResources().getString(R.string.max_can_transfer_num), NumberUtils.getFormatMoney(view.coinInfo.getAvailable()), coin.getName()));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    public void transfer(String coin_id, String from_account, String to_account, String number) {
        Map<String, String> map = new HashMap<>();
        map.put("coin_id", coin_id);
        map.put("from_account", from_account);
        map.put("to_account", to_account);
        map.put("number", number);
        RetrofitManager.getInstance().create(ApiService.class)
                .transfer(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showLongToast(R.string.transfer_success);
                        view.finish();
                    }

                });
    }

}
