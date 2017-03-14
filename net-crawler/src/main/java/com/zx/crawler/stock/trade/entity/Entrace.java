package com.zx.crawler.stock.trade.entity;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.collect.Maps;
import com.zx.configure.util.StringHandle;
import com.zx.crawler.stock.entity.Provider;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.ListUtil;
import com.zx.crawler.stock.util.PropertieUtil;
import com.zx.crawler.stock.util.SaveAbnormalUrl;
import com.zx.crawler.stock.util.StockTradeUtil;
import com.zx.crawler.stock.util.UserMassage;
import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeSrtockResult;
import com.zx.trade.datamodel.ZxQueryResult;

public class Entrace extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "stock-trade.properties";
    private static Map<String, Provider> providerMap = Maps.newConcurrentMap();
    private static TradeService tradeService = null;
    private static Map<String, String> filter = new HashMap<String, String>();
    private static Logger LOG = LoggerFactory.getLogger(Entrace.class);
    private static final int USER_NAME = 0;
    private static final int REAL_NAME = 1;
    private static final int REGIST_TIME = 2;
    private static final int TREM_VALIDITY = 3;
    private static final int MEMBER_LEVEL = 4;
    private static final int TRADE_TIMES = 5;
    private static final int TRADE_AMOUNT = 6;
    private static final int PRAISE_RATE = 7;
    private static final int EVALUATE_GRAND = 8;
    private static final int PAGE_INDEX_OFFSET = 2;
    private static final int RUL_GRAND_ONE = 1;
    private static final int RUL_GRAND_TWO = 2;
    private static final String REALATIVE_PAGE = "分为";
    private static final String SITE = "互动";
    private static final String NAME = "匿名发帖";
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        tradeService = (TradeService) context.getBean("tradeService");
    }

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            Provider provider = new Provider();
            provider.setHost(props.getProperty("host"));
            provider.setHostNext(props.getProperty("host_next"));
            provider.setHostUser(props.getProperty("host_user"));
            provider.setReferenceInformation(props.getProperty("reference_information"));
            provider.setHostConnect(props.getProperty("host_connect"));
            provider.setTelphone(props.getProperty("telphone"));
            provider.setBankType(props.getProperty("bank_type"));
            provider.setSection(props.getProperty("section"));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            LOG.warn("An error occours when reading profiles", e);
        }
    }

    @Override
    public List<String> crawUrl() {
        List<String> link = new ArrayList<String>();
        String url = "";
        try {
            url = providerMap.get("provider").getHost();
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(20000);
            Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url);
            Elements liNodes = doc.select("div.s-nav").select("li");
            for (int i = 0; i < liNodes.size() - 3; i++) {
                Elements aNodes = liNodes.get(i).select("a");
                for (int j = 0; j < aNodes.size(); j++) {
                    link.add(aNodes.get(j).attr("href"));
                }
            }
        } catch (Exception e) {
            LOG.warn("An error occours when entering main page from hudong:URL{}", url, e);
        }
        return link;
    }

    @Override
    public void crawContent(List<String> link) {
        for (int i = 0; i < link.size(); i++) {
            Document docNext = null;
            String url = link.get(i);
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(20000);
                docNext = Jsoup.parse(conn.getInputStream(), "gbk", url);
            } catch (Exception e) {
                LOG.warn("An error occours when crawing the content:{} section Corresponding to url {} from hudong",
                        (i + 1), url, e);
            }
            if (docNext == null) {
                continue;
            }
            Elements trNode = docNext.select("table.treadlistbox").last().select("tr");// 进入抓取信息的页面
            String totalPages = trNode.last().text().replaceAll("\u00A0", "").replace(" ", "").replace(" ", "");
            int index = totalPages.indexOf(REALATIVE_PAGE);
            totalPages = totalPages.substring(index + PAGE_INDEX_OFFSET);
            for (int j = 0; j < totalPages.length(); j++) {
                if (!Character.isDigit(totalPages.charAt(j))) {
                    index = j;
                    break;
                }
            }
            totalPages = totalPages.substring(0, index);
            int temp = Integer.parseInt(totalPages.trim());
            if (temp == 0 || temp == 1) {
                analysis(temp, url, i + 1);
                continue;
            }
            String nextLink = trNode.last().select("input").first().attr("onclick");
            nextLink = nextLink.replace("\"", "").replace(";", "").replace("'", "");
            index = nextLink.indexOf("=");
            nextLink = nextLink.substring(index + 1);
            nextLink = nextLink.substring(0, nextLink.length() - 1);
            nextLink = StringHandle.splitString(providerMap.get("provider").getSection(), nextLink);
            analysis(temp, nextLink, i + 1);
        }
    }

    public void analysis(int totalPages, String url, int section) {
        List<StockTrade> stockTrade = InterceptBlockUtil.paginationQuery(tradeService);
        List<StockTradeMassage> stockTradeMassage = InterceptBlockUtil.trunPageQuery(tradeService);
        ZxQueryResult rs = new ZxQueryResult();
        rs.setStockTrades(stockTrade);
        rs.setStockTradeMassage(stockTradeMassage);
        List<AbnormalUrl> abnormalUrls = tradeService.selectExisteAbnormalUrl(SITE);
        List<String> exceptionalUrl = new ArrayList<String>();
        for (int i = 0; i < abnormalUrls.size(); i++) {
            exceptionalUrl.add(abnormalUrls.get(i).getAbnormalUrl());
        }
        if (!url.contains("http") && totalPages != 1) {
            url = providerMap.get("provider").getHostNext() + url;
        }
        String commonUrl = url;
        for (int i = totalPages - 1; i >= 0; i--) {
            if (totalPages != 1) {
                url = commonUrl;
                url += i;
            }
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(20000);
                Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url); // 进入某一页
                LOG.info("{} page of {} section corresponding to url：{} from hudong", (i + 1), section, url);
                Elements trNode = doc.select("table.treadlistbox").last().select("tr");
                if (trNode.size() == 2 || trNode.get(1).select("td").size() == 0) {
                    continue;
                }
                List<String> links = new ArrayList<String>();
                for (int j = 1; j < trNode.size(); j++) { // 代表某一页里面的全部链接
                    links.add(trNode.get(j).select("td").first().select("a").attr("href"));
                }
                if (links.size() == 0) {
                    continue;
                }
                getUserName(i + 1, links, rs, exceptionalUrl, section);
            } catch (Exception e) {
                try {
                    if (!StockTradeUtil.isCompare(exceptionalUrl, url)) {
                        SaveAbnormalUrl.saveExceptionalUrl(url, RUL_GRAND_ONE, SITE);
                    }
                } catch (Exception ex) {
                    LOG.warn(
                            "An error occurs when the abnormalUrl into the database: {} page of {} section corresponding to url：grand:1 from hudong",
                            (i + 1), section, ex);
                }
                LOG.warn(
                        "An error occours when crawing a user massage: {} page of {} section  corresponding to url {} grand:1 from hudong",
                        (i + 1), section, url, e);
            }
        }
    }

    /**
     * 一页里面的链接数
     */
    public void getUserName(int page, List<String> links, ZxQueryResult rs, List<String> abnormalUrl, int section) {
        String url = "";
        boolean flag = false;
        List<StockTrade> stockTrades = rs.getStockTrades();
        List<StockTradeMassage> stockTradeMassages = rs.getStockTradeMassage();
        List<String> existUrl = new ArrayList<String>();
        List<String> existTelphone = new ArrayList<String>();
        List<String> existName = new ArrayList<String>();
        List<String> existCardNo = new ArrayList<String>();
        for (int i = 0; i < stockTrades.size(); i++) {
            existUrl.add(stockTrades.get(i).getUrlName());
            existName.add(stockTrades.get(i).getUserName() + stockTrades.get(i).getRealName());
        }
        for (int j = 0; j < stockTradeMassages.size(); j++) {
            existTelphone.add(stockTradeMassages.get(j).getTelphone());
            existCardNo.add(stockTradeMassages.get(j).getCardNo());
        }
        ListUtil listUtil = new ListUtil();
        listUtil.setExistUrl(existUrl);
        listUtil.setExistName(existName);
        listUtil.setExistTelphone(existTelphone);
        listUtil.setAbnormalUrls(abnormalUrl);
        listUtil.setExistCardNo(existCardNo);
        listUtil.setPage(page);
        for (int i = 0; i < links.size(); i++) {
            int size = filter.size();
            filter.put(links.get(i), "filter");
            if (filter.size() == size) {
                continue;
            }
            url = links.get(i);
            if (!links.get(i).contains("http")) {
                url = providerMap.get("provider").getHostNext() + url;
                if (providerMap.get("provider").getHostNext().length() == url.length()) {
                    continue;
                }
            }
            if (StockTradeUtil.isCompare(existUrl, url)) {
                continue;
            }
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(20000);
                Document doc = Jsoup.parse(conn.getInputStream(), "gbk", url);
                if(doc.html().contains("alert('没有找到对应的记录')")){
                    continue;
                }
                listUtil.setLink(url);
                LOG.info(" see massage: {} link of {}  page  of {} section corresponding to url : {} from hudong",
                        (i + 1), page, section, url);
                String userName = doc.select("table.detail").last().select("td.detailleft").select("script")
                        .attr("src");
                userName = userName.split("=")[1];
                if(userName.contains("&")){
                    userName = userName.substring(0, userName.indexOf("&"));
                }
                if (userName.contains(NAME)) {
                    continue;
                }
                userName = URLEncoder.encode(userName, "gb2312");
                ListUtil userMassage = getUserMassage(userName, listUtil);
                if (userMassage != null) {
                    saveUserMassage(userMassage, url);
                } else {
                    continue;
                }
            } catch (Exception e) {
                try {
                    if (!StockTradeUtil.isCompare(abnormalUrl, url)) {
                        SaveAbnormalUrl.saveExceptionalUrl(url, RUL_GRAND_TWO, SITE);
                    }
                } catch (Exception ex) {
                    LOG.warn(
                            "An error occurs when the data into the database : {}  link of {}  page corresponding to url :{}  grand:2 from hudong",
                            (i + 1), page, url, ex);
                }
                LOG.warn(
                        "An error occurs when getting a link inside a page: {}  link of {}  page corresponding to url {} grand:2 from hudong",
                        (i + 1), page, url, e);
            }
        }
    }

    public ListUtil getUserMassage(String userName, ListUtil listUtil) {// listUtil存放一个用户名加网名就可以了
        UserMassage userMassage = new UserMassage();
        PropertieUtil propertieUtil = new PropertieUtil();
        if (listUtil.getPage() == 0) {
            getPropert();
        }
        Provider provider = providerMap.get("provider");
        propertieUtil.setHostConnect(provider.getHostConnect());
        propertieUtil.setHostUser(provider.getHostUser());
        propertieUtil.setReferenceInformation(provider.getReferenceInformation());
        propertieUtil.setTelphone(provider.getTelphone());
        propertieUtil.setBankType(provider.getBankType());
        return userMassage.getUserMassage(userName, propertieUtil, listUtil);
    }

    public void saveUserMassage(ListUtil userInformation, String url) {
        StockTrade stockTrade = new StockTrade();
        List<StockTradeMassage> stockTradeMassage = new ArrayList<StockTradeMassage>();
        stockTrade.setUrlName(url);
        stockTrade.setAddress(userInformation.getAddress());
        stockTrade.setUserName(userInformation.getUserInfo().get(USER_NAME));
        stockTrade.setRealName(userInformation.getUserInfo().get(REAL_NAME));
        stockTrade.setIsDel(false);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (userInformation.getUserInfo().get(REGIST_TIME).matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                Date registDate = format.parse(userInformation.getUserInfo().get(REGIST_TIME));
                stockTrade.setRegistTime(registDate);
            } else {
                stockTrade.setRegistTime(null);
            }
            if (userInformation.getUserInfo().get(TREM_VALIDITY).matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                Date effectiveDate = format.parse(userInformation.getUserInfo().get(TREM_VALIDITY));
                stockTrade.setTermValidity(effectiveDate);
            } else {
                stockTrade.setTermValidity(null);
            }
        } catch (Exception e) {
            LOG.warn("An exception occurs when format date from hudong", e);
        }
        stockTrade.setMemberLevel(userInformation.getUserInfo().get(MEMBER_LEVEL));
        String tradeTimes = userInformation.getUserInfo().get(TRADE_TIMES);
        if (StockTradeUtil.isAllDigit(tradeTimes)) {
            stockTrade.setTradeTimes(Integer.parseInt(userInformation.getUserInfo().get(TRADE_TIMES)));
        } else {
            stockTrade.setTradeTimes(0);
        }
        String tradeAmount = userInformation.getUserInfo().get(TRADE_AMOUNT);
        tradeAmount = tradeAmount.substring(0, tradeAmount.length() - 1);
        if (StockTradeUtil.isAllDigit(tradeAmount)) {
            stockTrade.setTradeAmount(Double.parseDouble(tradeAmount));
        } else {
            stockTrade.setTradeAmount(0.0);
        }
        stockTrade.setPraiseRate(userInformation.getUserInfo().get(PRAISE_RATE));
        stockTrade.setEvaluateGrand(userInformation.getUserInfo().get(EVALUATE_GRAND));
        int size = 0;
        if (userInformation.getCardNo().size() >= userInformation.getTelphone().size()) {
            size = userInformation.getCardNo().size();
        } else {
            size = userInformation.getTelphone().size();
        }
        for (int i = 0; i < size; i++) {
            StockTradeMassage st = new StockTradeMassage();
            if (userInformation.getTelphone().size() > i) {
                st.setTelphone(userInformation.getTelphone().get(i));
            } else {
                st.setTelphone(" ");
            }
            if (userInformation.getBankType().size() > i) {
                st.setBankType(userInformation.getBankType().get(i));
            } else {
                st.setBankType(" ");
            }
            if (userInformation.getCardNo().size() > i) {
                st.setCardNo(userInformation.getCardNo().get(i));
            } else {
                st.setCardNo(" ");
            }
            if (userInformation.getDepositBank().size() > i) {
                st.setDepositBank(userInformation.getDepositBank().get(i));
            } else {
                st.setDepositBank(" ");
            }
            st.setIsDel(false);
            stockTradeMassage.add(st);
        }
        TradeSrtockResult rs = new TradeSrtockResult();
        rs.setStockTrade(stockTrade);
        rs.setStockTradeMassages(stockTradeMassage);
        try {
            tradeService.addUserMassage(rs);
        } catch (Exception e) {
            LOG.warn("An error occurs when the all data into the database from hudong", e);
        }
    }
}
