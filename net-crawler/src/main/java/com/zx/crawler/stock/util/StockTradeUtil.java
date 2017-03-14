package com.zx.crawler.stock.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

public class StockTradeUtil {
    private static Logger LOG = LoggerFactory.getLogger(StockTradeUtil.class);
    private static final String LEFT_BRACKET = "(";
    private static final String LEFT_SPACE_BRACKET = "（";
    private static final String LEFT_FULL_BRACKET = "【";
    private static final String LEFT_MID_BRACKET = "[";
    private static final String POST_CODE = "邮政编码";
    private static final String POST = "邮编";
    private static final String WE_CHAT = "微信";
    private static final String ALIPAY = "支付宝";
    private static final String YEAR = "年";
    private static final String MONTH = "月";
    private static final String DAY = "日";
    private static final int BANK_CARD_MIN_LENGTH = 16;
    private static final int BANK_CARD_MAX_LENGTH = 20;
    private static final int TELPHONE_LENGTH_ONE = 7;
    private static final int TELPHONE_LENGTH_TWO = 8;

    public static String getName(String name) {
        if (name.contains(LEFT_BRACKET)) {
            name = name.substring(0, name.indexOf(LEFT_BRACKET));
        } else if (name.contains(LEFT_SPACE_BRACKET)) {
            name = name.substring(0, name.indexOf(LEFT_SPACE_BRACKET));
        } else if (name.contains(LEFT_MID_BRACKET)) {
            name = name.substring(0, name.indexOf(LEFT_MID_BRACKET));
        } else if (name.contains(LEFT_FULL_BRACKET)) {
            name = name.substring(0, name.indexOf(LEFT_FULL_BRACKET));
        } else {
            return name;
        }
        return name;
    }

    public static String getAddress(String address) {
        if (address.contains(POST_CODE)) {
            address = address.substring(0, address.indexOf(POST_CODE));
        } else if (address.contains(POST)) {
            address = address.substring(0, address.indexOf(POST));
        } else {
            return address;
        }
        return address;
    }

    public static String getQQ(String qq) {
        int index = 0;
        for (int i = 0; i < qq.length(); i++) {
            if (!Character.isDigit(qq.charAt(i))) {
                qq = qq.substring(0, i);
                return qq;
            }
        }
        return qq;
    }

    public static String getDepositBank(String depositMassage) {
        if (depositMassage.contains(WE_CHAT)) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf(WE_CHAT));
        } else if (depositMassage.contains("qq")) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf("qq"));
        } else if (depositMassage.contains("QQ")) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf("QQ"));
        } else if (depositMassage.contains(ALIPAY)) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf(ALIPAY));
        } else {
            return depositMassage;
        }
        return depositMassage;
    }

    public static List<String> getBankType(String msg, List<String> cardNumber) {
        List<String> bankTypes = new ArrayList<String>();
        if (cardNumber.size() == 0) {
            return bankTypes;
        }
        String temp[] = RightSplitUtil.cowMassage(msg);
        boolean flag = false;
        for (int i = 0; i < cardNumber.size(); i++) {
            for (int k = 0; k < temp.length; k++) {
                if (temp[k].contains(cardNumber.get(i))) {
                    int index = temp[k].indexOf(cardNumber.get(i));
                    if (index == 0) {
                        bankTypes.add("");
                    } else {
                        bankTypes.add(removeSplitSign(temp[k].substring(0, index)));
                    }
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                bankTypes.add("");
            }
            flag = false;
        }
        return bankTypes;
    }

    public static String removeSplitSign(String temp) {
        if (temp.charAt(temp.length() - 1) == ':' || temp.charAt(temp.length() - 1) == '：'
                || temp.charAt(temp.length() - 1) == ';' || temp.charAt(temp.length() - 1) == '；') {
            return temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    public static List<String> getTelphone(String rightText) {
        Pattern p = Pattern.compile("\\d{2,}");
        Matcher m = p.matcher(rightText);
        List<String> temp = new ArrayList<String>();
        List<String> telphone = new ArrayList<String>();
        while (m.find()) {
            temp.add(m.group());
        }
        for (int j = 0; j < temp.size(); j++) {
            if (temp.get(j).length() == TELPHONE_LENGTH_ONE || temp.get(j).length() == TELPHONE_LENGTH_TWO) {
                int index = rightText.indexOf(temp.get(j));
                if (rightText.charAt(index - 1) == '-') {
                    telphone.add(temp.get(j - 1) + "-" + temp.get(j));
                }
                if (rightText.charAt(index - 1) == '—') {
                    telphone.add(temp.get(j - 1) + '—' + temp.get(j));
                }
            }
            if (temp.get(j).length() == 11) {
                telphone.add(temp.get(j));
            }
        }
        return telphone;
    }

    public static List<String> getCardNumber(String rightText) {
        rightText = StringUtils.replaceEach(rightText, new String[] { " ", " ", "\u00A0", ",", "，", ";", "；" },
                new String[] { "", "", "", "", "", "", "" }).replace("\n", "");
        List<String> temp = new ArrayList<String>();
        List<String> cardNumber = new ArrayList<String>();
        Pattern pt = Pattern.compile("\\d{15,}");
        Matcher mt = pt.matcher(rightText);
        while (mt.find()) {
            temp.add(mt.group());
        }
        for (int n = 0; n < temp.size(); n++) {
            if (temp.get(n).length() >= BANK_CARD_MIN_LENGTH && temp.get(n).length() <= BANK_CARD_MAX_LENGTH) {
                int index1 = rightText.indexOf(temp.get(n));
                if (rightText.charAt(index1 - 1) == '-' || rightText.charAt(index1 - 1) == '—') {
                    continue;
                } else {
                    cardNumber.add(temp.get(n));
                }
            }
        }
        return cardNumber;
    }

    public static boolean isCompare(List<String> list, String temp) {
        Set<String> set = new HashSet<String>(list);
        if (set.contains(temp)) {
            return true;
        }
        return false;
    }

    public static boolean isAllDigit(String temp) {
        for (int i = 0; i < temp.length(); i++) {
            if (!Character.isDigit(temp.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static Date dateFormatter(String temp) {
        String strDate = "";
        if (temp.contains("：") || temp.contains(":")) {
            if (temp.contains(YEAR) && temp.contains(MONTH) && temp.contains(DAY)) {
                strDate = temp.split("：")[1].replace(YEAR, "-").replace(MONTH, "-");
                strDate = strDate.substring(0, strDate.length() - 1);
            } else {
                strDate = null;
            }
        } else {
            strDate = null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = null;
            if (strDate != null) {
                date = format.parse(strDate);
            }
            return date;
        } catch (Exception e) {
            LOG.warn("regist date exception", e);
        }
        return null;
    }

    public static String getSplitMassage(String temp) {
        Iterable<String> iterable = Splitter.on(CharMatcher.anyOf(":：；;")).split(temp);
        Iterator<String> iterator = iterable.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return "";
    }
}
