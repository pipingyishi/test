package com.zx.crawler.trade.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.modules.trade.service.TradeService;

public class StockGrab {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        TradeService tradeService = (TradeService) context.getBean("tradeService");
        AbsStockCrawler entrace = context.getBean(Entrace.class);
        entrace.execute(tradeService);
    }
}
