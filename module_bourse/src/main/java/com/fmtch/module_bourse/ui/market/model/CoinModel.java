package com.fmtch.module_bourse.ui.market.model;

import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.pojo.MyChooseBean;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.listener.DataChangeListener;
import com.fmtch.module_bourse.listener.DataListener;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorLastPriceDown;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorLastPriceUp;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorLetterDown;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorLetterUp;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorNormal;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorRisefallDown;
import com.fmtch.module_bourse.ui.market.comparator.ComparatorRisefallUp;
import com.fmtch.module_bourse.ui.market.fragment.CoinFragment;
import com.fmtch.module_bourse.ui.market.fragment.MarketFragment;
import com.fmtch.module_bourse.utils.LogUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/20
 */
public class CoinModel {
    private CoinFragment view;
    private DataListener<List<MarketBean>> dataListener;
    private DataChangeListener<MarketBean> dataChangeListener;
    private List<MarketBean> allData = new ArrayList<>();


    public CoinModel(CoinFragment view) {
        this.view = view;
    }

    public DataListener getDataListener(int fragmentType) {
        String coinName = "";
        if (fragmentType == MarketFragment.MARKET_CHOOSE) {
            coinName = view.getString(R.string.optional);
        } else if (fragmentType == MarketFragment.MARKET_USDT) {
            coinName = "USDT";
        } else if (fragmentType == MarketFragment.MARKET_BTC) {
            coinName = "BTC";
        } else if (fragmentType == MarketFragment.MARKET_ETH) {
            coinName = "ETH";
        } else if (fragmentType == MarketFragment.MARKET_ET) {
            coinName = "ET";
        }
        if (TextUtils.isEmpty(coinName)) {
            return null;
        }
        final String finalCoinName = coinName;
        if (dataListener == null) {
            dataListener = new DataListener<List<MarketBean>>() {
                @Override
                public void onSuccess(List<MarketBean> data) {
                    if (data != null) {
                        //根据币种名称筛选数据
                        allData.clear();
                        allData.addAll(data);
                        for (MarketBean marketBean : allData) {
                            String[] split = marketBean.getSymbol().split("/");
                            marketBean.setCoin_name(split[0]);
                            marketBean.setMarket_name(split[1]);
                        }
                        filterData(finalCoinName);
                    }
                }

                @Override
                public void onFail(BaseResponse response) {
                    view.adapter.showErrorPage(response.getMessage());
                }

                @Override
                public void onException(Throwable e) {
                    view.adapter.showErrorPage("");
                }
            };
        }
        return dataListener;
    }

    public DataChangeListener getDataChangeListener() {
        if (dataChangeListener == null) {
            dataChangeListener = new DataChangeListener<MarketBean>() {
                @Override
                public void onDataChange(MarketBean data) {
                    for (MarketBean bean : view.list) {
                        if (bean.getSymbol().equals(data.getSymbol())) {
                            bean.setOpen(data.getOpen());
                            bean.setClose(data.getClose());
                            bean.setHigh(data.getHigh());
                            bean.setLow(data.getLow());
                            bean.setNumber(data.getNumber());
                            bean.setTotal(data.getTotal());
                            view.adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            };
        }
        return dataChangeListener;
    }

    /**
     * 筛选数据
     *
     * @param coinName
     */
    public void filterData(String coinName) {
        view.list.clear();
        if (coinName.equals(view.getString(R.string.optional))) {
            if (view.myChooseAsyncList.size() > 0) {
                for (SymbolBean myChooseBean : view.myChooseAsyncList) {
                    for (MarketBean bean : allData) {
                        if (myChooseBean.getSymbol().equals(bean.getSymbol()) && myChooseBean.isStar()) {
                            bean.setRiseFall(NumberUtils.getRate(bean.getClose(), bean.getOpen()));
                            view.list.add(bean);
                        }
                    }
                }
            }
        } else {
            for (MarketBean bean : allData) {
                if (bean.getMarket_name().equals(coinName)) {
                    bean.setRiseFall(NumberUtils.getRate(bean.getClose(), bean.getOpen()));
                    view.list.add(bean);
                }
            }
        }

        if (view.list.size() == 0) {
            view.adapter.showEmptyPage();
        } else {
            if (view.sortType != MarketFragment.SORT_NORMAL) {
                updateListBySort(view.sortType);
            }
            view.adapter.setNewData(view.list);
        }
    }

    public void updateListBySort(int sortType) {
        if (sortType == MarketFragment.SORT_LETTER_UP) {
            Collections.sort(view.list, new ComparatorLetterUp());
        } else if (sortType == MarketFragment.SORT_LETTER_DOWN) {
            Collections.sort(view.list, new ComparatorLetterDown());
        } else if (sortType == MarketFragment.SORT_LAST_PRICE_UP) {
            Collections.sort(view.list, new ComparatorLastPriceUp());
        } else if (sortType == MarketFragment.SORT_LAST_PRICE_DOWN) {
            Collections.sort(view.list, new ComparatorLastPriceDown());
        } else if (sortType == MarketFragment.SORT_RISE_FALL_UP) {
            Collections.sort(view.list, new ComparatorRisefallUp());
        } else if (sortType == MarketFragment.SORT_RISE_FALL_DOWN) {
            Collections.sort(view.list, new ComparatorRisefallDown());
        } else if (sortType == MarketFragment.SORT_NORMAL) {
            Collections.sort(view.list, new ComparatorNormal());
        }
    }
}
