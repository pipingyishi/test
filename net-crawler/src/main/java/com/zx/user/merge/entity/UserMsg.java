package com.zx.user.merge.entity;

import java.util.List;

import com.zx.bean.entity.BankCardMassage;
import com.zx.bean.entity.UserInformation;
import com.zx.trade.datamodel.UserMassage;

public class UserMsg {
    public static void mergeUserInfo(UserInformation userInfo, BankCardMassage bankCardMassage) {
        //获得一尘用户信息
//        List<UserMassage> userMassages = PresentUserMassage.getUserMassage(userInfo.getTradeStocks(),
//                bankCardMassage.getTradeMassages());
        //获得互动用户信息
        List<UserMassage> userInfos = PresentUserMassage.getUserInfo(userInfo.getStockTrades(),
                bankCardMassage.getStockTradeMassage());
        //获得一尘用户卡号信息
//        List<com.zx.trade.datamodel.BankCardMassage> bankCardMsg = PresentUserMassage
//                .getBankCardMsg(bankCardMassage.getTradeMassages());
        //获得互动用户卡号信息
        List<com.zx.trade.datamodel.BankCardMassage> bankCardInfo = PresentUserMassage
                .getBankCardInfo(bankCardMassage.getStockTradeMassage());
//        PresentServiceProvide.addUserMassage(userMassages);
//        PresentServiceProvide.addBankCardInfo(bankCardMsg);
        PresentServiceProvide.addUserMassage(userInfos);
        PresentServiceProvide.addBankCardInfo(bankCardInfo);
    }
}
