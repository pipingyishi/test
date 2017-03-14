package com.zx.crawler.stock.trade.entity;

import java.util.List;

import com.zx.modules.trade.service.TradeService;

public abstract class AbsStockCrawler {
    public void execute() {
        getPropert();
        List<String> link = crawUrl();
        crawContent(link);
    }

    abstract public void getPropert();

    abstract public List<String> crawUrl();

    abstract public void crawContent(List<String> link);
}
