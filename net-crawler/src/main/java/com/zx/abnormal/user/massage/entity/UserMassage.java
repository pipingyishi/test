package com.zx.abnormal.user.massage.entity;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.crawler.stock.util.CrawUserMassageThread;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.ListUtil;
import com.zx.modules.trade.service.AbnormalUrlService;
import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.user.merge.entity.PlatformConstants;

public class UserMassage extends AbsUserMassage {
    private static Logger LOG = LoggerFactory.getLogger(UserMassage.class);
    private static final String BASE_URL = "http://www.xx007.cn/";

    @Override
    public List<String> selectUrl(AbnormalUrlService abnormalUrlService) {
        List<AbnormalUrl> abnormalUrls = InterceptBlockUtil.selectAbnormalUrl(PlatformConstants.getSite(),
                abnormalUrlService);
        List<String> urList = new ArrayList<String>();
        List<String> urlUpList = new ArrayList<String>();
        for (int i = 0; i < abnormalUrls.size(); i++) {
            if (abnormalUrls.get(i).getUrlGrand() == 3) {
                urList.add(abnormalUrls.get(i).getAbnormalUrl().replace("www3", "www"));
            }
            if (abnormalUrls.get(i).getUrlGrand() == 2) {
                urlUpList.add(abnormalUrls.get(i).getAbnormalUrl().replace("www3", "www"));
            }
        }
        List<String> list = getUserUrl(urlUpList);
        for (int j = 0; j < list.size(); j++) {
            urList.add(list.get(j));
        }
        return urList;
    }

    @Override
    public void getUserMassage(List<String> urList) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        TradeService tradeService = (TradeService) context.getBean("tradeService");
        ListUtil list = InterceptBlockUtil.existDataBaseData(tradeService);
        CrawUserMassageThread userMassage = new CrawUserMassageThread();
        for (int i = 0; i < urList.size(); i++) {
            userMassage.getUserMsssage(urList.get(i), list);
        }
    }

    public List<String> getUserUrl(List<String> list) {
        List<String> url = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String link = list.get(i);
            try {
                URLConnection conn = new URL(link).openConnection();
                conn.setConnectTimeout(10000);
                Document doc = Jsoup.parse(conn.getInputStream(), "gbk", link);
                LOG.info("get the link of a page  corresponding to url {} from abnormalUrl of hudong", link);
                Elements ListAnodes = doc.select("div.list").select("div.listtitle").select("a");// 获得某一页里面的多个a链接
                for (Element listAnode : ListAnodes) {// 解析某一页里面的不同的a链接
                    url.add(BASE_URL + listAnode.attr("href"));
                }
            } catch (Exception e) {
                LOG.warn("An error occurs when grabing the url of a page corresponding to url {} from abnormalUrl",
                        link, e);
            }
        }
        return url;
    }
}
