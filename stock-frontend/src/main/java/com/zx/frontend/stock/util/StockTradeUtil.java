package com.zx.frontend.stock.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StockTradeUtil {
    private static Logger LOG = LoggerFactory.getLogger(StockTradeUtil.class);

    public static String getName(String name) {
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("("));
        } else if (name.contains("（")) {
            name = name.substring(0, name.indexOf("（"));
        } else if (name.contains("[")) {
            name = name.substring(0, name.indexOf("["));
        } else if (name.contains("【")) {
            name = name.substring(0, name.indexOf("【"));
        } else {
            return name;
        }
        return name;
    }

    public static String getAddress(String address) {
        if (address.contains("邮政编码")) {
            address = address.substring(0, address.indexOf("邮政编码"));
        } else if (address.contains("邮编")) {
            address = address.substring(0, address.indexOf("邮编"));
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
        if (depositMassage.contains("微信")) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf("微信"));
        } else if (depositMassage.contains("qq")) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf("qq"));
        } else if (depositMassage.contains("QQ")) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf("QQ"));
        } else if (depositMassage.contains("支付宝")) {
            depositMassage = depositMassage.substring(0, depositMassage.indexOf("支付宝"));
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
                    if(index==0){
                        bankTypes.add("");
                    }else{
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
        rightText=StringUtils.replaceEach(rightText, new String[] { " ", " ", "\u00A0", },
                new String[] { "", "", "" }).replace("\n", "");
        Matcher m = p.matcher(rightText);
        List<String> temp = new ArrayList<String>();
        List<String> telphone = new ArrayList<String>();
        while (m.find()) {
            temp.add(m.group());
        }
        for (int j = 0; j < temp.size(); j++) {
            if (temp.get(j).length() == 7 || temp.get(j).length() == 8) {
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
        rightText=StringUtils.replaceEach(rightText, new String[] { " ", " ", "&nbsp;", },
                new String[] { "", "", "" }).replace("\n", "");
        List<String> temp = new ArrayList<String>();
        List<String> cardNumber = new ArrayList<String>();
        Pattern pt = Pattern.compile("\\d{15,}");
        Matcher mt = pt.matcher(rightText);
        while (mt.find()) {
            temp.add(mt.group());
        }
        for (int n = 0; n < temp.size(); n++) {
            if (temp.get(n).length() >= 16 && temp.get(n).length() <= 20) {
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

    public static boolean urlCompare(List<String> listUrl, String url) {
        for (int n = 0; n < listUrl.size(); n++) {
            if (listUrl.get(n).equals(url)) {
                return true;
            }
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

    public static String splitString(String temp) {
        if (temp.contains("：") && temp.split("：").length > 1) {
            return temp.split("：")[1];
        } else if (temp.contains(":") && temp.split(":").length > 1) {
            return temp.split(":")[1];
        } else if (temp.contains(";") && temp.split(";").length > 1) {
            return temp.split(";")[1];
        } else {
            if (temp.contains("；") && temp.split("；").length > 1) {
                return temp.split("；")[1];
            }
        }
        return "";
    }

    public static Date dateFormatter(String temp) {
        String strDate = "";
        if (temp.contains("：") || temp.contains(":")) {
            if (temp.contains("年") && temp.contains("月") && temp.contains("日")) {
                strDate = temp.split("：")[1].replace("年", "-").replace("月", "-");
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

    public static boolean nameCompare(List<String> userInfo, String name) {
        for (int i = 0; i < userInfo.size(); i++) {
            if (userInfo.get(i).equals(name)) {
                return false;
            }
        }
        return true;
    }
}
