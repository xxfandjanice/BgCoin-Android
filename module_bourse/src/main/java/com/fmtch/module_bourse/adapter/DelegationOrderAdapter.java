package com.fmtch.module_bourse.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.DelegationOrderBean;

/**
 * Created by wtc on 2019/6/27
 */
public class DelegationOrderAdapter extends BaseQuickPageStateAdapter<DelegationOrderBean, BaseViewHolder> {

    public DelegationOrderAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DelegationOrderBean item) {
        helper.addOnClickListener(R.id.tv_retract);

        helper.setText(R.id.tv_name, item.getSymbol())
                .setText(R.id.tv_delegation_price, NumberUtils.stripMoneyZeros(item.getPrice()))
                .setText(R.id.tv_delegation_amounts, NumberUtils.stripMoneyZeros(item.getNumber()))
                .setText(R.id.tv_traded_amount, NumberUtils.stripMoneyZeros(item.getDeal_number()))
                .setText(R.id.tv_time, item.getCreated_at());

        if (item.getIs_pause() == 1) {
            //暂停
            helper.setBackgroundRes(R.id.tag, R.drawable.shape_tag_stop)
                    .setTextColor(R.id.tv_name, mContext.getResources().getColor(R.color.cl_999999))
                    .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.cl_999999))
                    .setText(R.id.tv_state, R.string.stop)
                    .setTextColor(R.id.tv_delegation_price, mContext.getResources().getColor(R.color.cl_999999))
                    .setTextColor(R.id.tv_delegation_amounts, mContext.getResources().getColor(R.color.cl_999999))
                    .setTextColor(R.id.tv_traded_amount, mContext.getResources().getColor(R.color.cl_999999))
                    .setTextColor(R.id.delegation_price, mContext.getResources().getColor(R.color.cl_C6C6C6))
                    .setTextColor(R.id.delegation_amounts, mContext.getResources().getColor(R.color.cl_C6C6C6))
                    .setTextColor(R.id.tv_traded_amount, mContext.getResources().getColor(R.color.cl_C6C6C6));
            return;
        }

        //买/卖
        helper.setText(R.id.tv_state, R.string.getting_order)
                .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.cl_333333))
                .setTextColor(R.id.tv_delegation_price, mContext.getResources().getColor(R.color.cl_333333))
                .setTextColor(R.id.tv_delegation_amounts, mContext.getResources().getColor(R.color.cl_333333))
                .setTextColor(R.id.tv_traded_amount, mContext.getResources().getColor(R.color.cl_333333))
                .setTextColor(R.id.delegation_price, mContext.getResources().getColor(R.color.cl_999999))
                .setTextColor(R.id.delegation_amounts, mContext.getResources().getColor(R.color.cl_999999))
                .setTextColor(R.id.traded_amount, mContext.getResources().getColor(R.color.cl_999999));
        String side = item.getSide();
        if (side.equals("BUY")) {
            //买
            helper.setBackgroundRes(R.id.tag, R.drawable.shape_tag_buy)
                    .setTextColor(R.id.tv_name, mContext.getResources().getColor(R.color.theme));
        } else if (side.equals("SELL")) {
            //卖
            helper.setBackgroundRes(R.id.tag, R.drawable.shape_tag_sell)
                    .setTextColor(R.id.tv_name, mContext.getResources().getColor(R.color.cl_f55758));
        }
    }
}
