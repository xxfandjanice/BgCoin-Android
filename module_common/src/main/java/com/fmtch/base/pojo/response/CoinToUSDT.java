package com.fmtch.base.pojo.response;

import io.realm.RealmObject;

public class CoinToUSDT extends RealmObject {

    private String coin_name;

    private String coin_to_usdt;

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getCoin_to_usdt() {
        return coin_to_usdt;
    }

    public void setCoin_to_usdt(String coin_to_usdt) {
        this.coin_to_usdt = coin_to_usdt;
    }

}
