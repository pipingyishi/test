package com.zx.modules.stock.service.impl;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.zx.datamodels.stock.bean.StockModelInfo;
import com.zx.modules.stock.service.StockService;
import com.zx.promotion.mybatis.dao.ZxStockInfoMapper;
import com.zx.promotion.mybatis.entity.ZxStockInfo;
import com.zx.promotion.mybatis.entity.ZxStockInfoExample;
import com.zx.promotion.mybatis.entity.ZxStockInfoExample.Criteria;

@Repository
@Service("stockService")
public class StockServiceImpl implements StockService {
	@Autowired
	private ZxStockInfoMapper zxStockInfoMapper;

	@Override
	public List<StockModelInfo> selectStock(int market) {
		ZxStockInfoExample example = new ZxStockInfoExample();
		Criteria criteria = example.createCriteria();
		criteria.andMarketEqualTo(market);
		List<ZxStockInfo> stockinfos = zxStockInfoMapper.selectByExample(example);
		List<StockModelInfo> result = Lists.newArrayListWithExpectedSize(stockinfos.size());
		StockModelInfo sm = null;
		for (ZxStockInfo rs : stockinfos) {
			sm = new StockModelInfo();
			BeanUtils.copyProperties(rs, sm);
			result.add(sm);
		}
		return result;
	}

	@Override
	public boolean updateStock(List<StockModelInfo> dtos) {
		for (int i = 0, size = dtos.size(); i < size; i++) {
			ZxStockInfo zs = new ZxStockInfo();
			ZxStockInfoExample example = new ZxStockInfoExample();
			Criteria criteria = example.createCriteria();
			StockModelInfo stockModelInfo = dtos.get(i);
			criteria.andMarketEqualTo(stockModelInfo.getMarket());
			criteria.andStockCodeEqualTo(stockModelInfo.getStockCode());
			zs.setStockName(stockModelInfo.getStockName());
			zs.setReserveAmount(stockModelInfo.getReserveAmount());
			zs.setBeginPrice(stockModelInfo.getBeginPrice());
			zs.setHasF10(stockModelInfo.getHasF10());
			zs.setPublishDate(stockModelInfo.getPublishDate());
			zs.setIsDel(stockModelInfo.getIsDel());
			zs.setModDate(stockModelInfo.getModDate());
			zs.setF10(stockModelInfo.getF10());
			int flage = zxStockInfoMapper.updateByExampleSelective(zs, example);
			if (flage == 0) {
				return false;
			}
		}
		return true;
	}
}
