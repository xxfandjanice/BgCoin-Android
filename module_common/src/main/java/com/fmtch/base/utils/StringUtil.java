package com.fmtch.base.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wang on 2016/3/30.
 */
public class StringUtil {

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0 || cs.equals("null")) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !TextUtils.isEmpty(cs);
    }

    /**
     * @param str1
     * @param str2
     * @return all blank or all not blank && equals
     */
    public static boolean isSame(String str1, String str2) {
        if (TextUtils.isEmpty(str1) && TextUtils.isEmpty(str2)) {
            return true;
        }
        return !StringUtils.isEmpty(str1) && !StringUtils.isEmpty(str2)
                && str1.equals(str2);
    }

    /**
     * email address
     *
     * @param str
     * @return true or false
     */
    public static boolean isEmailAddress(String str) {
        Pattern pattern = Pattern
                .compile("^([0-9a-zA-Z\\._-])+@([0-9a-zA-Z]+\\.)+([a-zA-Z])+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * digital or char
     *
     * @param str
     * @return true or false
     */
    public static boolean isDigitChar(String str) {
        Pattern pattern = Pattern.compile("([0-9a-zA-Z])+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * remove space char
     *
     * @param str
     * @return
     */
    public static String trimInnerSpaceStr(String str) {
        str = str.trim();
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1).trim();
        }

        return str;
    }

    public static String toMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String formatCount(int count) {
        if (count >= 1000) {
            return "999+";
        }
        return count + "";
    }

    public static String byUser(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return "";
        }
        return "BY " + userName;
    }

    public static String getFormatDurationSize(int duration) {
        if (duration > 60) {
            int minute = duration / 60;
            return minute + "\'" + duration % 60 + "\'\'";
        } else {
            return duration + "\'\'";
        }
    }

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isS3ID(String id) {
        if (!TextUtils.isEmpty(id)) {
            if (id.length() > 13 && !StringUtils.isNumber(id)) {
                return true;
            }
        }
        return false;
    }

    public static String listToString(List list, String separator) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    public static String imgCount(int count) {
        return count + "+";
    }

    public static String getFormatValueString(int value) {
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(value);
    }


    public static String getHighlighterString(String text) {
        return "<font color='#476b9d'>" + text + "</font>";
    }

    /**
     * 按照指定字节长度截取字符串，防止中文被截成一半的问题
     *
     * @param s      源字符串
     * @param length 截取的字节数
     * @return 截取后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String substring(String s, int length) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++) {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1) {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0) {
                i = i - 1;
            }
            // 该UCS2字符是字母或数字，则保留该字符
            else {
                i = i + 1;
            }
        }
        return new String(bytes, 0, i, "Unicode");
    }

    /**
     * 计算采用utf-8编码方式时字符串所占字节数
     *
     * @param content
     * @return
     */
    public static int getByteSize(String content) {
        int size = 0;
        if (null != content) {
            try {
                // 汉字采用utf-8编码时占3个字节
                size = content.getBytes("Unicode").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * 格式化
     *
     * @param jsonStr
     * @return
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }


}
