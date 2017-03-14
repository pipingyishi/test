package com.zx.crawler.stock.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.StaticApplicationContext;

import com.zx.trade.datamodel.TradeMassage;

public class RightSplitUtil {
    private static final int DB_DEPOSIT_MAX_LENGTH = 100;

    public static List<TradeMassage> getUserMassage(ListUtil list) {
        List<TradeMassage> tm = new ArrayList<TradeMassage>();
        int size = 0;
        if (list.isFlag() && !list.isStatus()) {
            for (int i = 0; i < list.getTelphone().size(); i++) {
                TradeMassage tradeMassage = new TradeMassage();
                tradeMassage.setTelephone(list.getTelphone().get(i));
                tradeMassage.setCardNumber("");
                tradeMassage.setDepositBank("");
                tradeMassage.setBankType("");
                tm.add(tradeMassage);
            }
        } else if (!list.isFlag() && list.isStatus()) {
            for (int i = 0; i < list.getCardNo().size(); i++) {
                TradeMassage tradeMassage = new TradeMassage();
                tradeMassage.setTelephone("");
                tradeMassage.setCardNumber(list.getCardNo().get(i));
                if (list.getDepositBank().size() > i) {
                    tradeMassage.setDepositBank(getDepositBank(list.getDepositBank().get(i)));
                } else {
                    tradeMassage.setDepositBank("");
                }
                if (list.getBankType().size() > i) {
                    tradeMassage.setBankType(list.getBankType().get(i));
                } else {
                    tradeMassage.setBankType("");
                }
                tm.add(tradeMassage);
            }
        } else {
            if (list.getTelphone().size() > list.getCardNo().size()) {
                size = list.getTelphone().size();
            } else {
                size = list.getCardNo().size();
            }
            for (int q = 0; q < size; q++) {
                TradeMassage tradeMassage = new TradeMassage();
                if (list.getTelphone().size() > q) {
                    tradeMassage.setTelephone(list.getTelphone().get(q));
                } else {
                    tradeMassage.setTelephone("");
                }
                if (list.getCardNo().size() > q) {
                    tradeMassage.setCardNumber(list.getCardNo().get(q));
                } else {
                    tradeMassage.setCardNumber("");
                }
                if (list.getDepositBank().size() > q) {
                    tradeMassage.setDepositBank(getDepositBank(list.getDepositBank().get(q)));
                } else {
                    tradeMassage.setDepositBank("");
                }
                if (list.getBankType().size() > q) {
                    tradeMassage.setBankType(list.getBankType().get(q));
                } else {
                    tradeMassage.setBankType("");
                }
                tm.add(tradeMassage);
            }
        }
        return tm;
    }

    public static String getDepositBank(String depositMassage) {
        depositMassage = StockTradeUtil.getDepositBank(depositMassage);
        if (depositMassage.length() > DB_DEPOSIT_MAX_LENGTH) {// DB_DEPOSIT_MAX_LENGTH表示开户行字段的最大长度
            depositMassage = depositMassage.substring(0, 100);
        }
        return depositMassage;
    }

    public static List<String> openAccountMassage(String msg, List<String> cardNumber) {
        List<String> depositMassage = new ArrayList<String>();
        if (cardNumber.size() == 0) {
            return depositMassage;
        }
        String temp[] = cowMassage(msg);
        boolean flag = false;
        for (int i = 0; i < cardNumber.size(); i++) {
            for (int j = 0; j < temp.length; j++) {
                if (temp[j].contains(cardNumber.get(i))) {
                    int index = temp[j].indexOf(cardNumber.get(i)) + cardNumber.get(i).length();
                    if (temp[j].length() > index) {
                        depositMassage.add(temp[j].substring(index));
                    } else {
                        depositMassage.add("");
                    }
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                depositMassage.add("");
            }
            flag = false;
        }
        return depositMassage;
    }

    public static String[] cowMassage(String msg) {
        String temp[] = msg.split("<br>");
        for (int j = 0; j < temp.length; j++) {
            temp[j] = StringUtils.replaceEach(temp[j], new String[] { " ", " ", "&nbsp;", ",", "，", ";", "；" },
                    new String[] { "", "", "", "", "", "", "" }).replace("\n", "");
        }
        if (temp[temp.length - 1].contains("')")) {
            int index = temp[temp.length - 1].indexOf("')");
            temp[temp.length - 1] = temp[temp.length - 1].substring(0, index);
        }
        return temp;
    }

    public static boolean foundCompreWithExist(List<String> exist, List<String> found) {
        boolean flag = false;
        for (int k = 0; k < exist.size(); k++) {
            if (exist.get(k).length() == 0 || exist.get(k).equals(" ")) {
                continue;
            }
            if (StockTradeUtil.isCompare(found, exist.get(k))) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
