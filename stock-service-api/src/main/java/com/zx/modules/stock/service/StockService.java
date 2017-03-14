package com.zx.modules.stock.service;

import java.util.List;

import com.zx.stock.datamodel.StockModelInfo;
import com.zx.stock.datamodel.StockOrigin;

public interface StockService {
	
	public List<StockOrigin> selectStock(int market);
	
	public List<StockOrigin> selectStockMassage(int market,String stockCode);
	
	public void batchInsertUpdate(List<StockModelInfo> dtos);
	
	public List <StockModelInfo> selectMassage(int maketId,String stockCode);
}
