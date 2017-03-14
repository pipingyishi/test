package com.zx.promotion.datamodel;

import java.io.Serializable;
import java.util.Date;

public class TradeTable implements Serializable {

    private static final long serialVersionUID = 6191851546154935496L;

    private Integer tradeId;

    private String tradeType;

    private String tradeCname;

    private String tradeVarieties;

    private Double tradePrice;
    
    private String tradePriceStr;

    private Integer tradeAmount;

    private String tradeUnit;

    private Double tradeMarketValue;

    private String tradePname;

    private String tradeTelphone;

    private Date tradeTime;

    private String tradeFlag;

    private String tradeRemakes;

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType == null ? null : tradeType.trim();
    }

    public String getTradeCname() {
        return tradeCname;
    }

    public void setTradeCname(String tradeCname) {
        this.tradeCname = tradeCname == null ? null : tradeCname.trim();
    }

    public String getTradeVarieties() {
        return tradeVarieties;
    }

    public void setTradeVarieties(String tradeVarieties) {
        this.tradeVarieties = tradeVarieties == null ? null : tradeVarieties.trim();
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }
    
    public String getTradePriceStr() {
        return tradePriceStr;
    }

    public void setTradePriceStr(String tradePriceStr) {
        this.tradePriceStr = tradePriceStr;
    }

    public Integer getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(Integer tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getTradeUnit() {
        return tradeUnit;
    }

    public void setTradeUnit(String tradeUnit) {
        this.tradeUnit = tradeUnit == null ? null : tradeUnit.trim();
    }

    public Double getTradeMarketValue() {
        return tradeMarketValue;
    }

    public void setTradeMarketValue(Double tradeMarketValue) {
        this.tradeMarketValue = tradeMarketValue;
    }

    public String getTradePname() {
        return tradePname;
    }

    public void setTradePname(String tradePname) {
        this.tradePname = tradePname == null ? null : tradePname.trim();
    }

    public String getTradeTelphone() {
        return tradeTelphone;
    }

    public void setTradeTelphone(String tradeTelphone) {
        this.tradeTelphone = tradeTelphone == null ? null : tradeTelphone.trim();
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeFlag() {
        return tradeFlag;
    }

    public void setTradeFlag(String tradeFlag) {
        this.tradeFlag = tradeFlag == null ? null : tradeFlag.trim();
    }

    public String getTradeRemakes() {
        return tradeRemakes;
    }

    public void setTradeRemakes(String tradeRemakes) {
        this.tradeRemakes = tradeRemakes == null ? null : tradeRemakes.trim();
    }

}
