package com.fmtch.base.pojo;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wtc on 2019/5/23
 * 已加入自选的交易对
 */
public class MyChooseBean extends RealmObject implements Serializable {

    private String symbol;  //交易对名称

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
