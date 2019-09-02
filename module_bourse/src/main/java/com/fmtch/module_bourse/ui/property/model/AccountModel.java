package com.fmtch.module_bourse.ui.property.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.RateInfo;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.bean.RiseFallBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.home.fragment.HomeFragment;
import com.fmtch.module_bourse.ui.property.fragment.AccountFragment;
import com.fmtch.module_bourse.ui.property.fragment.PropertyFragment;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by wtc on 2019/5/15
 */
public class AccountModel {

    private AccountFragment view;

    public AccountModel(AccountFragment view) {
        this.view = view;
    }

    /**
     * 请求接口
     *
     * @param fragmentType
     */
    public void getData(int fragmentType) {

        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<List<AccountBean>>> observable = null;

        if (fragmentType == PropertyFragment.PROPERTY_MY_ACCOUNT) {
            //我的钱包
            observable = apiService.getMyWallet();
        } else if (fragmentType == PropertyFragment.PROPERTY_COIN_COIN_ACCOUNT) {
            //币币账户
            observable = apiService.getCoinCoinList();
        } else if (fragmentType == PropertyFragment.PROPERTY_PARIS_COIN_ACCOUNT) {
            //法币账户
            observable = apiService.getParisCoinList();
        }
        if (observable == null) {
            return;
        }
        view.adapter.showLoadingPage();
        observable.subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<AccountBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<AccountBean>>() {
                    @Override
                    public void onSuccess(List<AccountBean> data) {
                        if (data != null) {
                            if (data.size() == 0) {
                                view.adapter.showEmptyPage();
                            } else {
                                view.list.clear();
                                view.list.addAll(data);
                                BigDecimal totalBigDecimal = BigDecimal.ZERO;
                                if (view.coinToBTCList != null && view.coinToBTCList.size() > 0) {
                                    view.haveMoneyList.clear();
                                    for (AccountBean accountBean : data) {
                                        BigDecimal numDecimal = new BigDecimal(accountBean.getAvailable()).add(new BigDecimal(accountBean.getDisabled()));
                                        for (CoinToBTC coinToBTC : view.coinToBTCList) {
                                            if (TextUtils.equals(accountBean.getCoin().getName(), coinToBTC.getCoin_name())) {
                                                BigDecimal btcDecimal = numDecimal.multiply(new BigDecimal(coinToBTC.getCoin_to_btc()));
                                                totalBigDecimal = totalBigDecimal.add(btcDecimal);
                                                accountBean.setBtc_num(btcDecimal);
                                                if (numDecimal.compareTo(BigDecimal.ZERO) == 1) {
                                                    view.haveMoneyList.add(accountBean);
                                                }
                                            }
                                        }
                                    }
                                }

                                view.totalBTC = totalBigDecimal;
                                view.tvTotalCoin.setText(view.isHideMoney ? view.hideText : view.totalBTC.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString());
                                view.tvTotalMoney.setText(view.isHideMoney ? view.hideText : "≈ " + NumberUtils.getBTCToMoneyWithUnit(view.totalBTC));

                                //是否隐藏小额币种
                                if (view.isHideFenCoinKind)
                                    view.adapter.setNewData(view.haveMoneyList);
                                else
                                    view.adapter.setNewData(view.list);
                                //备份数据
                                view.coinListCopy.clear();
                                view.haveMoneyListCopy.clear();
                                view.coinListCopy.addAll(view.list);
                                view.haveMoneyListCopy.addAll(view.haveMoneyList);
                            }
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        view.adapter.showErrorPage(response.getMessage());
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        view.adapter.showErrorPage("");
                    }
                });
    }
}
