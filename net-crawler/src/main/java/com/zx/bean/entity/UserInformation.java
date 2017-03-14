package com.zx.bean.entity;

import java.util.List;

import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.TradeStock;

public class UserInformation {
    private List<TradeStock> tradeStocks;
    private List<StockTrade> stockTrades;

    public List<TradeStock> getTradeStocks() {
        return tradeStocks;
    }

    public void setTradeStocks(List<TradeStock> tradeStocks) {
        this.tradeStocks = tradeStocks;
    }

    public List<StockTrade> getStockTrades() {
        return stockTrades;
    }

    public void setStockTrades(List<StockTrade> stockTrades) {
        this.stockTrades = stockTrades;
    }
}
