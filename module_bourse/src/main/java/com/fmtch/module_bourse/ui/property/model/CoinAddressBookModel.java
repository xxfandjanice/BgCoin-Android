package com.fmtch.module_bourse.ui.property.model;


import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.net.API;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.bean.CoinAddressBean;
import com.fmtch.module_bourse.bean.request.AddCoinAddressRequest;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.activity.AddCoinAddressActivity;
import com.fmtch.module_bourse.ui.property.activity.CoinAddressBookActivity;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CoinAddressBookModel {

    private CoinAddressBookActivity view;

    public CoinAddressBookModel(CoinAddressBookActivity view) {
        this.view = view;
    }

    public void getCoinAddressList(String coin_id) {
        RetrofitManager.getInstance().create(ApiService.class)
                .getCoinAddressList(API.COIN_ADDRESS + "/" + coin_id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<CoinAddressBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<CoinAddressBean>>() {
                    @Override
                    public void onSuccess(List<CoinAddressBean> data) {
                        if (data == null || data.size() < 1) {
                            view.adapter.showEmptyPage();
                            return;
                        }
                        if (view.SelecCoinAddressBean != null) {
                            for (CoinAddressBean coinAddressBean : data) {
                                if (TextUtils.equals(view.SelecCoinAddressBean.getId(), coinAddressBean.getId())) {
                                    coinAddressBean.setType(1);
                                } else {
                                    coinAddressBean.setType(0);
                                }
                            }
                        }
                        view.adapter.setNewData(data);
                    }
                });
    }

    public void deleteCoinAddress(String address_id, final int position) {
        RetrofitManager.getInstance().create(ApiService.class)
                .deleteCoinAddress(API.COIN_ADDRESS + "/" + address_id)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showLongToast(R.string.delete_success);
                        view.adapter.remove(position);
                        if (view.adapter.getItemCount() < 1){
                            view.adapter.showEmptyPage();
                        }
                    }
                });
    }

}
