package com.fmtch.module_bourse.ui.home.model;


import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.NoticeBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.ui.home.activity.ChooseCoinActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChooseCoinModel {

    private ChooseCoinActivity view;

    public ChooseCoinModel(ChooseCoinActivity view) {
        this.view = view;
    }

    public void getCoinList(int accpunt_type) {
        ApiService apiService = RetrofitManager.getInstance().create(ApiService.class);
        Observable<BaseResponse<ArrayList<AccountBean>>> observable;
        //账户类型(总账户0 币币账户1 法币账户2)
        switch (accpunt_type) {
            case 0:
                //总账户
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
                        if (data == null || data.size() < 1){
                            return;
                        }
                        view.coinList = data;
                        //按字母排序
                        Collections.sort(view.coinList, new AccountBean());
                        view.adapter.setNewData(view.coinList);
                        updateHeaderAndIndexBar();
                        view.coinListCopy.addAll(view.coinList);
                    }
                });
    }

    /**
     * 刷新头部和索引
     */
    public void updateHeaderAndIndexBar() {
        if (view.coinList != null && view.coinList.size() > 0) {
            view.mHeaderList.put(0, view.coinList.get(0).getCoin().getFirstLetter());
            for (int i = 1; i < view.coinList.size(); i++) {
                if (!view.coinList.get(i - 1).getCoin().getFirstLetter().equalsIgnoreCase(view.coinList.get(i).getCoin().getFirstLetter())) {
                    view.mHeaderList.put(i, view.coinList.get(i).getCoin().getFirstLetter());
                }
            }
        }
        view.indexList.clear();
        view.indexList.addAll(view.mHeaderList.values());
        view.indexBar.setNavigators(view.indexList);
    }

}
