package com.fmtch.module_bourse.ui.property.model;


import android.text.TextUtils;
import android.view.View;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.StringUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.request.WithdrawRequest;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.BringCoinActivity;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BringCoinModel {

    private BringCoinActivity view;

    public BringCoinModel(BringCoinActivity view) {
        this.view = view;
    }

    public void getCoinList() {
        RetrofitManager.getInstance().create(ApiService.class)
                .getCoinList()
                .subscribeOn(Schedulers.io())
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
                                if (coin.getCan_withdraw() == 1) {
                                    view.coinInfo = accountBean;
                                    view.tvCoinName.setText(coin.getName());
                                    view.tvBringMinNum.setText(NumberUtils.getFormatMoney(coin.getWithdraw_min()));
//                                    view.etNum.setHint(String.format(view.getResources().getString(R.string.now_withdraw_num), NumberUtils.getFormatMoney(accountBean.getAvailable()), coin.getName()));
                                    String etNumHint = String.format(view.getResources().getString(R.string.now_withdraw_num), NumberUtils.getFormatMoney(accountBean.getAvailable()), coin.getName());
                                    StringUtils.setTextHintStyle(view,view.etNum,etNumHint,R.color.theme,4,etNumHint.length());
                                    view.tvFee.setText(NumberUtils.getFormatMoney(coin.getWithdraw_fee()));
                                    if (!TextUtils.isEmpty(coin.getWithdraw_prompt())) {
                                        view.tvExplain1.setText(coin.getWithdraw_prompt().replace("\\n", "\n"));
                                    }
                                    if (coin.getIs_tag() == 1) {
                                        view.llTag.setVisibility(View.VISIBLE);
                                    } else {
                                        view.llTag.setVisibility(View.GONE);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    public void withdraw(WithdrawRequest request) {
        RetrofitManager.getInstance().create(ApiService.class)
                .withdraw(request)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showLongToast(R.string.submit_success);
                        view.finish();
                    }

                });
    }

}
