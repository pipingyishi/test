package com.zx.promotion.datamodel;

import java.math.BigDecimal;

public class TrustStock {
    private String productName;
    private String title;
    private double productPrice;
    private String dateAvailable;
    private int quantity;
    private String remark;
    private String transactionType;
    private String avatar;
    private String username;
    private BigDecimal sellerTurnover;
    private int tradecount;
    private float passRate;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getDateAvailable() {
        return dateAvailable;
    }

    public void setDateAvailable(String dateAvailable) {
        this.dateAvailable = dateAvailable;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getSellerTurnover() {
        return sellerTurnover;
    }

    public void setSellerTurnover(BigDecimal sellerTurnover) {
        this.sellerTurnover = sellerTurnover;
    }

    public int getTradecount() {
        return tradecount;
    }

    public void setTradecount(int tradecount) {
        this.tradecount = tradecount;
    }

    public float getPassRate() {
        return passRate;
    }

    public void setPassRate(float passRate) {
        this.passRate = passRate;
    }

}
