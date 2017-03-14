package com.zx.promotion.datamodel;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserInfo implements Serializable {
    private String username;

    private String avatar;

    private String sellerTurnover;

    private int tradecount;

    private String passRate;

    private long userId;
    
    private String merchantName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getTradecount() {
        return tradecount;
    }

    public void setTradecount(int tradecount) {
        this.tradecount = tradecount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSellerTurnover() {
        return sellerTurnover;
    }

    public void setSellerTurnover(String sellerTurnover) {
        this.sellerTurnover = sellerTurnover;
    }

    public String getPassRate() {
        return passRate;
    }

    public void setPassRate(String passRate) {
        this.passRate = passRate;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
