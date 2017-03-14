package com.zx.frontend.stock.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.zx.trade.datamodel.TradeStock;

public class LeftSplitUtil {
    public static List<String> getLeftMassage(List<String> allMassage) {
        List<String> temporary = new ArrayList<String>();
        for (int i = 0; i < allMassage.size(); i++) {
            if (allMassage.get(i).contains("：")) {
                if (allMassage.get(i).split("：").length >= 1) {
                    temporary.add(allMassage.get(i).split("：")[0]);
                }
            } else if (allMassage.get(i).contains(":")) {
                if (allMassage.get(i).split(":").length >= 1) {
                    temporary.add(allMassage.get(i).split(":")[0]);
                }
            } else if (allMassage.get(i).contains(";")) {
                if (allMassage.get(i).split(";").length >= 1) {
                    temporary.add(allMassage.get(i).split(";")[0]);
                }
            } else if (allMassage.get(i).contains("；")) {
                if (allMassage.get(i).split("；").length >= 1) {
                    temporary.add(allMassage.get(i).split("；")[0]);
                }
            }
        }
        return temporary;
    }

    public static String[] getUserMassage(String temp, String referenceMassage) {
        String leftSplit[] = temp.split(" ");
        String msg[] = referenceMassage.split("、");
        String userMassage[] = new String[leftSplit.length];
        userMassage[0] = leftSplit[0];
        int count=1;
        for (int i = 0; i < msg.length; i++) {
            for (int j = 1; j < leftSplit.length; j++) {
                if (leftSplit[j].contains(msg[i])) {
                    userMassage[count] = leftSplit[j];
                    count++;
                    break;
                }
            }
        }
        return userMassage;
    }

    public static TradeStock getLeftUserMassage(TradeStock tradeStock, String accountMsg[], String url) {
        tradeStock.setUrlName(url);
        if (accountMsg[0] != null && (!accountMsg[0].contains("：") || !accountMsg[0].contains(":"))) {
            tradeStock.setOnlineName(accountMsg[0]);
        } else {
            tradeStock.setOnlineName("");
        }
        int sign = 0;
        String rowMsg = "";
        if (accountMsg.length == 7) {
            tradeStock.setTitel("");
        } else {
            sign = 1;
            rowMsg = StockTradeUtil.splitString(accountMsg[sign]);
            if (rowMsg.equals("")) {
                tradeStock.setTitel("");
            } else {
                tradeStock.setTitel(rowMsg);
            }
        }
        rowMsg = StockTradeUtil.splitString(accountMsg[sign + 1]);
        if (rowMsg.equals("")) {
            tradeStock.setTradeGrade("");
        } else {
            tradeStock.setTradeGrade(rowMsg);
        }
        rowMsg = StockTradeUtil.splitString(accountMsg[2 + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setCreditScore(0);
        } else {
            tradeStock.setCreditScore(Integer.parseInt(rowMsg));
        }
        rowMsg = StockTradeUtil.splitString(accountMsg[3 + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setScoreTimes(0);
        } else {
            tradeStock.setScoreTimes(Integer.parseInt(rowMsg));
        }
        rowMsg = StockTradeUtil.splitString(accountMsg[4 + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setPostTimes(0);
        } else {
            tradeStock.setPostTimes(Integer.parseInt(rowMsg));
        }
        rowMsg = StockTradeUtil.splitString(accountMsg[5 + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setPostIntegral(0);
        } else {
            tradeStock.setPostIntegral(Integer.parseInt(rowMsg));
        }
        Date date = StockTradeUtil.dateFormatter(accountMsg[6 + sign]);
        tradeStock.setRegistTime(date);
        return tradeStock;
    }

    public static TradeStock getUserMassages(TradeStock tradeStock, String rightText) {
        String personalMsg[] = rightText.replaceAll("\u00A0", " ").split(" ");
        List<String> allMassage = Lists.newArrayList(personalMsg);
        rightText = rightText.replaceAll("\u00A0", "").replace(" ", "");
        boolean flag = false, status = false, state = false;
        for (int k = 0; k < personalMsg.length; k++) {
            if (personalMsg[k].contains("姓名：") || personalMsg[k].contains("姓名:") || personalMsg[k].contains("姓名；")
                    || personalMsg[k].contains("姓名;")) {
                if (flag) {
                    continue;
                }
                String name = "";
                if (personalMsg[k].length() > 4) {
                    name = StockTradeUtil.splitString(personalMsg[k]);
                    if (!name.equals("")) {
                        name = StockTradeUtil.getName(name);
                    }
                } else {
                    name = StockTradeUtil.getName(personalMsg[k + 1]);
                    k++;
                }
                for (int n = 0; n < name.length(); n++) {
                    if (name.charAt(n) == '（') {
                        name = name.substring(0, n);
                        break;
                    }
                }
                tradeStock.setUserName(name);
                flag = true;
            } else if (personalMsg[k].contains("地址") || personalMsg[k].contains("地址") || personalMsg[k].contains("地址")
                    || personalMsg[k].contains("地址")) {
                if (status) {
                    continue;
                }
                String address = personalMsg[k];
                if (k != personalMsg.length - 1) {
                    for (int p = k + 1; p < personalMsg.length; p++) {
                        if (!personalMsg[p].contains("：") && !personalMsg[p].contains(":")
                                && !personalMsg[p].contains(";") && !personalMsg[p].contains("；")) {
                            address += personalMsg[p];
                        } else {
                            break;
                        }
                    }
                }
                address = StockTradeUtil.splitString(address);
                address = getAddress(address);
                tradeStock.setAddress(address);
                status = true;
            } else if (personalMsg[k].toUpperCase().contains("QQ：") || personalMsg[k].toUpperCase().contains("QQ:")
                    || personalMsg[k].toUpperCase().contains("QQ；") || personalMsg[k].toUpperCase().contains("QQ;")) {
                if (state) {
                    continue;
                }
                if (personalMsg[k].toUpperCase().length() > 4) {
                    String qq = StockTradeUtil.splitString(personalMsg[k].toUpperCase());
                    if (!qq.equals("")) {
                        qq = StockTradeUtil.getQQ(qq);
                    }
                    tradeStock.setQq(qq);
                } else {
                    tradeStock.setQq(StockTradeUtil.getQQ(personalMsg[k + 1]));
                }
                state = true;
            }
        }
        if (!flag) {
            tradeStock.setUserName("");
        }
        if (!status) {
            tradeStock.setAddress("");
        }
        if (!state) {
            tradeStock.setQq("");
        }
        return tradeStock;
    }

    public static String getAddress(String address) {
        address = StockTradeUtil.getAddress(address);
        if (address.length() > 100) {
            address = address.substring(0, 100);
        }
        return address;
    }
}
