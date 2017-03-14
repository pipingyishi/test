package com.zx.frontend.promotion.trust.util;

import com.zx.datamodels.store.entity.Merchant;
import com.zx.datamodels.user.bean.entity.User;
import com.zx.promotion.datamodel.UserInfo;

public class UserInfoUtil {
    public static UserInfo getUserMassage(Merchant merchant, User user, int productId) {
        String username = user.getUserName();
        String avatar = user.getAvatar();
        String sellerTurnover = "0.0";
        int tradecount = 0;
        String passRate = "0.0%";
        if (merchant != null) {
            if (productId == 1) {// 包托管
                sellerTurnover = merchant.getSellerTurnoverStr();
                tradecount = merchant.getTradeCount();
                passRate = merchant.getPassRateStr();
            } else {
                sellerTurnover = merchant.getSgSellerTurnoverStr();
                tradecount = merchant.getSgTradeCount();
            }
        }
        String merchantName=merchant.getMerchantName();
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setAvatar(avatar);
        userInfo.setSellerTurnover(sellerTurnover);
        userInfo.setTradecount(tradecount);
        userInfo.setPassRate(passRate);
        userInfo.setMerchantName(merchantName);
        return userInfo;
    }
}
