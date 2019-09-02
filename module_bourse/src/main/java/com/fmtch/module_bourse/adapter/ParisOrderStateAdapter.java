package com.fmtch.module_bourse.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.ParisOrderStateBean;
import com.fmtch.module_bourse.utils.TimeUtils;

import java.math.BigDecimal;

/**
 * Created by wtc on 2019/6/27
 */
public class ParisOrderStateAdapter extends BaseQuickPageStateAdapter<ParisOrderStateBean, BaseViewHolder> {

    public ParisOrderStateAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ParisOrderStateBean item) {
        helper.setGone(R.id.tv_remaining_time, false)
                .setGone(R.id.ll_real_out, false)
                .setGone(R.id.ll_service_fee, false);
        helper.setText(R.id.tv_name, item.getSymbol());
        helper.setText(R.id.tv_price, NumberUtils.stripMoneyZeros(item.getPrice()));
        helper.setText(R.id.tv_amount, NumberUtils.stripMoneyZeros(item.getNumber()));
        helper.setText(R.id.tv_total_money, new BigDecimal(item.getPrice()).multiply(new BigDecimal(item.getNumber())).stripTrailingZeros().toPlainString());
        helper.setText(R.id.tv_time, item.getCreated_at());

        if (item.getSide().equals("BUY")) {
            //买入
            helper.setBackgroundRes(R.id.tag, R.drawable.shape_tag_buy);
            helper.setText(R.id.tv_buy_sell, R.string.buy_in)
                    .setTextColor(R.id.tv_buy_sell, mContext.getResources().getColor(R.color.theme));
        } else if (item.getSide().equals("SELL")) {
            //卖出
            helper.setBackgroundRes(R.id.tag, R.drawable.shape_tag_sell);
            helper.setText(R.id.tv_buy_sell, R.string.sell_out)
                    .setTextColor(R.id.tv_buy_sell, mContext.getResources().getColor(R.color.cl_f55758));
        }
        //0-已取消，1-待付款，2-已付款，3-冻结，4-已完成
        int status = item.getStatus();
        if (status == 1 || status == 2) {
            //未完成
            String timeSpan = TimeUtils.getTimeSpanFormat(item.getRemainingTime());
            helper.setText(R.id.tv_remaining_time, String.format(mContext.getResources().getString(R.string.the_remaining_time), timeSpan))
                    .setGone(R.id.tv_remaining_time, true);
            if (item.getSide().equals("BUY")) {
                if (item.getStatus() == 1) {  //待付款
                    helper.setText(R.id.tv_state, R.string.wait_pay)
                            .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.cl_fa8006));
                } else if (item.getStatus() == 2) { //已付款
                    helper.setText(R.id.tv_state, R.string.wait_other_side_release_coin)
                            .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.theme));
                }
            } else if (item.getSide().equals("SELL")) {
                if (item.getStatus() == 1) {  //待付款
                    helper.setText(R.id.tv_state, R.string.wait_other_side_pay)
                            .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.cl_fa8006));
                } else if (item.getStatus() == 2) { //已付款
                    helper.setText(R.id.tv_state, R.string.wait_release_coin)
                            .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.theme));
                }
            }
        } else if (status == 4) {
            //已完成
            helper.setText(R.id.tv_state, R.string.deal_done)
                    .setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.cl_666666));
            helper.setGone(R.id.ll_real_out, item.getSide().equals("SELL"))
                    .setText(R.id.tv_real_out, new BigDecimal(item.getNumber()).add(new BigDecimal(item.getFee())).stripTrailingZeros().toPlainString());
            helper.setGone(R.id.ll_service_fee, item.getSide().equals("SELL"))
                    .setText(R.id.tv_service_fee, NumberUtils.stripMoneyZeros(item.getFee()));
        } else if (status == 0) {
            //已取消
            // 1-主动取消，2-超时未支付取消，3-管理员取消
            int cancel_type = item.getCancel_type();
            helper.setTextColor(R.id.tv_state, mContext.getResources().getColor(R.color.cl_666666));
            if (cancel_type == 1) {
                helper.setText(R.id.tv_state, R.string.buyer_cancel);
            } else if (cancel_type == 2) {
                helper.setText(R.id.tv_state, R.string.time_out_un_pay_cancel);
            } else if (cancel_type == 3) {
                helper.setText(R.id.tv_state, R.string.seller_cancel);
            }
        }
    }
}
