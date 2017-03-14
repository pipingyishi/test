package com.zx.crawler.stock.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.AbnormalUrl;

public class SaveAbnormalUrl {
    public static void saveExceptionalUrl(String abnormalUrl, int grand, String tradePlatform) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        TradeService tradeService = (TradeService) context.getBean("tradeService");
        AbnormalUrl abnormalUrls = new AbnormalUrl();
        abnormalUrls.setAbnormalUrl(abnormalUrl);
        abnormalUrls.setIsDel(false);
        abnormalUrls.setTradePlatform(tradePlatform);
        abnormalUrls.setUrlGrand(grand);
        tradeService.saveAbnormalUrl(abnormalUrls);
    }
}
