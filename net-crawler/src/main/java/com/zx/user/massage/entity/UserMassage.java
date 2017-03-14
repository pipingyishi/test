package com.zx.user.massage.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.collect.Maps;
import com.zx.configure.util.CrawlerPropertieProvide;
import com.zx.crawler.stock.util.CrawUserMassageThread;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.crawler.stock.util.LeftSplitUtil;
import com.zx.crawler.stock.util.ListUtil;
import com.zx.crawler.stock.util.MultiThreadParameter;
import com.zx.crawler.stock.util.ParseUtil;
import com.zx.crawler.stock.util.RightSplitUtil;
import com.zx.crawler.stock.util.StockTradeUtil;
import com.zx.modules.trade.service.TradeService;
import com.zx.modules.trade.service.UserUrlService;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeResult;
import com.zx.trade.datamodel.TradeStock;
import com.zx.trade.datamodel.UserUrlMassage;

public class UserMassage extends AbsUserMassage {
    private static Logger LOG = LoggerFactory.getLogger(UserMassage.class);
    private static final String PROPERTY_NAME = "trade-stock.properties";
    private static Map<String, CrawlerPropertieProvide> providerMap = null;
    private static final String SITE = "一尘";
    private static TradeService tradeService = null;
    private static Map<String, String> filter = new HashMap<String, String>();
    private static List<String> oname = new ArrayList<String>();

    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        tradeService = (TradeService) context.getBean("tradeService");
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            CrawlerPropertieProvide provider = new CrawlerPropertieProvide();
            provider.setBoardId(props.getProperty("board_id"));
            provider.setId(props.getProperty("id"));
            provider.setStar(props.getProperty("star"));
            provider.setThreadNum(props.getProperty("user_thread"));
            provider.setReferenceInformation(props.getProperty("reference_information"));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            LOG.warn("read profiles from yichen", e);
        }
    }

    @Override
    public void selectId(UserUrlService userUrlService) {
        ListUtil list = InterceptBlockUtil.existDataBaseData(tradeService);
        int offset = 0, limit = 1000, count = 0;// limit表示每一页有多少条记录
        while (true) {
            List<UserUrlMassage> urlIds = userUrlService.selectId(offset, limit);
            if (urlIds.size() == 0) {
                break;
            }
            MultiThreadParameter mtp = new MultiThreadParameter();
            mtp.setOffset(offset);
            mtp.setLimit(offset + limit);
            getUrl(urlIds, list, list.getName(), mtp);
            count++;
            offset = count;
            offset *= limit;
        }
    }

    public void getUrl(List<UserUrlMassage> urlIds, ListUtil list, List<String> name, MultiThreadParameter mtp) {
        List<String> urlList = new ArrayList<String>();
        String url = "";
        CrawlerPropertieProvide provide = providerMap.get("provider");
        for (int i = 0; i < urlIds.size(); i++) {
            UserUrlMassage userUrlMassage = urlIds.get(i);
            url = provide.getBoardId() + userUrlMassage.getBoardId() + provide.getId() + userUrlMassage.getUrlId();
            urlList.add(url);
        }
        mtp.setUrl(urlList);
        mtp.setAbnormalUrl(list.getAbnormalUrls());
        mtp.setList(list);
        mtp.setName(name);
        mtp.setFlip(provide.getStar());
        mtp.setId(urlIds.get(1000 - 1).getId());
        multiThreadHandle(mtp);
    }

    public void multiThreadHandle(MultiThreadParameter mtp) {
        int threadNum = ParseUtil.safeParseInt(providerMap.get("provider").getThreadNum());
        mtp.setThread(threadNum);
        for (int i = 1; i <= threadNum; i++) {
            new CrawUserMassageThread(i, mtp).start();
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
                List<String> telphone = getTelphone(docText);
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
            }
        }
    }

    public List<String> getBankType(String msg, List<String> cardNumber) {
        return StockTradeUtil.getBankType(msg, cardNumber);
    }

    public List<String> getTelphone(String rightText) {
        return StockTradeUtil.getTelphone(rightText);
    }

    public void analysis(String docText, String leftText, String rightText, String url, List<String> name) {
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
        boolean flag = false, status = false;
        List<String> telphone = new ArrayList<String>();
        telphone = getTelphone(docText);
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
