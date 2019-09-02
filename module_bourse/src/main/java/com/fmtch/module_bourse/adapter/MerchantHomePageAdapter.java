package com.fmtch.module_bourse.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.DelegationOrderBean;
import com.fmtch.module_bourse.bean.MerchantHomePageBean;

/**
 * Created by wtc on 2019/6/27
 */
public class MerchantHomePageAdapter extends BaseQuickPageStateAdapter<MerchantHomePageBean.BuyOrSellBean, BaseViewHolder> {

    public MerchantHomePageAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MerchantHomePageBean.BuyOrSellBean item) {
        String type = item.getSide();
        helper.setText(R.id.tv_type, type.equals("SELL") ? R.string.to_ta_buy : R.string.to_ta_sell);
        helper.setBackgroundRes(R.id.tag, type.equals("SELL") ? R.drawable.shape_tag_buy : R.drawable.shape_tag_sell);
        helper.setTextColor(R.id.tv_name, type.equals("SELL") ? mContext.getResources().getColor(R.color.theme) : mContext.getResources().getColor(R.color.cl_f55758))
                .setText(R.id.tv_name, item.getSymbol());
        helper.setText(R.id.tv_limit_money, item.getMin_cny() + " - " + item.getMax_cny() + " CNY");
        helper.setText(R.id.tv_amount, item.getNumber() + " " + item.getSymbol().split("/")[0]);
        helper.setText(R.id.tv_price, item.getPrice());

        helper.setGone(R.id.iv_bank, item.getBank() == 1)
                .setGone(R.id.iv_wechat, item.getWechat() == 1)
                .setGone(R.id.iv_zfb, item.getAlipay() == 1);


        int position = helper.getAdapterPosition();
        if (position == 0) {
            helper.setVisible(R.id.tv_type, true);
        } else {
            MerchantHomePageBean.BuyOrSellBean lastItem = getData().get(position - 1);
            if (item.getSide().equals(lastItem.getSide())) {
                helper.setGone(R.id.tv_type, false);
            } else {
                helper.setGone(R.id.tv_type, true);
            }
        }
        if (position + 1 <=  getData().size() - 1) {
            MerchantHomePageBean.BuyOrSellBean NextItem =  getData().get(position + 1);
            if (item.getSide().equals(NextItem.getSide())) {
                helper.setGone(R.id.divider, true);
            } else {
                helper.setGone(R.id.divider, false);
            }
        }
    }
}
