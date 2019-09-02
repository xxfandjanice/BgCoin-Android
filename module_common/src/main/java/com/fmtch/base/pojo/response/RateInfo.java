package com.fmtch.base.pojo.response;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RateInfo extends RealmObject {

    private String usdt_to_cny;

    private String btc_to_cny;

    private RealmList<CoinToUSDT> coin_to_usdt_list;

    private RealmList<CoinToBTC> coin_to_btc_list;

    public String getUsdt_to_cny() {
        return usdt_to_cny;
    }

    public void setUsdt_to_cny(String usdt_to_cny) {
        this.usdt_to_cny = usdt_to_cny;
    }

    public String getBtc_to_cny() {
        return btc_to_cny;
    }

    public void setBtc_to_cny(String btc_to_cny) {
        this.btc_to_cny = btc_to_cny;
    }

    public RealmList<CoinToUSDT> getCoin_to_usdt_list() {
        return coin_to_usdt_list;
    }

    public void setCoin_to_usdt_list(RealmList<CoinToUSDT> coin_to_usdt_list) {
        this.coin_to_usdt_list = coin_to_usdt_list;
    }

    public RealmList<CoinToBTC> getCoin_to_btc_list() {
        return coin_to_btc_list;
    }

    public void setCoin_to_btc_list(RealmList<CoinToBTC> coin_to_btc_list) {
        this.coin_to_btc_list = coin_to_btc_list;
    }
}
