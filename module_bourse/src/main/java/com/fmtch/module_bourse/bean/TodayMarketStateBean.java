package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/5/17
 */
public class TodayMarketStateBean {

    /**
     * rise_percent : 100%
     * fall_percent : 0%
     */

    private String rise_percent;
    private String fall_percent;
    private int status;  //0-没有预测过 1-预测了看多  2-预测了看空

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRise_percent() {
        return rise_percent;
    }

    public void setRise_percent(String rise_percent) {
        this.rise_percent = rise_percent;
    }

    public String getFall_percent() {
        return fall_percent;
    }

    public void setFall_percent(String fall_percent) {
        this.fall_percent = fall_percent;
    }
}
