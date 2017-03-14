package com.zx.crawler.trade.entity;

import com.zx.modules.trade.service.TradeService;

public abstract class AbsStockCrawler {
    public AbsStockCrawler() {

    }

    public void execute(TradeService tradeService) {
        getPropert();
        crawContent(tradeService);
    }

    abstract public void getPropert();

    abstract public void crawContent(TradeService tradeService);

}
