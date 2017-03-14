package com.zx.bean.entity;

import java.util.List;

import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeMassage;

public class BankCardMassage {
    private List<TradeMassage> tradeMassages;

    private List<StockTradeMassage> stockTradeMassage;

    public List<TradeMassage> getTradeMassages() {
        return tradeMassages;
    }

    public void setTradeMassages(List<TradeMassage> tradeMassages) {
        this.tradeMassages = tradeMassages;
    }

    public List<StockTradeMassage> getStockTradeMassage() {
        return stockTradeMassage;
    }

    public void setStockTradeMassage(List<StockTradeMassage> stockTradeMassage) {
        this.stockTradeMassage = stockTradeMassage;
    }

}
