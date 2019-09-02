package com.fmtch.base.utils;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.fmtch.base.app.BaseApplication;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.internal.Utils;

/**
 * 一些常用的字符串类型判断，正则表达式
 * Created by xiaxin on 2018/11/26.
 * Blog : http://blog.csdn.net/u011240877
 *
 * @description Codes there always can be better.
 */
public class StringUtils {
    public final static String UTF_8 = "utf-8";
    private static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";  //正则表达式：验证身份证

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 判断是否为邮箱
     *
     * @param string
     * @return
     */
    public static boolean isEmail(String string) {
        if (TextUtils.isEmpty(string))
            return false;
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
        Matcher m = p.matcher(string);
        return m.matches();
    }

    public static boolean isNumber(String string) {
        if (TextUtils.isEmpty(string))
            return false;
        Pattern p = Pattern.compile("^(\\d+)$");
        Matcher m = p.matcher(string);
        return m.matches();
    }

    /**
     * 判断该字符串是否为中文
     *
     * @param string
     * @return
     */
    public static boolean isChinese(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); i++) {
            n = (int) string.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    //验证邮政编码
    public static boolean checkPost(String post) {
        if (post.matches("[1-9]\\d{5}(?!\\d)")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,6,7,8])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断是否为手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
        if (mobiles.length() == 11)
            return true;
        else
            return false;
    }

    public static boolean isPhoneNum(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true
     */
    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }

    /**
     * 返回一个高亮spannable
     *
     * @param content 文本内容
     * @param color   高亮颜色
     * @param start   起始位置
     * @param end     结束位置
     * @return 高亮spannable
     */
    public static CharSequence getHighLightText(String content, int color, int start, int end) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        start = start >= 0 ? start : 0;
        end = end <= content.length() ? end : content.length();
        SpannableString spannable = new SpannableString(content);
        CharacterStyle span = new ForegroundColorSpan(color);
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    /**
     * 格式化文件大小，不保留末尾的0
     */
    public static String formatFileSize(long len) {
        return formatFileSize(len, false);
    }

    /**
     * 格式化文件大小，保留末尾的0，达到长度一致
     */
    public static String formatFileSize(long len, boolean keepZero) {
        String size;
        DecimalFormat formatKeepTwoZero = new DecimalFormat("#.00");
        DecimalFormat formatKeepOneZero = new DecimalFormat("#.0");
        if (len < 1024) {
            size = String.valueOf(len + "B");
        } else if (len < 10 * 1024) {
            // [0, 10KB)，保留两位小数
            size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
        } else if (len < 100 * 1024) {
            // [10KB, 100KB)，保留一位小数
            size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
        } else if (len < 1024 * 1024) {
            // [100KB, 1MB)，个位四舍五入
            size = String.valueOf(len / 1024) + "KB";
        } else if (len < 10 * 1024 * 1024) {
            // [1MB, 10MB)，保留两位小数
            if (keepZero) {
                size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / 1024 / (float) 100)) + "MB";
            } else {
                size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100) + "MB";
            }
        } else if (len < 100 * 1024 * 1024) {
            // [10MB, 100MB)，保留一位小数
            if (keepZero) {
                size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024 / 1024 / (float) 10)) + "MB";
            } else {
                size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10) + "MB";
            }
        } else if (len < 1024 * 1024 * 1024) {
            // [100MB, 1GB)，个位四舍五入
            size = String.valueOf(len / 1024 / 1024) + "MB";
        } else {
            // [1GB, ...)，保留两位小数
            size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100) + "GB";
        }
        return size;
    }

    public static String getClipString(String address) {
        return address.substring(0, 10) + "..." + address.substring(address.length() - 8);
    }

    /**
     * 获取截获地址字符串
     *
     * @param address
     * @return
     */
    public static String getClipAddressString(String address) {
        return address.substring(0, 6) + "..." + address.substring(address.length() - 4);
    }

    /**
     * 截取以太坊交易Hash
     *
     * @param hash
     * @return
     */
    public static String getEthTxHashClipString(String hash) {
        return hash.substring(0, 6) + "..." + hash.substring(hash.length() - 6);
    }

    public static String getEosTxHashClipString(String hash) {
        return hash.substring(0, 6) + "..." + hash.substring(hash.length() - 4);
    }

    public static String covertPath(String path) {
        String[] strs = path.split("'/");
        StringBuffer sb = new StringBuffer();
        for (int index = 0; index < strs.length; index++) {
            if (index < strs.length - 1) {
                sb.append(strs[index]).append("H");
            }

            if (index == strs.length - 1) {
                sb.append(strs[index]);
            } else {
                sb.append("/");
            }
        }

        return sb.toString();
    }

    public static Boolean checkPwd(String password) {
        String str = "[\\d\\W]*";
        if (password.matches(str)) {
            return true;
        }
        if (password.length() < 6 || password.length() > 20) {
            return true;
        }
        return false;
    }

    /*由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。
     * 但有时需要以字节单位获得字符串的长度。例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。
     * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。
     * 因此，可以编写如下的方法来获得以字节为单位的字符串长度。*/
    public static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;
        }
        return length;

    }

    /*基本原理是将字符串中所有的非标准字符（双字节字符）替换成两个标准字符（**，或其他的也可以）。这样就可以直接例用length方法获得字符串的字节长度了*/
    public static int getWordCountRegex(String s) {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }

    /**
     * 隐藏手机号中间四位
     */
    public static String hiddenMobile(String mobile) {
        if (isEmpty(mobile) || mobile.length() != 11)
            return mobile;
        else
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 隐藏邮箱：只显示@前面的首位和末位
     */
    public static String hiddenEmail(String email) {
        if (!isEmail(email))
            return email;
        else
            return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    /**
     * 截取match之前文本内容
     */
    public static String subBeforeStr(String content, String match) {
        if (TextUtils.isEmpty(content))
            return content;
        return content.substring(0, content.indexOf(match));
    }

    /**
     * 截取match之后文本内容
     */
    public static String subAfterStr(String content, String match) {
        if (TextUtils.isEmpty(content))
            return content;
        return content.substring(content.indexOf(match) + 1);
    }

    /**
     * 添加括号
     */
    public static String addBrackets(String content) {
        return "(" + content + ")";
    }

    /**
     * Android long 类型以毫秒数为单位，例如：将 234736 转化为分钟和秒应为 00:03:55 （包含四舍五入）
     *
     * @param duration 时长
     * @return
     */
    public static String timeMillisecondFormat(long duration){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(duration);
        return hms;
    }


    /**
     * Android long 类型以秒数为单位，例如：将 234736 转化为分钟和秒应为 00:03:55 （包含四舍五入）
     *
     * @param duration 时长
     * @return
     */
    public static String timeSecondFormat(long duration){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(duration * 1000);
        return hms;
    }


    /**
     * Android long 类型以秒数为单位，例如：将 234736 转化为分钟和秒应为 03'55'' （包含四舍五入）
     *
     * @param duration 时长
     * @return
     */
    public static String formatSeconds(long duration){
        long minute = duration / 60;
        long seconds = duration % 60;
        long second = Math.round((float) seconds);
        return minute + "'" + second +  "''";
    }

    /**
     * 设置文本内容样式
     *
     * @param context     上下文
     * @param textView    需要修改的TextView控件
     * @param textContent TextView控件的文本内容
     * @param color       内容的字体颜色
     * @param fontSize    内容的字体大小
     * @param start       起始位置
     * @param end         结束位置
     */
    public static void setTextContentStyle(Context context, TextView textView, String textContent, int color, float fontSize, int start, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(textContent);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new RelativeSizeSpan(fontSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
    }

    /**
     * 设置文本内容样式
     *
     * @param context     上下文
     * @param textView    需要修改的TextView控件
     * @param textContent TextView控件的文本内容
     * @param fontSize    内容的字体大小
     * @param start       起始位置
     * @param end         结束位置
     */
    public static void setTextContentStyle(Context context, TextView textView, String textContent, float fontSize, int start, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(textContent);
        style.setSpan(new RelativeSizeSpan(fontSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
    }

    /**
     * 设置文本内容样式
     *
     * @param context     上下文
     * @param textView    需要修改的TextView控件
     * @param textContent TextView控件的文本内容
     * @param color       内容的字体颜色
     * @param start       起始位置
     * @param end         结束位置
     */
    public static void setTextContentStyle(Context context, TextView textView, String textContent, int color, int start, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(textContent);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
    }

    /**
     * 设置文本内容样式
     *
     * @param context     上下文
     * @param textView    需要修改的TextView控件
     * @param textContent TextView控件的文本内容
     * @param color       内容的字体颜色
     * @param fontSize    内容的字体大小
     * @param start       起始位置
     * @param end         结束位置
     */
    public static void setTextHintStyle(Context context, TextView textView, String textContent, int color, float fontSize, int start, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(textContent);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new RelativeSizeSpan(fontSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setHint(style);
    }

    /**
     * 设置文本内容样式
     *
     * @param context     上下文
     * @param textView    需要修改的TextView控件
     * @param textContent TextView控件的文本内容
     * @param fontSize    内容的字体大小
     * @param start       起始位置
     * @param end         结束位置
     */
    public static void setTextHintStyle(Context context, TextView textView, String textContent, float fontSize, int start, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(textContent);
        style.setSpan(new RelativeSizeSpan(fontSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setHint(style);
    }

    /**
     * 设置文本内容样式
     *
     * @param context     上下文
     * @param textView    需要修改的TextView控件
     * @param textContent TextView控件的文本内容
     * @param color       内容的字体颜色
     * @param start       起始位置
     * @param end         结束位置
     */
    public static void setTextHintStyle(Context context, TextView textView, String textContent, int color, int start, int end) {
        SpannableStringBuilder style = new SpannableStringBuilder(textContent);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setHint(style);
    }

}
