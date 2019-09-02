package com.fmtch.module_bourse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.CoinToUSDT;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.MyPendOrderBean;

import java.math.BigDecimal;

import io.realm.RealmList;

/**
 * Created by wtc on 2019/6/27
 */
public class AccountAdapter extends BaseQuickPageStateAdapter<AccountBean, BaseViewHolder> {

    private RealmList<CoinToBTC> coinToBTCList;
    public RealmList<CoinToUSDT> coinToUSDTList;

    public AccountAdapter(Context context, int layoutResId, RealmList<CoinToBTC> coinToBTCList, RealmList<CoinToUSDT> coinToUSDTList) {
        super(context, layoutResId);
        this.coinToBTCList = coinToBTCList;
        this.coinToUSDTList = coinToUSDTList;
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountBean item) {
        helper.itemView.setVisibility(View.VISIBLE);
        helper.setText(R.id.tv_name, item.getCoin().getName());
        helper.setText(R.id.tv_available, NumberUtils.getFormatMoney(item.getAvailable()));
        helper.setText(R.id.tv_freeze, NumberUtils.getFormatMoney(item.getDisabled()));
        //折合人民币/美元
        if (NumberUtils.isToCNY()) {
            helper.setText(R.id.tv_converter_type, R.string.converter_coin_cny);
            if (coinToBTCList == null) {
                return;
            }
            for (CoinToBTC coinToBTC : coinToBTCList) {
                if (TextUtils.equals(item.getCoin().getName(), coinToBTC.getCoin_name())) {
                    BigDecimal btcDecimal = new BigDecimal(item.getAvailable()).add(new BigDecimal(item.getDisabled())).multiply(new BigDecimal(coinToBTC.getCoin_to_btc()));
                    helper.setText(R.id.tv_converter, NumberUtils.getBTCToMoney(btcDecimal));
                }
            }
        } else {
            helper.setText(R.id.tv_converter_type, R.string.converter_coin_usd);
            if (coinToUSDTList == null) {
                return;
            }
            for (CoinToUSDT coinToUSDT : coinToUSDTList) {
                if (TextUtils.equals(item.getCoin().getName(), coinToUSDT.getCoin_name())) {
                    BigDecimal usdtDecimal = new BigDecimal(item.getAvailable()).add(new BigDecimal(item.getDisabled())).multiply(new BigDecimal(coinToUSDT.getCoin_to_usdt()));
                    helper.setText(R.id.tv_converter, usdtDecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                }
            }
        }
    }

}
