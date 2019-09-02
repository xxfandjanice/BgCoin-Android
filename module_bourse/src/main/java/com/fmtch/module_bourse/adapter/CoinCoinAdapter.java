package com.fmtch.module_bourse.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.DelegationOrderBean;
import com.fmtch.module_bourse.bean.MyPendOrderBean;

import java.util.List;

/**
 * Created by wtc on 2019/6/27
 */
public class CoinCoinAdapter extends BaseQuickPageStateAdapter<MyPendOrderBean, BaseViewHolder> {

    public CoinCoinAdapter(Context context, int layoutResId, List<MyPendOrderBean> list) {
        super(context, layoutResId,list);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyPendOrderBean item) {
        helper.setText(R.id.tv_state, item.getSide().equals("BUY") ? R.string.buy : R.string.sell);
        helper.setBackgroundRes(R.id.tv_state, item.getSide().equals("BUY") ? R.drawable.bg_circle_green : R.drawable.bg_circle_red);
        helper.setText(R.id.tv_name, item.getSymbol());
        String created_at = item.getCreated_at();
        String[] time = created_at.split(" ");
        helper.setText(R.id.tv_time, time[1]);
        helper.setText(R.id.tv_delegation_price, NumberUtils.stripMoneyZeros(item.getPrice()));
        helper.setText(R.id.tv_trade_amounts, NumberUtils.stripMoneyZeros(item.getDeal_number()));
        helper.setText(R.id.tv_delegation_amounts, NumberUtils.stripMoneyZeros(item.getNumber()));
        helper.addOnClickListener(R.id.tv_revoke);
    }
}
