package com.fmtch.module_bourse.ui.property.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.property.fragment.AccountFragment;
import com.fmtch.module_bourse.ui.property.fragment.PropertyFragment;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/15
 */
public class PropertyModel {

    private PropertyFragment view;

    private List<AccountBean> myWalletList;

    private List<AccountBean> myCoinCoinList;

    private List<AccountBean> myParisCoinList;

    public PropertyModel(PropertyFragment view) {
        this.view = view;
    }

    /**
     * 请求接口
     */
    public void getData() {

        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        //我的钱包
        Observable<BaseResponse<List<AccountBean>>> observable1 = apiService.getMyWallet();
        //币币账户
        Observable<BaseResponse<List<AccountBean>>> observable2 = apiService.getCoinCoinList();
        //法币账户
        Observable<BaseResponse<List<AccountBean>>> observable3 = apiService.getParisCoinList();
        observable1.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<AccountBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<AccountBean>>() {
                    @Override
                    public void onSuccess(List<AccountBean> data) {
                        myWalletList = data;
                        updateData();
                    }
                });
        observable2.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<AccountBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<AccountBean>>() {
                    @Override
                    public void onSuccess(List<AccountBean> data) {
                        myCoinCoinList = data;
                        updateData();
                    }
                });
        observable3.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<AccountBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<AccountBean>>() {
                    @Override
                    public void onSuccess(List<AccountBean> data) {
                        myParisCoinList = data;
                        updateData();
                    }
                });
    }

    private void updateData() {
        if (myWalletList == null || myCoinCoinList == null || myParisCoinList == null
                || view.coinToBTCList == null || view.coinToBTCList.size() < 1)
            return;
        BigDecimal totalBigDecimal = BigDecimal.ZERO;
        if (myWalletList.size() > 0) {
            for (AccountBean accountBean : myWalletList) {
                for (CoinToBTC coinToBTC : view.coinToBTCList) {
                    if (TextUtils.equals(accountBean.getCoin().getName(), coinToBTC.getCoin_name())) {
                        BigDecimal btcDecimal = new BigDecimal(accountBean.getAvailable()).add(new BigDecimal(accountBean.getDisabled()))
                                .multiply(new BigDecimal(coinToBTC.getCoin_to_btc()));
                        totalBigDecimal = totalBigDecimal.add(btcDecimal);
                    }
                }
            }
        }
        if (myCoinCoinList.size() > 0) {
            for (AccountBean accountBean : myCoinCoinList) {
                for (CoinToBTC coinToBTC : view.coinToBTCList) {
                    if (TextUtils.equals(accountBean.getCoin().getName(), coinToBTC.getCoin_name())) {
                        BigDecimal btcDecimal = new BigDecimal(accountBean.getAvailable()).add(new BigDecimal(accountBean.getDisabled()))
                                .multiply(new BigDecimal(coinToBTC.getCoin_to_btc()));
                        totalBigDecimal = totalBigDecimal.add(btcDecimal);
                    }
                }
            }
        }
        if (myParisCoinList.size() > 0) {
            for (AccountBean accountBean : myParisCoinList) {
                for (CoinToBTC coinToBTC : view.coinToBTCList) {
                    if (TextUtils.equals(accountBean.getCoin().getName(), coinToBTC.getCoin_name())) {
                        BigDecimal btcDecimal = new BigDecimal(accountBean.getAvailable()).add(new BigDecimal(accountBean.getDisabled()))
                                .multiply(new BigDecimal(coinToBTC.getCoin_to_btc()));
                        totalBigDecimal = totalBigDecimal.add(btcDecimal);
                    }
                }
            }
        }
        view.totalBTC = totalBigDecimal.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        view.tvTotalCoin.setText(view.totalBTC);
        view.tvTotalMoney.setText(String.format("≈ %s", NumberUtils.getBTCToMoneyWithUnit(view.totalBTC)));
    }
}
