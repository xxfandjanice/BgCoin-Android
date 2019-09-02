package com.fmtch.base.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.fmtch.base.pojo.response.CoinToUSDT;
import com.fmtch.base.pojo.response.RateInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.realm.Realm;
import io.realm.RealmList;

public class NumberUtils {

    private static RateInfo rateInfo;//汇率转化数据
    private static RealmList<CoinToUSDT> coinToUSDTList;

    private static boolean empty(Object o) {
        return o == null || "".equals(o.toString().trim())
                || "null".equalsIgnoreCase(o.toString().trim())
                || "undefined".equalsIgnoreCase(o.toString().trim());
    }


    //取整
    public static String getIntegerMoney(String money) {
        String str = "0";
        if (!empty(money)) {
            str = new BigDecimal(money).intValue() + "";
        }
        return str;
    }

    //去除小数点后多余0
    public static String stripMoneyZeros(String money) {
        String str = "0";
        if (!empty(money) && Double.parseDouble(money) > 0) {
            str = new BigDecimal(money).stripTrailingZeros().toPlainString();
        }
        return str;
    }

    public static String setScale(String money, int scale) {
        String str = "0";
        if (!empty(money) && Double.parseDouble(money) > 0) {
            str = new BigDecimal(money).setScale(scale, RoundingMode.HALF_DOWN).toPlainString();
        }
        return str;
    }

    public static String setScale(BigDecimal money, int scale) {
        if (money == null || scale < 0) {
            return "0";
        }
        return money.setScale(scale, RoundingMode.HALF_DOWN).toPlainString();
    }

    public static boolean equalsInteger(String text, int i) {
        return TextUtils.equals(text, i + "");
    }

    //转成百分比保留8位有效小数，去末尾0
    public static String getRateToPercent(String rate) {
        String percent = "0%";
        if (!empty(rate) && Double.parseDouble(rate) > 0) {
            percent = new BigDecimal(rate).multiply(new BigDecimal(100)).setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "%";
        }
        return percent;
    }


    //保留有效8位小数
    public static String getFormatMoney(String money) {
        String percent = "0";
        if (!empty(money) && Double.parseDouble(money) > 0) {
            percent = new BigDecimal(money).setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        }
        return percent;
    }

    //转成百(M)或十亿(B)
    public static String formatMoney(String money) {
        String format = "0.00M";
        if (!empty(money) && Double.parseDouble(money) > 0) {
            String cnyOrUsd = money;
            if (isToCNY()) {
                cnyOrUsd = usd2Cny(money, 8);
            }
            BigDecimal divide = new BigDecimal(cnyOrUsd).divide(new BigDecimal("1000000"), BigDecimal.ROUND_DOWN);
            if (divide.compareTo(new BigDecimal("100")) >= 0) {
                BigDecimal divide1 = new BigDecimal(cnyOrUsd).divide(new BigDecimal("1000000000"), BigDecimal.ROUND_DOWN);
                format = divide1.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "B";
            } else {
                format = divide.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "M";
            }
        }
        return format;
    }

    public static String getFormatMoney(String money, int scale) {
        String percent = "0";
        if (!empty(money) && Double.parseDouble(money) > 0) {
            percent = new BigDecimal(money).setScale(scale, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        }
        return percent;
    }

    public static Double getFormatMoney(Double money, int scale) {
        String percent = "0";
        if (money > 0) {
            percent = new BigDecimal(money).setScale(scale, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        }
        return Double.valueOf(percent);
    }


    public static boolean isBigZero(String num) {
        if (!empty(num) && Double.parseDouble(num) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String addBigZero(String num) {
        if (empty(num)) {
            return "0";
        } else {
            String str = new BigDecimal(num).setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            if (isBigZero(num)) {
                return "+" + str;
            } else {
                return str;
            }
        }
    }

    public static String bigDecimal2Percent(BigDecimal num) {
        int i = num.compareTo(BigDecimal.ZERO);
        if (i == 0) {
            return "0.00%";
        } else if (i > 0) {
            return "+" + num + "%";
        } else {
            return num + "%";
        }
    }

    public static String bigDecimal2Percent(String num) {
        if (TextUtils.isEmpty(num)) {
            return "0.00%";
        }
        String percent = new BigDecimal(num).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
        return percent + "%";
    }

    /**
     * 获取开盘收盘幅度
     *
     * @param open
     * @param close
     * @return
     */
    public static BigDecimal getRate(String close, String open) {
        try {
            if (empty(open) || empty(close)) {
                return BigDecimal.ZERO;
            } else {
                BigDecimal closeBigDecimal = new BigDecimal(close);
                BigDecimal openBigDecimal = new BigDecimal(open);

                return closeBigDecimal.subtract(openBigDecimal)
                        .divide(openBigDecimal, 6, BigDecimal.ROUND_DOWN)
                        .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN);
            }
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }


    /**
     * 获取开盘收盘幅度
     *
     * @param open
     * @param close
     * @return
     */
    public static String getBotnRange(String open, String close) {
        try {
            if (empty(open) || empty(close)) {
                return "+ 0%";
            } else {
                BigDecimal openBigDecimal = new BigDecimal(open);
                BigDecimal closeBigDecimal = new BigDecimal(close);
                BigDecimal rate = closeBigDecimal.subtract(openBigDecimal)
                        .divide(openBigDecimal, 6, BigDecimal.ROUND_DOWN)
                        .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN);
                if (rate.doubleValue() >= 0) {
                    return "+" + rate.stripTrailingZeros().toPlainString() + "%";
                } else {
                    return rate.stripTrailingZeros().toPlainString() + "%";
                }
            }
        } catch (ArithmeticException e) {
            return "+ 0%";
        }
    }

    //获取二维数组最大值
    public static String getArrayMax(String[][] arr) {
        double max = 0;
        if (null == arr || arr.length < 1) {
            return String.valueOf(max);
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length - 1; j++) {
                if (max < Double.parseDouble(arr[i][j])) {
                    max = Double.parseDouble(arr[i][j]);//算出最大值
                }
            }
        }
        return String.valueOf(max);
    }

    //获取两组二维数组最大值
    public static String getArrayMax(String[][] arr1, String[][] arr2) {
        double max = 0;
        if (null == arr1 || arr1.length < 1) {
            return String.valueOf(max);
        }
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length - 1; j++) {
                if (max < Double.parseDouble(arr1[i][j])) {
                    max = Double.parseDouble(arr1[i][j]);//算出最大值
                }
            }
        }
        if (null == arr2 || arr2.length < 1) {
            return String.valueOf(max);
        }
        for (int i = 0; i < arr2.length; i++) {
            for (int j = 0; j < arr2[i].length - 1; j++) {
                if (max < Double.parseDouble(arr2[i][j])) {
                    max = Double.parseDouble(arr2[i][j]);//算出最大值
                }
            }
        }
        return String.valueOf(max);
    }

    //获取二维数组最小值
    public static String getArrayMin(String[][] arr) {
        double min = 0;
        if (null == arr || arr.length < 1) {
            return String.valueOf(min);
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length - 1; j++) {
                if (min > Double.parseDouble(arr[i][j])) {
                    min = Double.parseDouble(arr[i][j]);//算出最小值
                }
            }
        }
        return String.valueOf(min);
    }

    //将BTC数量转为人民币或美元(带单位)
    public static String getBTCToMoneyWithUnit(String num) {
        String money = "0.00";
        if (TextUtils.isEmpty(num))
            return money;
        RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        if (rateInfo != null && !TextUtils.isEmpty(rateInfo.getBtc_to_cny()) && !TextUtils.isEmpty(rateInfo.getUsdt_to_cny())) {
            BigDecimal CNYBigdecimal = new BigDecimal(num).multiply(new BigDecimal(rateInfo.getBtc_to_cny()));
            if (isToCNY()) {
                return CNYBigdecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "  CNY";
            } else if (!TextUtils.isEmpty(rateInfo.getUsdt_to_cny()) && new BigDecimal(rateInfo.getUsdt_to_cny()).compareTo(BigDecimal.ZERO) > 0) {
                return CNYBigdecimal.divide(new BigDecimal(rateInfo.getUsdt_to_cny()), 2, BigDecimal.ROUND_DOWN).toPlainString() + "  USD";
            }
        }
        return money;
    }

    //将BTC数量转为人民币或美元
    public static String getBTCToMoney(String num) {
        String money = "0.00";
        if (TextUtils.isEmpty(num))
            return money;
        RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        if (rateInfo != null && !TextUtils.isEmpty(rateInfo.getBtc_to_cny()) && !TextUtils.isEmpty(rateInfo.getUsdt_to_cny())) {
            BigDecimal CNYBigdecimal = new BigDecimal(num).multiply(new BigDecimal(rateInfo.getBtc_to_cny()));
            if (isToCNY()) {
                return CNYBigdecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
            } else if (!TextUtils.isEmpty(rateInfo.getUsdt_to_cny()) && new BigDecimal(rateInfo.getUsdt_to_cny()).compareTo(BigDecimal.ZERO) > 0) {
                return CNYBigdecimal.divide(new BigDecimal(rateInfo.getUsdt_to_cny()), 2, BigDecimal.ROUND_DOWN).toPlainString();
            }
        }
        return money;
    }

    //将BTC数量转为人民币或美元(带单位)
    public static String getBTCToMoneyWithUnit(BigDecimal num) {
        String money = "0.00";
        if (num == null)
            return money;
        RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        if (rateInfo != null && !TextUtils.isEmpty(rateInfo.getBtc_to_cny()) && !TextUtils.isEmpty(rateInfo.getUsdt_to_cny())) {
            BigDecimal CNYBigdecimal = num.multiply(new BigDecimal(rateInfo.getBtc_to_cny()));
            if (isToCNY()) {
                return CNYBigdecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "  CNY";
            } else if (!TextUtils.isEmpty(rateInfo.getUsdt_to_cny()) && new BigDecimal(rateInfo.getUsdt_to_cny()).compareTo(BigDecimal.ZERO) > 0) {
                return CNYBigdecimal.divide(new BigDecimal(rateInfo.getUsdt_to_cny()), 2, BigDecimal.ROUND_DOWN).toPlainString() + "  USD";
            }
        }
        return money;
    }

    //将BTC数量转为人民币或美元
    public static String getBTCToMoney(BigDecimal num) {
        String money = "0.00";
        if (num == null)
            return money;
        RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        if (rateInfo != null && !TextUtils.isEmpty(rateInfo.getBtc_to_cny()) && !TextUtils.isEmpty(rateInfo.getUsdt_to_cny())) {
            BigDecimal CNYBigdecimal = num.multiply(new BigDecimal(rateInfo.getBtc_to_cny()));
            if (isToCNY()) {
                return CNYBigdecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
            } else if (!TextUtils.isEmpty(rateInfo.getUsdt_to_cny()) && new BigDecimal(rateInfo.getUsdt_to_cny()).compareTo(BigDecimal.ZERO) > 0) {
                return CNYBigdecimal.divide(new BigDecimal(rateInfo.getUsdt_to_cny()), 2, BigDecimal.ROUND_DOWN).toPlainString();
            }
        }
        return money;
    }

    //是否转化成人民币（否则是转成美元）
    public static boolean isToCNY() {
        return TextUtils.equals((String) SpUtils.get(KeyConstant.KEY_PREF_CURRENCY_UNIT, "CNY"), "CNY");
    }

    //将不同的币专程人民币或美元
    public static String transform2CnyOrUsd(String marketName, String close) {
        if (TextUtils.isEmpty(marketName) || TextUtils.isEmpty(close)) {
            return "";
        }
        if (rateInfo == null) {
            rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        }
        if (rateInfo != null && coinToUSDTList == null) {
            coinToUSDTList = rateInfo.getCoin_to_usdt_list();
        }

        BigDecimal usdtBigdecimal = BigDecimal.ZERO;
        if (coinToUSDTList == null || coinToUSDTList.size() == 0) {
            return "";
        }
        for (CoinToUSDT coinToUSDT : coinToUSDTList) {
            if (TextUtils.equals(marketName, coinToUSDT.getCoin_name())) {
                usdtBigdecimal = new BigDecimal(coinToUSDT.getCoin_to_usdt());
                break;
            }
        }
        usdtBigdecimal = usdtBigdecimal.multiply(new BigDecimal(close));
        if (isToCNY()) {
            return String.format("%s CNY", String.format("≈ %s", usdtBigdecimal.multiply(new BigDecimal(rateInfo.getUsdt_to_cny())).setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
        } else {
            return String.format("%s USD", String.format("≈ %s", usdtBigdecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
        }
    }

    //将不同的币专程人民币或美元
    public static String transform2CnyOrUsdSymbol(String market_name, String close) {
        if (TextUtils.isEmpty(market_name) || TextUtils.isEmpty(close)) {
            return "";
        }
        if (rateInfo == null) {
            rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        }
        if (rateInfo != null && coinToUSDTList == null) {
            coinToUSDTList = rateInfo.getCoin_to_usdt_list();
        }

        BigDecimal usdtBigdecimal = BigDecimal.ZERO;
        if (coinToUSDTList == null || coinToUSDTList.size() == 0) {
            return "";
        }
        for (CoinToUSDT coinToUSDT : coinToUSDTList) {
            if (TextUtils.equals(market_name, coinToUSDT.getCoin_name())) {
                usdtBigdecimal = new BigDecimal(coinToUSDT.getCoin_to_usdt());
                break;
            }
        }
        usdtBigdecimal = usdtBigdecimal.multiply(new BigDecimal(close));
        if (isToCNY()) {
            return "¥ " + usdtBigdecimal.multiply(new BigDecimal(rateInfo.getUsdt_to_cny())).setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
        } else {
            return "$ " + usdtBigdecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
        }
    }


    //将不同的币专程人民币或美元
    public static String transform2CnyOrUsdSymbol(String market_name, String close, int scale) {
        if (TextUtils.isEmpty(market_name) || TextUtils.isEmpty(close)) {
            return "";
        }
        if (rateInfo == null) {
            rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        }
        if (rateInfo != null && coinToUSDTList == null) {
            coinToUSDTList = rateInfo.getCoin_to_usdt_list();
        }

        BigDecimal usdtBigdecimal = BigDecimal.ZERO;
        if (coinToUSDTList == null || coinToUSDTList.size() == 0) {
            return "";
        }
        for (CoinToUSDT coinToUSDT : coinToUSDTList) {
            if (TextUtils.equals(market_name, coinToUSDT.getCoin_name())) {
                usdtBigdecimal = new BigDecimal(coinToUSDT.getCoin_to_usdt());
                break;
            }
        }
        usdtBigdecimal = usdtBigdecimal.multiply(new BigDecimal(close));
        if (isToCNY()) {
            return usdtBigdecimal.multiply(new BigDecimal(rateInfo.getUsdt_to_cny())).setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
        } else {
            return usdtBigdecimal.setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
        }
    }

    /**
     * 将USD转CNY
     *
     * @param usdStr
     * @return
     */
    public static String usd2Cny(String usdStr, int scale) {
        if (TextUtils.isEmpty(usdStr)) {
            return "";
        }
        if (rateInfo == null) {
            rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        }

        return new BigDecimal(usdStr).multiply(new BigDecimal(rateInfo.getUsdt_to_cny())).setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static String setNormalNumber(String num) {
        if (TextUtils.isEmpty(num)) {
            return "";
        }
        String firstChar = num.substring(0, 1);
        if (firstChar.equals(".")) {
            return "0" + num;
        }
        return num;
    }

    public static Editable setInputScale(Editable e, int scale) {
        if (e == null || TextUtils.isEmpty(e.toString()) || scale < 0) {
            return e;
        }
        String temp = e.toString();
        int posDot = temp.indexOf(".");
        if (temp.indexOf(".") > 0 && temp.length() - posDot - 1 > scale) {
            e.delete(posDot + scale + 1, temp.length());
        }
        return e;
    }
}
