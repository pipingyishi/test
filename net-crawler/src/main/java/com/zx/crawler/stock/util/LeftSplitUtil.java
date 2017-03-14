package com.zx.crawler.stock.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.zx.trade.datamodel.TradeStock;

public class LeftSplitUtil {
    private static final int ONLINE_NAME = 0;
    private static final int TRADE_GRADE = 1;
    private static final int CREDIT_SCORE = 2;
    private static final int SCORE_TIMES = 3;
    private static final int POST_TIMES = 4;
    private static final int POST_INTEGRAL = 5;
    private static final int REGIST_TIME = 6;
    private static final int LIST_LENGTH = 7;
    private static final int SPLIT_INDEX_ZERO = 0;
    private static final int SPLIT_INDEX_ONE = 1;
    private static final int SPLIT_INDEX_TWO = 2;
    private static final int SPLIT_INDEX_THREE = 3;
    private static final int DB_MAX_LENGTH = 100;
    private static final String NAME = "姓名： 姓名: 姓名； 姓名;";
    private static final String ADDRESS = "地址： 地址: 地址； 地址;";
    private static final String QQ = "QQ： QQ: QQ； QQ;";

    public static String[] getUserMassage(String temp, String referenceMassage) {
        String leftSplit[] = temp.split(" ");
        String msg[] = referenceMassage.split("、");
        String userMassage[] = new String[leftSplit.length];
        userMassage[0] = leftSplit[0];
        int count = 1;
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
        if (accountMsg[ONLINE_NAME] != null && (!accountMsg[0].contains("：") || !accountMsg[0].contains(":"))) {
            tradeStock.setOnlineName(accountMsg[ONLINE_NAME]);
        } else {
            tradeStock.setOnlineName("");
        }
        int sign = 0;
        String rowMsg = "";
        if (accountMsg.length == LIST_LENGTH) {
            tradeStock.setTitel("");
        } else {
            sign = 1;
            rowMsg = StockTradeUtil.getSplitMassage(accountMsg[sign]);
            if (rowMsg.equals("")) {
                tradeStock.setTitel("");
            } else {
                tradeStock.setTitel(rowMsg);
            }
        }
        rowMsg = StockTradeUtil.getSplitMassage(accountMsg[sign + TRADE_GRADE]);
        if (rowMsg.equals("")) {
            tradeStock.setTradeGrade("");
        } else {
            tradeStock.setTradeGrade(rowMsg);
        }
        rowMsg = StockTradeUtil.getSplitMassage(accountMsg[CREDIT_SCORE + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setCreditScore(0);
        } else {
            tradeStock.setCreditScore(Integer.parseInt(rowMsg));
        }
        rowMsg = StockTradeUtil.getSplitMassage(accountMsg[SCORE_TIMES + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setScoreTimes(0);
        } else {
            tradeStock.setScoreTimes(Integer.parseInt(rowMsg));
        }
        rowMsg = StockTradeUtil.getSplitMassage(accountMsg[POST_TIMES + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setPostTimes(0);
        } else {
            tradeStock.setPostTimes(Integer.parseInt(rowMsg));
        }
        rowMsg = StockTradeUtil.getSplitMassage(accountMsg[POST_INTEGRAL + sign]);
        if (rowMsg.equals("") || !StockTradeUtil.isAllDigit(rowMsg)) {
            tradeStock.setPostIntegral(0);
        } else {
            tradeStock.setPostIntegral(Integer.parseInt(rowMsg));
        }
        Date date = StockTradeUtil.dateFormatter(accountMsg[REGIST_TIME + sign]);
        tradeStock.setRegistTime(date);
        return tradeStock;
    }

    public static TradeStock getUserMassages(TradeStock tradeStock, String rightText) {
        String personalMsg[] = rightText.replaceAll("\u00A0", " ").split(" ");
        List<String> allMassage = Lists.newArrayList(personalMsg);
        rightText = rightText.replaceAll("\u00A0", "").replace(" ", "");
        boolean flag = false, status = false, state = false;
        for (int k = 0; k < personalMsg.length; k++) {
            if (StringUtils.containsAny(personalMsg[k], getSplitMassage(NAME, SPLIT_INDEX_ZERO),
                    getSplitMassage(NAME, SPLIT_INDEX_ONE), getSplitMassage(NAME, SPLIT_INDEX_TWO),
                    getSplitMassage(NAME, SPLIT_INDEX_THREE))) {
                if (flag) {
                    continue;
                }
                String name = "";
                if (personalMsg[k].length() > 4) {
                    name = StockTradeUtil.getSplitMassage(personalMsg[k]);
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
            } else if (StringUtils.containsAny(personalMsg[k], getSplitMassage(ADDRESS, SPLIT_INDEX_ZERO),
                    getSplitMassage(ADDRESS, SPLIT_INDEX_ONE), getSplitMassage(ADDRESS, SPLIT_INDEX_TWO),
                    getSplitMassage(ADDRESS, SPLIT_INDEX_THREE))) {
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
                address = StockTradeUtil.getSplitMassage(address);
                address = getAddress(address);
                tradeStock.setAddress(address);
                status = true;
            } else if (StringUtils.containsAny(personalMsg[k].toUpperCase(), getSplitMassage(QQ, SPLIT_INDEX_ZERO),
                    getSplitMassage(QQ, SPLIT_INDEX_ONE), getSplitMassage(QQ, SPLIT_INDEX_TWO),
                    getSplitMassage(QQ, SPLIT_INDEX_THREE))) {
                if (state) {
                    continue;
                }
                if (personalMsg[k].toUpperCase().length() > 4) {
                    String qq = StockTradeUtil.getSplitMassage(personalMsg[k].toUpperCase());
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
        if (address.length() > DB_MAX_LENGTH) {// DB_MAX_LENGTH表示数据库地址字段的最大长度
            address = address.substring(0, 100);
        }
        return address;
    }

    public static String getSplitMassage(String temp, int index) {
        return temp.split(" ")[index];
    }
}
