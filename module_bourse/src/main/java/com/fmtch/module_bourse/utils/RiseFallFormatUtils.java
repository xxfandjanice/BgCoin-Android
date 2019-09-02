package com.fmtch.module_bourse.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * Created by wtc on 2019/5/20
 */
public class RiseFallFormatUtils {

    /**
     * 计算涨跌幅
     *
     * @param closeStr 收盘价
     * @param openStr  开盘价
     * @param decimal  保留小数位
     * @return
     */
    public static BigDecimal format(String closeStr, String openStr, int decimal) {
        if (TextUtils.isEmpty(closeStr) || TextUtils.isEmpty(openStr)) {
            return BigDecimal.ZERO;
        }
        BigDecimal open = new BigDecimal(openStr);
        BigDecimal close = new BigDecimal(closeStr);
        BigDecimal riseFall;
        //涨跌幅 = （收盘价 - 开盘价） / 开盘价 = (close - open) / open
        if (open.compareTo(BigDecimal.ZERO) == 0) {
            riseFall = close;
        } else {
            riseFall = (close.subtract(open)).divide(open, decimal, BigDecimal.ROUND_HALF_UP);
        }

        return riseFall;
    }

    public static String formatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String[] s = time.split(" ");
        String[] date = s[0].split("-");
        String[] hour = s[1].split(":");

        return hour[0] + ":" + hour[1] + "  " + date[1] + "/" + date[2];
    }

    public static String formatTime2(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String[] s = time.split(" ");
        String[] date = s[0].split("-");
        String[] hour = s[1].split(":");

        return date[1] + "/" + date[2] + "  " + hour[0] + ":" + hour[1];
    }
}
