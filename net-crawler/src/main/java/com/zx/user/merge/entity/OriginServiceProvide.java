package com.zx.user.merge.entity;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.zx.bean.entity.BankCardMassage;
import com.zx.bean.entity.UserInformation;
import com.zx.crawler.stock.util.InterceptBlockUtil;
import com.zx.modules.trade.service.TradeService;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeStock;

public class OriginServiceProvide {
    private static TradeService tradeService = null;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        tradeService = (TradeService) context.getBean("tradeService");
    }

    public static UserInformation getUserMassage() {
//        List<TradeStock> tradeStock = InterceptBlockUtil.flipPageQuery(tradeService);
        List<StockTrade> stockTrade = InterceptBlockUtil.paginationQuery(tradeService);
        UserInformation userInfo = new UserInformation();
//        userInfo.setTradeStocks(tradeStock);
        userInfo.setStockTrades(stockTrade);
        return userInfo;
    }

    public static BankCardMassage getBankCardMassage() {
//        List<TradeMassage> tradeMassage = tradeService.selectMassage();
        List<StockTradeMassage> stockTradeMassage = InterceptBlockUtil.trunPageQuery(tradeService);
        BankCardMassage bankCardMassage = new BankCardMassage();
//        bankCardMassage.setTradeMassages(tradeMassage);
        bankCardMassage.setStockTradeMassage(stockTradeMassage);
        return bankCardMassage;
    }
}
