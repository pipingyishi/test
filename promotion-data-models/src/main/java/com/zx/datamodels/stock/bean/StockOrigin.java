package com.zx.datamodels.stock.bean;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StockOrigin implements Serializable {
    private Integer stockinfoid;

    private Integer market;

    private String stockcode;

    private String stockname;

    private String stockpics;

    private Integer stockamount;

    private BigDecimal beginprice;

    private Date publishdate;

    private String isdel;

    private Date updateDate;

    public Integer getStockinfoid() {
        return stockinfoid;
    }

    public void setStockinfoid(Integer stockinfoid) {
        this.stockinfoid = stockinfoid;
    }

    public Integer getMarket() {
        return market;
    }

    public void setMarket(Integer market) {
        this.market = market;
    }

    public String getStockcode() {
        return stockcode;
    }

    public void setStockcode(String stockcode) {
        this.stockcode = stockcode == null ? null : stockcode.trim();
    }

    public String getStockname() {
        return stockname;
    }

    public void setStockname(String stockname) {
        this.stockname = stockname == null ? null : stockname.trim();
    }

    public String getStockpics() {
        return stockpics;
    }

    public void setStockpics(String stockpics) {
        this.stockpics = stockpics == null ? null : stockpics.trim();
    }

    public Integer getStockamount() {
        return stockamount;
    }

    public void setStockamount(Integer stockamount) {
        this.stockamount = stockamount;
    }

    public BigDecimal getBeginprice() {
        return beginprice;
    }

    public void setBeginprice(BigDecimal beginprice) {
        this.beginprice = beginprice;
    }

    public Date getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(Date publishdate) {
        this.publishdate = publishdate;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel == null ? null : isdel.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
