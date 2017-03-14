package com.zx.crawler.url.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.modules.trade.service.AbnormalUrlService;

public class GrabUrl {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        AbnormalUrlService abnormalUrlService = (AbnormalUrlService) context.getBean("abnormalUrl");
        AbsStockCrawler entrace = new Entrace();
        entrace.execute(abnormalUrlService);
    }
}
