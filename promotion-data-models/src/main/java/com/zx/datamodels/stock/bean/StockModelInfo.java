package com.zx.datamodels.stock.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StockModelInfo implements Serializable {
	private Integer stockInfoId;

    private Integer market;

    private String stockCode;

    private String stockName;

    private Integer reserveAmount;

    private Double beginPrice;

    private Boolean hasF10;

    private Date publishDate;

    private Boolean isDel;

    private Date modDate;

    private String f10;

    public Integer getStockInfoId() {
        return stockInfoId;
    }

    public void setStockInfoId(Integer stockInfoId) {
        this.stockInfoId = stockInfoId;
    }

    public Integer getMarket() {
        return market;
    }

    public void setMarket(Integer market) {
        this.market = market;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode == null ? null : stockCode.trim();
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName == null ? null : stockName.trim();
    }

    public Integer getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(Integer reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public Double getBeginPrice() {
        return beginPrice;
    }

    public void setBeginPrice(Double beginPrice) {
        this.beginPrice = beginPrice;
    }

    public Boolean getHasF10() {
        return hasF10;
    }

    public void setHasF10(Boolean hasF10) {
        this.hasF10 = hasF10;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public String getF10() {
        return f10;
    }

    public void setF10(String f10) {
        this.f10 = f10 == null ? null : f10.trim();
    }
}
