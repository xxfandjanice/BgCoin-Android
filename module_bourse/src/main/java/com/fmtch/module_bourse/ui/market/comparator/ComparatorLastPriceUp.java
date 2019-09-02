package com.fmtch.module_bourse.ui.market.comparator;

import com.fmtch.module_bourse.bean.MarketBean;

import java.util.Comparator;

/**
 * Created by wtc on 2019/5/20
 */
public class ComparatorLastPriceUp implements Comparator<MarketBean> {

    @Override
    public int compare(MarketBean o1, MarketBean o2) {
        Double close = Double.valueOf(o1.getClose());
        Double close2 = Double.valueOf(o2.getClose());
        return close2.compareTo(close);
    }
}
