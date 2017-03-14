package com.zx.user.merge.entity;

import java.util.ArrayList;
import java.util.List;
import com.zx.trade.datamodel.BankCardMassage;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeStock;
import com.zx.trade.datamodel.UserMassage;

public class PresentUserMassage {
    private static final int USER_ID_INDEX = 51919;
    private static final int CARD_ID_INDEX =97396;

    public static List<UserMassage> getUserMassage(List<TradeStock> tradeStock, List<TradeMassage> tradeMassage) {
        List<UserMassage> userMassages = new ArrayList<UserMassage>();
        for (int i = 0; i < tradeStock.size(); i++) {
            UserMassage userMassage = new UserMassage();
            userMassage.setUserId(tradeStock.get(i).getId());
            userMassage.setUserName(tradeStock.get(i).getOnlineName());
            userMassage.setRealName(getStr(tradeStock.get(i).getUserName()));
            userMassage.setTitle(getStr(tradeStock.get(i).getTitel()));
            userMassage.setTradeGrade(tradeStock.get(i).getTradeGrade());
            userMassage.setCreditScore(tradeStock.get(i).getCreditScore());
            userMassage.setScoreTimes(tradeStock.get(i).getScoreTimes());
            userMassage.setPostTimes(tradeStock.get(i).getPostTimes());
            userMassage.setPostScore(tradeStock.get(i).getPostIntegral());
            userMassage.setRegisterTime(tradeStock.get(i).getRegistTime());
            userMassage.setEffectiveDate(null);
            userMassage.setTradeTimes(0);
            userMassage.setDealValue(0.0);
            userMassage.setPraiseRate(null);
            userMassage.setEvaluateGrand(null);
            userMassage.setAddress(getStr(tradeStock.get(i).getAddress()));
            userMassage.setQq(getStr(tradeStock.get(i).getQq()));
            userMassage.setUrl(tradeStock.get(i).getUrlName());
            userMassage.setPlatform(PlatformConstants.getSite());
            userMassage = getTelephone(tradeStock.get(i).getId(), userMassage, tradeMassage);
            userMassages.add(userMassage);
        }
        return userMassages;
    }

    public static List<UserMassage> getUserInfo(List<StockTrade> stockTrade,
            List<StockTradeMassage> stockTradeMassage) {
        List<UserMassage> userMassages = new ArrayList<UserMassage>();
        for (int i = 0; i < stockTrade.size(); i++) {
            UserMassage userMassage = new UserMassage();
            userMassage.setUserId(stockTrade.get(i).getId() + USER_ID_INDEX);
            userMassage.setUserName(stockTrade.get(i).getUserName());
            userMassage.setRealName(getStr(stockTrade.get(i).getRealName()));
            userMassage.setTitle(null);
            userMassage.setTradeGrade(stockTrade.get(i).getMemberLevel());
            userMassage.setCreditScore(0);
            userMassage.setScoreTimes(0);
            userMassage.setPostTimes(0);
            userMassage.setPostScore(0);
            userMassage.setRegisterTime(stockTrade.get(i).getRegistTime());
            userMassage.setEffectiveDate(stockTrade.get(i).getTermValidity());
            userMassage.setTradeTimes(stockTrade.get(i).getTradeTimes());
            userMassage.setDealValue(stockTrade.get(i).getTradeAmount());
            userMassage.setPraiseRate(getStr(stockTrade.get(i).getPraiseRate()));
            userMassage.setEvaluateGrand(getStr(stockTrade.get(i).getEvaluateGrand()));
            userMassage.setAddress(getStr(stockTrade.get(i).getAddress()));
            userMassage.setQq(null);
            userMassage.setUrl(stockTrade.get(i).getUrlName());
            userMassage.setPlatform(PlatformConstants.getPlatform());
            userMassage = getPhone(stockTrade.get(i).getId(), userMassage, stockTradeMassage);
            userMassages.add(userMassage);
        }
        return userMassages;
    }

    public static UserMassage getTelephone(int userId, UserMassage userMassage, List<TradeMassage> tradeMassage) {// 看看有几个电话号码
        boolean endNext = false;
        int index = 0;
        for (int i = 0; i < tradeMassage.size(); i++) {
            if (userId == tradeMassage.get(i).getUserId() && !tradeMassage.get(i).getTelephone().equals("")) {
                index++;
                endNext = true;
            } else {
                if (endNext) {
                    break;
                }
            }
            if (index == 1) {
                userMassage.setTelephone1(tradeMassage.get(i).getTelephone());
            } else if (index == 2) {
                userMassage.setTelephone2(tradeMassage.get(i).getTelephone());
            } else if (index == 3) {
                userMassage.setTelephone3(tradeMassage.get(i).getTelephone());
            } else {
                if (index == 4) {
                    userMassage.setTelephone4(tradeMassage.get(i).getTelephone());
                }
            }
        }
        if (index == 0) {
            userMassage.setTelephone1(null);
            userMassage.setTelephone2(null);
            userMassage.setTelephone3(null);
            userMassage.setTelephone4(null);
        }
        return userMassage;
    }

    public static UserMassage getPhone(int userId, UserMassage userMassage, List<StockTradeMassage> stockTradeMassage) {// 看看有几个电话号码
        boolean endNext = false;
        int index = 0;
        for (int i = 0; i < stockTradeMassage.size(); i++) {
            if (userId == stockTradeMassage.get(i).getUserId() && !stockTradeMassage.get(i).getTelphone().equals("")) {
                index++;
                endNext = true;
            } else {
                if (endNext) {
                    break;
                }
            }
            if (index == 1) {
                userMassage.setTelephone1(stockTradeMassage.get(i).getTelphone());
            } else if (index == 2) {
                userMassage.setTelephone2(stockTradeMassage.get(i).getTelphone());
            } else if (index == 3) {
                userMassage.setTelephone3(stockTradeMassage.get(i).getTelphone());
            } else {
                if (index == 4) {
                    userMassage.setTelephone4(stockTradeMassage.get(i).getTelphone());
                }
            }
        }
        if (index == 0) {
            userMassage.setTelephone1(null);
            userMassage.setTelephone2(null);
            userMassage.setTelephone3(null);
            userMassage.setTelephone4(null);
        }
        return userMassage;
    }

    public static List<BankCardMassage> getBankCardMsg(List<TradeMassage> tradeMassage) {
        List<BankCardMassage> BankCardMassages = new ArrayList<BankCardMassage>();
        for (int i = 0; i < tradeMassage.size(); i++) {
            BankCardMassage cardNumMassag = new BankCardMassage();
            cardNumMassag.setCardId(tradeMassage.get(i).getId());
            cardNumMassag.setUserId(tradeMassage.get(i).getUserId());
            cardNumMassag.setCardNo(getStr(tradeMassage.get(i).getCardNumber()));
            cardNumMassag.setCardType(getStr(tradeMassage.get(i).getBankType()));
            cardNumMassag.setDepositBank(getStr(tradeMassage.get(i).getDepositBank()));
            BankCardMassages.add(cardNumMassag);
        }
        return BankCardMassages;
    }

    public static List<BankCardMassage> getBankCardInfo(List<StockTradeMassage> stockTradeMassage) {
        List<BankCardMassage> BankCardMassages = new ArrayList<BankCardMassage>();
        for (int i = 0; i < stockTradeMassage.size(); i++) {
            BankCardMassage cardNumMassag = new BankCardMassage();
            cardNumMassag.setCardId(stockTradeMassage.get(i).getId() + CARD_ID_INDEX);
            cardNumMassag.setUserId(stockTradeMassage.get(i).getUserId() + USER_ID_INDEX);
            cardNumMassag.setCardNo(getStr(stockTradeMassage.get(i).getCardNo()));
            cardNumMassag.setCardType(getStr(stockTradeMassage.get(i).getBankType()));
            cardNumMassag.setDepositBank(getStr(getDepositBank(stockTradeMassage.get(i).getDepositBank())));
            BankCardMassages.add(cardNumMassag);
        }
        return BankCardMassages;
    }

    public static String getDepositBank(String depositBank) {
        if (depositBank.contains("')")) {
            int index = depositBank.indexOf("')");
            depositBank = depositBank.substring(0, index);
        }
        return depositBank;
    }

    public static String getStr(String temp) {
        if (temp.equals("")) {
            return null;
        }
        return temp;
    }
}
