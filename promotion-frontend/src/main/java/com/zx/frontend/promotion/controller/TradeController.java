package com.zx.frontend.promotion.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zx.datamodels.utils.MarketUtils;
import com.zx.frontend.promotion.entity.Trade;
import com.zx.modules.promotion.service.TradeService;
import com.zx.promotion.datamodel.TradeTable;

@Controller
@RequestMapping(value = "trade/")
public class TradeController {
	private static final String STOCK_ELECTRONIC_INFORMATION = "pages/trade/paging::paging";
	private static final String tradeFlag = "未成交";
	private static final int DECIMAL_CARRY_ONE=10;
	@Autowired
	private TradeService tradeService;

	/**
	 * 一览页面
	 * 
	 */
	@RequestMapping(value = "show")
	public String show(@ModelAttribute("trade") Trade trade, ModelMap model) {
		if (trade.getTradeType() == null) {
			model.put("tradeType", "现货盘");
		} else {
			model.put("tradeType", trade.getTradeType());
		}
		return "/pages/trade/show";
	}

	/**
	 * 用户信息输入页面
	 */
	@RequestMapping(value = "input")
	public String input(ModelMap model) {
		return "/pages/trade/input";
	}

	/**
	 * 
	 * 将用户输入的信息保存到数据库中
	 */
	@RequestMapping(value = "addPage", method = { RequestMethod.POST, RequestMethod.GET })
	public String addTrade(@ModelAttribute("trade") Trade trade, ModelMap model) {
		int tradeAmount = Integer.parseInt(trade.getTradeAmount());
		double tradePrice = Double.parseDouble(trade.getTradePrice());
		String marketValue[] = trade.getTradeMarketValue().split("万");
		double tradeMarketValue = Double.parseDouble(marketValue[0]);
		TradeTable tradeTable = new TradeTable();
		tradeTable.setTradeUnit(trade.getTradeUnit());
		tradeTable.setTradeAmount(tradeAmount);
		tradeTable.setTradeCname(trade.getTradeCname());
		tradeTable.setTradeTelphone(trade.getTradeTelphone());
		tradeTable.setTradePname(trade.getTradePname());
		tradeTable.setTradePrice(tradePrice);
		tradeTable.setTradeMarketValue(tradeMarketValue);
		tradeTable.setTradeType(trade.getTradeType());
		tradeTable.setTradeVarieties(trade.getTradeVarieties());
		tradeTable.setTradeFlag(tradeFlag);
		int flag = tradeService.addTradeData(tradeTable);
		if (flag != 0) {
			model.put("tradeType", trade.getTradeType());
			return "redirect:/trade/show";
		} else {
			return "/pages/trade/input";
		}
	}

	/**
	 * 查询一页用户信息
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "page")
	public String selectPageData(@RequestParam("pageOf") int pageOf, @RequestParam("tradeType") String tradeType,
			ModelMap model) {
		int pageNumber = 10;
		pageOf *= pageNumber;
		List<TradeTable> pageList = tradeService.selectTradeData(tradeType, pageOf, pageNumber);
		List<TradeTable> productList=new ArrayList<TradeTable>();
		for(int i=0;i<pageList.size();i++){
		    TradeTable tradeTable=pageList.get(i);
		    tradeTable.setTradePriceStr(MarketUtils.toTotalValueStrWithNoPrefix(tradeTable.getTradePrice()));
		    productList.add(tradeTable);
		}
		model.put("pageList", productList);
		return STOCK_ELECTRONIC_INFORMATION;
	}
}
