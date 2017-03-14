package com.zx.crawler.trade.entity;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zx.crawler.stock.entity.Provider;
import com.zx.crawler.stock.util.CrawUrlThread;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.LeftSplitUtil;
import com.zx.crawler.stock.util.ListUtil;
import com.zx.crawler.stock.util.MultiThread;
import com.zx.crawler.stock.util.MultiThreadParameter;
import com.zx.crawler.stock.util.ParseUtil;
import com.zx.crawler.stock.util.RightSplitUtil;
import com.zx.crawler.stock.util.SaveAbnormalUrl;
import com.zx.crawler.stock.util.StockTradeUtil;
import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeResult;
import com.zx.trade.datamodel.TradeStock;
import com.zx.trade.datamodel.ZxQueryResult;

@Service
public class Entrace extends AbsStockCrawler {
    private static Logger LOG = LoggerFactory.getLogger(Entrace.class);
    private static final String PROPERTY_NAME = "trade-stock.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<String> listPageUrl = null;
    private static List<Integer> totalPages = null;
    private static Map<String, String> filter = null;
    private static List<String> allMassage = null;
    private static TradeService tradeService = null;
    private static List<String> oname = new ArrayList<String>();
    private static final String SITE = "一尘";
    private static final int URL_GRAND_ONE = 1;
    private static final int URL_GRAND_THREE = 3;

    @Override
    public void getPropert() {
        filter = new HashMap<String, String>();
        allMassage = new ArrayList<String>();
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("host"));
            provider.setTurnPage(props.getProperty("turn_page"));
            provider.setBankType(props.getProperty("bank_type"));
            provider.setReferenceInformation(props.getProperty("reference_information"));
            provider.setThreadNum(props.getProperty("user_thread"));

            providerMap.put("provider", provider);
        } catch (Exception e) {
            LOG.warn("read profiles from yichen", e);
        }
    }

    @Override
    public void crawContent(TradeService ts) {
        tradeService = ts;
        List<TradeStock> tradeStocks = InterceptBlockUtil.flipPageQuery(ts);
        List<AbnormalUrl> exceptionalUrl = ts.selectExisteAbnormalUrl(SITE);
        List<TradeMassage> tradeMassages = tradeService.selectMassage();
        List<String> abnormalUrls = new ArrayList<String>();
        List<String> name = new ArrayList<String>();
        for (int i = 0; i < tradeStocks.size(); i++) {
            name.add(tradeStocks.get(i).getOnlineName() + tradeStocks.get(i).getUserName());
        }
        ListUtil list = new ListUtil();
        list = list.getListMassage(tradeStocks, tradeMassages, exceptionalUrl);
        abnormalUrls = list.getAbnormalUrls();
        try {
            String url = providerMap.get("provider").getHost();
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(10000);
            LOG.info("enter page of section  corresponding to url {} from yichen", url);
            Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url);// 进入版块
            Elements aNodes = doc.select("[style=height:60px;overflow :hidden;line-height:18px;]").select("a");
            listPageUrl = new ArrayList<String>();
            totalPages = new ArrayList<Integer>();
            for (int i = aNodes.size() - 14; i >= 0; i--) {
                String link = aNodes.get(i).attr("href");// 获得众多版块中的其中一个版块中的一个链接
                link = providerMap.get("provider").getHost() + link;
                listPageUrl.add(link);// listPageUrl存放的是某一个版块链接
                try {
                    conn = new URL(link).openConnection();
                    conn.setConnectTimeout(10000);
                    doc = Jsoup.parse(conn.getInputStream(), "gbk", link);
                    String tdText = doc.select("td.tabletitle1").text();
                    String str[] = tdText.split(" ");
                    String pages = str[str.length - 1].split("/")[1];
                    pages = pages.substring(0, pages.length() - 2);
                    totalPages.add(Integer.parseInt(pages));// 该板块包含多少页
                } catch (Exception e) {
                    try {
                        if (!StockTradeUtil.isCompare(abnormalUrls, link)) {
                            SaveAbnormalUrl.saveExceptionalUrl(link, URL_GRAND_ONE, SITE);
                        }
                    } catch (Exception ex) {
                        LOG.warn("An error occurs when the data into the database:url {} grand:1 from yichen", link,
                                ex);
                    }
                    LOG.warn("An error occurs when enter a section: url {}  grand:1 from yichen", link, e);
                }
            }
            craw(list, name);
        } catch (Exception e) {
            LOG.warn("url exception" + providerMap.get("provider").getHost() + "from yichen", e);
        }
    }

    public void craw(ListUtil list, List<String> name) {// 已经进入一个版块
        for (int i = 0; i < totalPages.size(); i++) {// 不同版块里面总的页数
            MultiThreadParameter mtp = new MultiThreadParameter();
            mtp.setSection(i + 1);
            mtp.setSectionUrl(listPageUrl.get(i));
            mtp.setTurnPageUrl(providerMap.get("provider").getTurnPage());
            mtp.setHost(providerMap.get("provider").getHost());
            mtp.setAbnormalUrl(list.getAbnormalUrls());
            mtp.setList(list);
            mtp.setName(name);
            mtp.setTotalPages(totalPages.get(i));
            mtp.setThread(ParseUtil.safeParseInt(providerMap.get("provider").getThreadNum()));
            multiThreadHandle(mtp);
        }
    }

    public void multiThreadHandle(MultiThreadParameter mtp) {
        int threadNum = ParseUtil.safeParseInt(providerMap.get("provider").getThreadNum());
        for (int i = 1; i <= threadNum; i++) {
            new MultiThread(i, mtp).start();
        }
    }

    public void getUserMassage(List<String> userMassageLink, ListUtil list, List<String> name) {
        boolean flag = false;
        for (int i = 0; i < userMassageLink.size(); i++) {// 代表一页里面可以点击的链接
            String url = userMassageLink.get(i);
            flag = StockTradeUtil.isCompare(list.getListUrl(), url);
            if (flag) {
                continue;
            }
            Document doc = null;
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(10000);
                doc = Jsoup.parse(conn.getInputStream(), "gbk", url);// 进入某一页里面的某个链接
                LOG.info("grab user massage cortesponding to url:{} from yichen", url);
                Elements imgNodes = doc.select("[src=skins/Default/sigline.gif]");
                Elements divNodes = doc.select("div.postuserinfo");
                if (imgNodes.size() == 0 || divNodes.size() == 0) {
                    continue;
                }
                String tdText = doc.select("td.tabletitle1").text();
                String str[] = tdText.split(" ");
                String pages = str[str.length - 1].split("/")[1];
                pages = pages.substring(0, pages.length() - 2);
                getText(divNodes, imgNodes, url, list, name);// 代表某一页用户信息
            } catch (Exception e) {
                try {
                    if (!StockTradeUtil.isCompare(list.getAbnormalUrls(), url)) {
                        SaveAbnormalUrl.saveExceptionalUrl(url, URL_GRAND_THREE, SITE);
                    }
                } catch (Exception ex) {
                    LOG.warn("An error occurs when the data into the database" + url + "grand:3 from yichen", ex);
                }
                LOG.warn("a link of a page connect  unsuccessfully" + url + "from yichen ", e);
            }
            if (doc == null) {
                continue;
            }
            Elements aNodes = doc.select("table.tableborder5").select("a");
            if (aNodes.size() != 0) {// 说明要翻页
                for (int k = 0; k < aNodes.size(); k++) {
                    String nextUrl = aNodes.get(k).attr("href");
                    if (!nextUrl.contains("www")) {
                        nextUrl = providerMap.get("provider").getHost() + nextUrl.substring(0, nextUrl.length());
                    }
                    if (k == aNodes.size() - 1) {
                        nextUrl = nextUrl.substring(0, nextUrl.length() - 1);
                        nextUrl += "1";
                    }
                    try {
                        if (StockTradeUtil.isCompare(list.getListUrl(), nextUrl)) {
                            continue;
                        }
                        URLConnection conn = new URL(nextUrl).openConnection();
                        conn.setConnectTimeout(10000);
                        Document docd = Jsoup.parse(conn.getInputStream(), "gbk", nextUrl);
                        Elements divNextNodes = docd.select("div.postuserinfo");
                        Elements imgNextNodes = docd.select("[src=skins/Default/sigline.gif]");
                        if (divNextNodes.size() == 0 || imgNextNodes.size() == 0) {
                            continue;
                        }
                        getText(divNextNodes, imgNextNodes, nextUrl, list, name);
                    } catch (Exception e) {
                        try {
                            if (!StockTradeUtil.isCompare(list.getAbnormalUrls(), nextUrl)) {
                                SaveAbnormalUrl.saveExceptionalUrl(nextUrl, URL_GRAND_THREE, SITE);
                            }
                        } catch (Exception ex) {
                            LOG.warn(
                                    "An error occurs when the data into the database" + nextUrl + "grand:3 form yichen",
                                    ex);
                        }
                        LOG.warn("an error occurs when turning page" + nextUrl + "grand:3 from yichen", e);
                    }
                }
            }
        }
    }

    public void getText(Elements divNode, Elements imgNodes, String url, ListUtil list, List<String> name) {
        List<Element> divNodes = new ArrayList<Element>();
        for (int i = 0; i < divNode.size(); i++) {
            String temp = divNode.get(i).select("img").attr("src");
            if (temp.contains("http")) {
                continue;
            } else {
                divNodes.add(divNode.get(i));
            }
        }
        if (divNodes.size() / 2 != imgNodes.size()) {
            return;
        }
        boolean flag = false;
        for (int j = 0; j < divNodes.size(); j++) {// 代表一页用户
            if (j % 2 == 0) {
                String divText = divNodes.get(j).text();
                String imgText = "";
                String docText = "";
                if (imgNodes.size() > j / 2) {
                    imgText = imgNodes.get(j / 2).parent().html();
                    docText = imgNodes.get(j / 2).parent().text();
                } else {
                    continue;
                }
                List<String> telphone = StockTradeUtil.getTelphone(docText);
                List<String> cardNumber = StockTradeUtil.getCardNumber(docText);
                if (RightSplitUtil.foundCompreWithExist(list.getListTelphone(), telphone)
                        || RightSplitUtil.foundCompreWithExist(list.getCardNo(), cardNumber)) {
                    continue;
                }
                int size = filter.size();
                for (int n = 0; n < telphone.size(); n++) {
                    filter.put(telphone.get(n), "filter");
                }
                for (int m = 0; m < cardNumber.size(); m++) {
                    filter.put(cardNumber.get(m), "filter");
                }
                if (filter.size() == size + telphone.size() + cardNumber.size()) {
                    analysis(docText, divText, imgText, url, name);
                } else {
                    continue;
                }
                analysis(docText, divText, imgText, url, name);
            }
        }
    }

    public List<String> getBankType(String msg, List<String> cardNumber) {
        allMassage.clear();
        return StockTradeUtil.getBankType(msg, cardNumber);
    }

    public void analysis(String docText, String leftText, String rightText, String url, List<String> name) {
        getPropert();
        TradeStock tradeStock = new TradeStock();
        String accountMsg[] = LeftSplitUtil.getUserMassage(leftText,
                providerMap.get("provider").getReferenceInformation());
        tradeStock = LeftSplitUtil.getLeftUserMassage(tradeStock, accountMsg, url);
        tradeStock = LeftSplitUtil.getUserMassages(tradeStock, docText);
        String temp = tradeStock.getOnlineName() + tradeStock.getUserName();
        if (StockTradeUtil.isCompare(name, temp)) {
            return;
        }
        if (StockTradeUtil.isCompare(oname, temp)) {
            return;
        }
        oname.add(temp);
        String personalMsg[] = rightText.replaceAll("\u00A0", " ").split(" ");
        allMassage = Lists.newArrayList(personalMsg);
        boolean flag = false, status = false;
        List<String> telphone = new ArrayList<String>();
        telphone = StockTradeUtil.getTelphone(docText);
        if (telphone.size() != 0) {
            flag = true;
        }
        List<String> cardNumber = StockTradeUtil.getCardNumber(docText);
        if (cardNumber.size() != 0) {
            status = true;
        }
        telphone = removeListRepeat(telphone);
        cardNumber = removeListRepeat(cardNumber);
        List<String> depositMassage = RightSplitUtil.openAccountMassage(rightText, cardNumber);
        List<String> bankType = getBankType(rightText, cardNumber);
        ListUtil listUtil = new ListUtil();
        listUtil.setTelphone(telphone);
        listUtil.setCardNo(cardNumber);
        listUtil.setDepositBank(depositMassage);
        listUtil.setBankType(bankType);
        listUtil.setFlag(flag);
        listUtil.setStatus(status);
        List<TradeMassage> tm = RightSplitUtil.getUserMassage(listUtil);
        TradeResult tradeResult = new TradeResult();
        tradeResult.setTradeMassage(tm);
        tradeResult.setTradeStock(tradeStock);
        try {
            tradeService.addStockTrade(tradeResult);
        } catch (Exception e) {
            LOG.warn("An error occours when saving all data", e);
        }
    }

    public List<String> removeListRepeat(List<String> list) {
        Set<String> set = new HashSet<String>();
        List<String> newList = new ArrayList<String>();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        return newList;
    }
}
