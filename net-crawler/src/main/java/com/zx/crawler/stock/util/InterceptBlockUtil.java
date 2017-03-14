package com.zx.crawler.stock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zx.modules.trade.service.AbnormalUrlService;
import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeStock;
import com.zx.user.merge.entity.PlatformConstants;

public class InterceptBlockUtil {
    private static final int PAGE_SIZE = 1000;
    private static final int PAGE_SIZE_ONE = 200;
    private static final int PAGE_SIZE_TWO = 50;

    public static List<String> getNumberBlock(String str) {
        List<String> idStr = new ArrayList<String>();
        Pattern p = Pattern.compile("\\d{1,}");
        Matcher m = p.matcher(str);
        while (m.find()) {
            idStr.add(m.group());
        }
        return idStr;
    }

    public static List<TradeStock> flipPageQuery(TradeService tradeService) {
        int pageNum = 1;
        List<TradeStock> list = new ArrayList<TradeStock>();
        while (true) {
            List<TradeStock> tradeStock = tradeService.selectUserMassage(pageNum, PAGE_SIZE);
            list.addAll(tradeStock);
            if (tradeStock.isEmpty()) {
                break;
            }
            pageNum++;
        }
        return list;
    }

    public static List<StockTrade> paginationQuery(TradeService tradeService) {
        int pageNum = 1;
        List<StockTrade> list = new ArrayList<StockTrade>();
        while (true) {
            List<StockTrade> stockTrade = tradeService.queryUserMassage(pageNum, PAGE_SIZE_ONE);
            list.addAll(stockTrade);
            if (stockTrade.isEmpty()) {
                break;
            }
            pageNum++;
        }
        return list;
    }

    public static List<StockTradeMassage> trunPageQuery(TradeService tradeService) {
        int pageNum = 1;
        List<StockTradeMassage> list = new ArrayList<StockTradeMassage>();
        while (true) {
            List<StockTradeMassage> stockTradeMassage = tradeService.queryUserInfo(pageNum, PAGE_SIZE_ONE);
            list.addAll(stockTradeMassage);
            if (stockTradeMassage.isEmpty()) {
                break;
            }
            pageNum++;
        }
        return list;
    }

    public static List<AbnormalUrl> selectAbnormalUrl(String tradePlatform, AbnormalUrlService abnormalUrlService) {
        int pageNum = 1;
        List<AbnormalUrl> list = new ArrayList<AbnormalUrl>();
        while (true) {
            List<AbnormalUrl> abnormalUrl = abnormalUrlService.selectAbnormalUrl(tradePlatform, pageNum, PAGE_SIZE_TWO);
            if (abnormalUrl.isEmpty()) {
                break;
            }
            list.addAll(abnormalUrl);
            pageNum++;
        }
        return list;
    }

    public static ListUtil existDataBaseData(TradeService tradeService) {
        List<TradeStock> tradeStocks = flipPageQuery(tradeService);
        List<TradeMassage> tradeMassages = new ArrayList<TradeMassage>();
        List<AbnormalUrl> exceptionalUrl = tradeService.selectExisteAbnormalUrl(PlatformConstants.getSite());
        List<String> name = new ArrayList<String>();
        for (int i = 0; i < tradeStocks.size(); i++) {
            name.add(tradeStocks.get(i).getOnlineName() + tradeStocks.get(i).getUserName());
        }
        ListUtil list = new ListUtil();
        list = list.getListMassage(tradeStocks, tradeMassages, exceptionalUrl);
        list.setName(name);
        return list;
    }
}
