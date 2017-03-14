package com.zx.repeat.crawler.bean;

import java.util.List;

import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;

public class OriginUserInfo {
    List<StockTrade> stockTrade;
    List<StockTradeMassage> stockTradeMassage;
    private List<AbnormalUrl> abnormalUrls;

    public List<StockTrade> getStockTrade() {
        return stockTrade;
    }

    public void setStockTrade(List<StockTrade> stockTrade) {
        this.stockTrade = stockTrade;
    }

    public List<StockTradeMassage> getStockTradeMassage() {
        return stockTradeMassage;
    }

    public void setStockTradeMassage(List<StockTradeMassage> stockTradeMassage) {
        this.stockTradeMassage = stockTradeMassage;
    }

    public List<AbnormalUrl> getAbnormalUrls() {
        return abnormalUrls;
    }

    public void setAbnormalUrls(List<AbnormalUrl> abnormalUrls) {
        this.abnormalUrls = abnormalUrls;
    }
}
