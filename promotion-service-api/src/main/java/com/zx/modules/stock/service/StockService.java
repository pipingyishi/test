package com.zx.modules.stock.service;

import java.util.List;

import com.zx.datamodels.stock.bean.StockModelInfo;
import com.zx.datamodels.stock.bean.StockOrigin;

public interface StockService {
	
	public List<StockModelInfo> selectStock(int market);
	
	public boolean updateStock(List<StockModelInfo> dtos);
}
