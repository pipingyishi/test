package com.zx.abnormal.user.massage.entity;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.crawler.stock.trade.entity.Entrace;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.ListUtil;
import com.zx.modules.trade.service.AbnormalUrlService;
import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.user.merge.entity.PlatformConstants;

public class HuserMassage extends AbsYuserMassage {
    private static Logger LOG = LoggerFactory.getLogger(HuserMassage.class);
    private static final String BASE_URL = "http://shop.ybk001.com/";

    @Override
    public List<String> selectUrl(AbnormalUrlService abnormalUrlService) {
        List<AbnormalUrl> abnormalUrls = InterceptBlockUtil.selectAbnormalUrl(PlatformConstants.getPlatform(),
                abnormalUrlService);
        List<String> url = new ArrayList<String>();
        List<String> nextUrl = new ArrayList<String>();
        for (int i = 0; i < abnormalUrls.size(); i++) {
            if (abnormalUrls.get(i).getUrlGrand() == 1) {
                url.add(abnormalUrls.get(i).getAbnormalUrl());
            }
            if (abnormalUrls.get(i).getUrlGrand() == 2) {
                nextUrl.add(abnormalUrls.get(i).getAbnormalUrl());
            }
        }
        List<String> list = getUrl(url);
        for (int j = 0; j < list.size(); j++) {
            nextUrl.add(list.get(j));
        }
        return nextUrl;
    }

    @Override
    public void getUserMassage(List<String> urList) {
        Entrace entrace = new Entrace();
        List<String> existName = getExistName();
        ListUtil listUtil = new ListUtil();
        listUtil.setExistName(existName);
        for (int i = 0; i < urList.size(); i++) {
            String url = urList.get(i);
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(20000);
                Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url);
                LOG.info("get username with abnormal url corresponding to url:{} from hudong ", url);
                listUtil.setLink(url);
                listUtil.setPage(0);
                String userName = doc.select("table.detail").last().select("td.detailleft").select("script")
                        .attr("src");
                userName = userName.split("=")[1];
                userName = userName.substring(0, userName.indexOf("&"));
                userName = URLEncoder.encode(userName, "gb2312");
                ListUtil userMassage = entrace.getUserMassage(userName, listUtil);
                if (userMassage != null) {
                    entrace.saveUserMassage(userMassage, url);
                } else {
                    continue;
                }
            } catch (Exception e) {
                LOG.info("get username with abnormal url corresponding to url:{} from hudong ", url, e);
            }
        }
    }

    public List<String> getUrl(List<String> list) {
        List<String> url = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            Document doc = null;
            try {
                URLConnection conn = new URL(list.get(8)).openConnection();
                conn.setConnectTimeout(20000);
                doc = Jsoup.parse(conn.getInputStream(), "gbk", list.get(8)); // 进入某一页
                LOG.info("grab link of a page corresponding to url:{}  from abnormalUrl of hudong", list.get(i));
            } catch (Exception e) {
                LOG.info(
                        "An error occours when grabing link of a page corresponding to url:{}  from abnormalUrl of hudong",
                        list.get(i), e);
            }
            if (doc == null) {
                continue;
            }
            Elements trNode = doc.select("table.treadlistbox").last().select("tr");
            if (trNode.size() == 2 || trNode.get(1).select("td").size() == 0) {
                continue;
            }
            for (int j = 1; j < trNode.size(); j++) {
                url.add(BASE_URL + trNode.get(j).select("td").first().select("a").attr("href"));
            }
        }
        return url;
    }

    public List<String> getExistName() {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        TradeService tradeService = (TradeService) context.getBean("tradeService");
        List<StockTrade> stockTrade = InterceptBlockUtil.paginationQuery(tradeService);
        List<String> existName = new ArrayList<String>();
        for (int i = 0; i < stockTrade.size(); i++) {
            existName.add(stockTrade.get(i).getUserName() + stockTrade.get(i).getRealName());
        }
        return existName;
    }

}
