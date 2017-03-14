package com.zx.crawler.stock.entity;

public class StockGrab {
    public static void main(String[] args) {
        AbsStockCrawler njStock = new NjTradeProvider();
        AbsStockCrawler nsStock = new NsTradeProvider();
        AbsStockCrawler hbStock = new HbTradeProvider();
        AbsStockCrawler yjsStock = new YjsTradeProvider();
        AbsStockCrawler dbStock = new DbTradeProvider();
        AbsStockCrawler znStock = new ZnTradeProvider();
        AbsStockCrawler hzStock = new HzTradeProvider();
        AbsStockCrawler jmjStock = new BJTradeProvider();
        AbsStockCrawler nfStock = new NfTradeProvider();
        AbsStockCrawler fltStock = new FltTradeProvider();
        nsStock.execute();
        hbStock.execute();
        yjsStock.execute();
        dbStock.execute();
        znStock.execute();
        hzStock.execute();
        jmjStock.execute();
        nfStock.execute();
        fltStock.execute();
        njStock.execute();
    }
}
