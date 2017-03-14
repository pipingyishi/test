package com.zx.crawler.url.entity;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import com.google.common.collect.Maps;
import com.zx.crawler.stock.entity.Provider;
import com.zx.crawler.stock.util.CrawUrlThread;
import com.zx.crawler.stock.util.DocumentUtil;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.MultiThreadParameter;
import com.zx.crawler.stock.util.ParseUtil;
import com.zx.crawler.stock.util.SaveAbnormalUrl;
import com.zx.crawler.stock.util.StockTradeUtil;
import com.zx.modules.trade.service.AbnormalUrlService;
import com.zx.trade.datamodel.AbnormalUrl;

public class Entrace extends AbsStockCrawler {
    private static Logger LOG = LoggerFactory.getLogger(Entrace.class);
    private static final String PROPERTY_NAME = "trade-stock.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<String> listPageUrl = null;
    private static List<Integer> totalPages = null;
    private static AbnormalUrlService abnormalUrlService = null;
    private static final String SITE = "url";
    private static final int URL_GRAND_ONE = 1;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("host"));
            provider.setTurnPage(props.getProperty("turn_page"));
            provider.setThreadNum(props.getProperty("thread_num"));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            LOG.warn("read profiles from yichen", e);
        }
    }

    @Override
    public void crawContent(AbnormalUrlService abnormalUrl) {
        abnormalUrlService = abnormalUrl;
        List<AbnormalUrl> list =InterceptBlockUtil.selectAbnormalUrl(SITE,abnormalUrlService);
        List<String> abnormalUrls = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            abnormalUrls.add(list.get(i).getAbnormalUrl());
        }
        String url = providerMap.get("provider").getHost();
        Document doc = null;
        try {
            doc = DocumentUtil.getDocument(url);  // 进入版块
        } catch (Exception e) {
            LOG.warn("main url exception {} from yichen in grabing urlId", providerMap.get("provider").getHost(), e);
        }
        if (doc == null) {
            return;
        }
        Elements aNodes = doc.select("[style=height:60px;overflow :hidden;line-height:18px;]").select("a");
        listPageUrl = new ArrayList<String>();
        totalPages = new ArrayList<Integer>();
        for (int i = aNodes.size()-1; i >=0; i--) {
            String link = aNodes.get(3).attr("href");// 获得众多版块中的其中一个版块中的一个链接
            link = providerMap.get("provider").getHost() + link;
            listPageUrl.add(link);// listPageUrl存放的是某一个版块链接
            try {
                doc = null;
                doc = DocumentUtil.getDocument(link);
                LOG.info("enter {} section  corresponding to url :{}  from yichen in grabing urlId", (i+1), link);
            } catch (Exception e) {
                try {
                    if (!StockTradeUtil.isCompare(abnormalUrls, link)) {
                        SaveAbnormalUrl.saveExceptionalUrl(link, URL_GRAND_ONE, SITE);
                    }
                } catch (Exception ex) {
                    LOG.warn("An error occurs when the abnormalUrl into the database:{}grand:1 from yichen in grabing urlId", link, ex);
                }
                LOG.warn("An error occurs when enter a section: {} grand:1 from yichen in grabing urlId", link, e);
            }
            if (doc == null) {
                continue;
            }
            String tdText = doc.select("td.tabletitle1").text();
            String str[] = tdText.split(" ");
            try{
                String pages = str[str.length - 1].split("/")[1]; 
                pages = pages.substring(0, pages.length() - 2);
                totalPages.add(ParseUtil.safeParseInt(pages));// 该板块包含多少页
            }catch(Exception e){
                LOG.info("get the totalPages of {} section  from yichen in grabing urlId",(i+1),e); 
            }
        }
        craw(abnormalUrls);
    }

    public void craw(List<String> abnormalUrls) {// 已经进入一个版块
        for (int i = 0; i < totalPages.size(); i++) {// 不同版块里面总的页数
            MultiThreadParameter mtp = new MultiThreadParameter();
            mtp.setSection(i + 1);
            mtp.setSectionUrl(listPageUrl.get(i));
            mtp.setTurnPageUrl(providerMap.get("provider").getTurnPage());
            mtp.setHost(providerMap.get("provider").getHost());
            mtp.setAbnormalUrl(abnormalUrls);
            mtp.setTotalPages(totalPages.get(i));
            mtp.setThread(ParseUtil.safeParseInt(providerMap.get("provider").getThreadNum()));
            multiThreadHandle(mtp);
        }
    }

    public void multiThreadHandle(MultiThreadParameter mtp) {
        int threadNum = ParseUtil.safeParseInt(providerMap.get("provider").getThreadNum());
        for (int i = 1; i <= threadNum; i++) {
            new CrawUrlThread(i, mtp).start();
        }
    }
}
