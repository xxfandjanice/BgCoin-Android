package com.fmtch.module_bourse.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.DeepTransformBean;

/**
 * Created by wtc on 2019/6/27
 */
public class DeepAdapter extends BaseQuickPageStateAdapter<DeepTransformBean, BaseViewHolder> {


    public DeepAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeepTransformBean item) {
        //买入
        if (TextUtils.isEmpty(item.getBuyAmount())) {
            helper.setText(R.id.tv_buy, "");
            helper.setText(R.id.tv_buy_amount, "");
            helper.setText(R.id.tv_buy_price, "");
            helper.setProgress(R.id.pb_buy, 100);
        } else {
            helper.setText(R.id.tv_buy, helper.getAdapterPosition() + 1 + "");
            helper.setText(R.id.tv_buy_amount, item.getBuyAmount());
            helper.setText(R.id.tv_buy_price, item.getBuyPrice());
            helper.setProgress(R.id.pb_buy, item.getBuyPercent());
        }
        //卖出
        if (TextUtils.isEmpty(item.getSellAmount())) {
            helper.setText(R.id.tv_sell, "");
            helper.setText(R.id.tv_sell_amount, "");
            helper.setText(R.id.tv_sell_price, "");
            helper.setProgress(R.id.pb_sell, 0);
        } else {
            helper.setText(R.id.tv_sell, helper.getAdapterPosition() + 1 + "");
            helper.setText(R.id.tv_sell_amount, item.getSellAmount());
            helper.setText(R.id.tv_sell_price, item.getSellPrice());
            helper.setProgress(R.id.pb_sell, item.getSellPercent());
        }

    }

}
