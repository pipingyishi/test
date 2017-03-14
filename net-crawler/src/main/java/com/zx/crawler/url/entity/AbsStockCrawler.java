package com.zx.crawler.url.entity;

import com.zx.modules.trade.service.AbnormalUrlService;

public abstract class AbsStockCrawler {
    public AbsStockCrawler() {

    }

    public void execute(AbnormalUrlService abnormalUrlService) {
        getPropert();
        crawContent(abnormalUrlService);
    }

    abstract public void getPropert();

    abstract public void crawContent(AbnormalUrlService abnormalUrlService);

}
