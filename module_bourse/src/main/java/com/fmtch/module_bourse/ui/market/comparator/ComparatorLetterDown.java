package com.fmtch.module_bourse.ui.market.comparator;

import com.fmtch.module_bourse.bean.MarketBean;

import java.util.Comparator;

/**
 * Created by wtc on 2019/5/20
 */
public class ComparatorLetterDown implements Comparator<MarketBean> {

    @Override
    public int compare(MarketBean o1, MarketBean o2) {
        return o2.getCoin_name().compareTo(o1.getCoin_name());
    }
}
