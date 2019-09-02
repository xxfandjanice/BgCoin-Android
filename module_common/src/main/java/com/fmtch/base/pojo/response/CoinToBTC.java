package com.fmtch.base.pojo.response;

import io.realm.RealmObject;

public class CoinToBTC extends RealmObject {

    private String coin_name;

    private String coin_to_btc;

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getCoin_to_btc() {
        return coin_to_btc;
    }

    public void setCoin_to_btc(String coin_to_btc) {
        this.coin_to_btc = coin_to_btc;
    }

}
