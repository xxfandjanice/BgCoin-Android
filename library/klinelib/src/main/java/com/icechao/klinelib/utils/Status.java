package com.icechao.klinelib.utils;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : Status.java
 * @Author       : chao
 * @Date         : 2019-04-26
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class Status {

    public enum MainStatus {
        MA, BOLL, NONE
    }

    public enum CrossTouchModel {
        FOLLOW_FINGERS(false),//跟随手指移动
        SHOW_CLOSE(true); //y值显示当前价格

        private boolean state;

        CrossTouchModel(boolean state) {
            this.state = state;
        }

        public boolean getStateValue() {
            return state;
        }

    }

    public enum ChildStatus {
        MAIN_ONLY(0), //显示主图交易量时隐藏交易量
        MAIN_VOL(1), //显示主图时显示交易量
        MAIN_INDEX(2), //显示主图交易量指标图时隐藏交易量
        MAIN_VOL_INDEX(3);//显示主图指标图时显示交易量

        ChildStatus(int statu) {
            this.statu = statu;
        }

        private int statu;

        public int getStatu() {
            return statu;
        }
    }

    public enum IndexStatus {
        NONE(-1), MACD(0), KDJ(1), RSI(2), WR(3);
        private int statu;

        IndexStatus(int statu) {
            this.statu = statu;
        }

        public int getStatu() {
            return statu;
        }

    }

    public enum VolChartStatus {
        BAR_CHART(true),//柱状图
        LINE_CHART(false); //线状图

        private boolean statu;

        VolChartStatus(boolean statu) {
            this.statu = statu;
        }

        public boolean showLine() {
            return statu;
        }
    }

    public enum KlineStatus {
        TIME_LINE(true),//分时线
        K_LINE(false); //K线

        private boolean statu;

        KlineStatus(boolean statu) {
            this.statu = statu;
        }

        public boolean showLine() {
            return statu;
        }
    }

    public enum ShowCrossModel {
        SELECT_TOUCHE(0),//touch model
        SELECT_PRESS(1),//long press model
        SELECT_BOTH(2),//both model
        SELECT_NONE(3);// can't touch
        private int model;

        ShowCrossModel(int model) {
            this.model = model;
        }
    }
}


