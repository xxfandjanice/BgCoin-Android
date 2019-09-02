package com.fmtch.module_bourse.ui.property.model;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.ZXingUtils;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.RechargeCoinActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RechargeCoinModel {

    private RechargeCoinActivity view;

    public RechargeCoinModel(RechargeCoinActivity view) {
        this.view = view;
    }

//    public void getCoinList() {
//        RetrofitManager.getInstance().create(ApiService.class)
//                .getCoinList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<ArrayList<AccountBean>>() {
//                    @Override
//                    public void onSuccess(ArrayList<AccountBean> data) {
//                        if (data == null || data.size() < 1) {
//                            return;
//                        }
//                        view.coinList = data;
//                        //按字母排序
//                        Collections.sort(view.coinList, new AccountBean());
//                        for (AccountBean accountBean : view.coinList) {
//                            if (accountBean.getCoin() != null) {
//                                AccountBean.Coin coin = accountBean.getCoin();
//                                if (coin.getCan_recharge() == 1) {
//                                    view.coinInfo = accountBean;
//                                    view.tvCoinName.setText(coin.getName());
//                                    if (!TextUtils.isEmpty(accountBean.getAddress())) {
//                                        view.mRechargeAddress = accountBean.getAddress();
//                                        view.tvRechargeAddress.setText(view.mRechargeAddress);
//                                        view.mQrCodeBitmap = ZXingUtils.Create2DCode(view.mRechargeAddress, view.ivQrCode.getWidth(), view.ivQrCode.getHeight());
//                                        view.ivQrCode.setImageBitmap(view.mQrCodeBitmap);
//                                    }
//                                    if (!TextUtils.isEmpty(coin.getRecharge_prompt())) {
//                                        view.tvWarningContent.setText(coin.getRecharge_prompt().replace("\\n", "\n"));
//                                    }
//                                    if (coin.getIs_tag() == 1) {
//                                        view.tvTag.setText(view.coinInfo.getTag());
//                                        view.tvTag.setVisibility(View.VISIBLE);
//                                        view.tvCopyTag.setVisibility(View.VISIBLE);
//                                    } else {
//                                        view.tvTag.setVisibility(View.GONE);
//                                        view.tvCopyTag.setVisibility(View.GONE);
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                });
//    }

    public void getCoinInfo(String coin_id) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getCoinList(coin_id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<AccountBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    public void onSuccess(AccountBean accountBean) {
                        if (accountBean == null || view == null) {
                            return;
                        }
                        view.coinInfo = accountBean;
                        AccountBean.Coin coin = view.coinInfo.getCoin();
                        view.tvCoinName.setText(coin.getName());
                        if (!TextUtils.isEmpty(accountBean.getAddress())) {
                            view.mRechargeAddress = accountBean.getAddress();
                        } else {
                            view.mRechargeAddress = "";
                        }
                        view.tvRechargeAddress.setText(view.mRechargeAddress);
                        view.mQrCodeBitmap = ZXingUtils.Create2DCode(view.mRechargeAddress, view.ivQrCode.getWidth(), view.ivQrCode.getHeight());
                        view.ivQrCode.setImageBitmap(view.mQrCodeBitmap);
                        if (!TextUtils.isEmpty(coin.getRecharge_prompt())) {
                            view.tvWarningContent.setText(coin.getRecharge_prompt().replace("\\n", "\n"));
                        }else {
                            view.tvWarningContent.setText("");
                        }
                        if (coin.getIs_tag() == 1) {
                            view.tvTag.setText(view.coinInfo.getTag());
                            view.tvTag.setVisibility(View.VISIBLE);
                            view.tvCopyTag.setVisibility(View.VISIBLE);
                        } else {
                            view.tvTag.setVisibility(View.GONE);
                            view.tvCopyTag.setVisibility(View.GONE);
                        }
                    }
                });
    }

}
