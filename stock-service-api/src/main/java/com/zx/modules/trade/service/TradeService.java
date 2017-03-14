package com.zx.modules.trade.service;

import java.util.List;

import com.zx.trade.datamodel.AbnormalUrl;
import com.zx.trade.datamodel.StockTrade;
import com.zx.trade.datamodel.StockTradeMassage;
import com.zx.trade.datamodel.TradeMassage;
import com.zx.trade.datamodel.TradeResult;
import com.zx.trade.datamodel.TradeSrtockResult;
import com.zx.trade.datamodel.TradeStock;
import com.zx.trade.datamodel.ZxQueryResult;

public interface TradeService {
    public boolean  addStockTrade(TradeResult dots);
    
   public ZxQueryResult selectUrl(String tradePlatform);
   
   public List<TradeMassage> selectMassage();
   
   public boolean saveAbnormalUrl(AbnormalUrl dtos);

   public boolean addUserMassage(TradeSrtockResult dots);
   
   public ZxQueryResult queryUrl(String tradePlatform);
   
   public List<AbnormalUrl> selectExisteAbnormalUrl(String tradePlatform);
   
   public List<TradeStock> selectUserMassage(int offset,int limit);
   
   public List<StockTrade> queryUserMassage(int pageNum, int pageSize);
   
   public List<StockTradeMassage> queryUserInfo(int pageNum, int pageSize);
}
