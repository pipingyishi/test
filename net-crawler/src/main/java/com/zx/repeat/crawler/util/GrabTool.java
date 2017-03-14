package com.zx.repeat.crawler.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.zx.crawler.stock.trade.entity.Entrace;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.ParseUtil;
import com.zx.modules.trade.service.TradeService;
import com.zx.modules.url.service.UrlService;
import com.zx.stock.datamodel.UserUrlInfo;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.ZxQueryResult;
import com.zx.user.merge.entity.PlatformConstants;

public class GrabTool {
    private static Logger LOG = LoggerFactory.getLogger(GrabTool.class);
    private static final String PROPERTY_NAME = "stock-trade.properties";
    private static final int PAGE_SIZE = 200;
    private static final int PAGE=0;
    private static TradeService tradeService = null;
    private static UrlService urlService = null;
    private static ZxQueryResult rs = null;
    private static List<String> exceptionUrl = null;
    private static int page = 0;
    private static final int FIXED_THREAD_NUM=20;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        tradeService = (TradeService) context.getBean("tradeService");
        urlService = (UrlService) context.getBean("userUrlInfo");
    }

    public static int getThreadNum() {
        Resource resource = new ClassPathResource(PROPERTY_NAME);
        String ThradNumStr = "";
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            ThradNumStr = props.getProperty("thread_num");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ParseUtil.safeParseInt(ThradNumStr);
    }

    public static void grabUserInfo(int threadNum) {
        rs = getOriginDate();
        exceptionUrl = new ArrayList<String>();
        for (int i = 0; i < rs.getAbnormalUrls().size(); i++) {
            AbnormalUrl abnormalUrl = rs.getAbnormalUrls().get(i);
            exceptionUrl.add(abnormalUrl.getAbnormalUrl());
        }
        int pageNum = 1;
        while (true) {
            page = pageNum;
            List<String> urList = new ArrayList<String>();
            List<UserUrlInfo> urlIds = urlService.selectUpdatedUrlId();
            int urlId = 0;
            if (!urlIds.isEmpty()) {
                urlId = urlIds.get(0).getId();
            }
            int position = pageNum * PAGE_SIZE;
            if (urlId >= position) {
                pageNum++;
                continue;
            }
            int prePosition = (pageNum - 1) * PAGE_SIZE;
            urlService.updatePosition(prePosition, false);
            List<UserUrlInfo> userUrlInfos = urlService.selectUrl(pageNum, PAGE_SIZE);
            if (userUrlInfos.isEmpty()) {
                break;
            }
            urlService.updatePosition(position, true);
            for (int i = 0; i < userUrlInfos.size(); i++) {
                UserUrlInfo userUrlInfo = userUrlInfos.get(i);
                urList.add(userUrlInfo.getUrl());
            }
//            getUserInfo(urList);
            int count=threadNum;
            MultiThread threadOne=new MultiThread(urList, count, threadNum);
            MultiThread threadTwo=new MultiThread(urList, --count, threadNum);
            MultiThread threadThree=new MultiThread(urList,-- count, threadNum);
            threadOne.start();
            threadTwo.start();
            threadThree.start();
            try{
                threadOne.join();
                threadTwo.join();
                threadThree.join();
            }catch(InterruptedException e){
                LOG.warn("An error occour when creating thread");
            }
            LOG.info("This cycle ends ；cycle to " + pageNum + " times");
            pageNum++;
        }
        LOG.info("programe is over...................................");
    }

//    public static void getUserInfo(List<String> urList){
//        Entrace entrace = new Entrace();
//        try {
//              entrace.getUserName(PAGE, urList, rs, exceptionUrl, page);
//              LOG.info("Fetching data");
//        } catch (Exception e) {
//              LOG.warn("An error occur when grabing the date " + e);
//        }
//    }
    
    public static void getUserInfo(int startIndex, int endIndex, List<String> urList) {
        List<String> partUrList = new ArrayList<String>();
        for (int i = startIndex; i < endIndex; i++) {
            partUrList.add(urList.get(i));
        }
        Entrace entrace = new Entrace();
        try {
            entrace.getUserName(PAGE, partUrList, rs, exceptionUrl, page);
            LOG.info("Fetching data");
        } catch (Exception e) {
            LOG.warn("An error occur when grabing the date " + e);
        }
    }

    public static ZxQueryResult getOriginDate() {
        ZxQueryResult zxQueryResult = new ZxQueryResult();
        List<StockTrade> stockTrade = InterceptBlockUtil.paginationQuery(tradeService);
        List<StockTradeMassage> stockTradeMassage = InterceptBlockUtil.trunPageQuery(tradeService);
        List<AbnormalUrl> abnormalUrl = tradeService.selectExisteAbnormalUrl(PlatformConstants.getPlatform());
        zxQueryResult.setStockTrades(stockTrade);
        zxQueryResult.setStockTradeMassage(stockTradeMassage);
        zxQueryResult.setAbnormalUrls(abnormalUrl);
        return zxQueryResult;
    }
}
