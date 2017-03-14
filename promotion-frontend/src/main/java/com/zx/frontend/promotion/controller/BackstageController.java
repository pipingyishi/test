package com.zx.frontend.promotion.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zx.modules.promotion.service.TradeService;
import com.zx.promotion.datamodel.TradeTable;
import com.zx.promotion.tool.ZxQueryResult;

@Controller
@RequestMapping(value = "backstage/")
public class BackstageController {

	private static final String BACKSTAGE_PAGE_INFORMATION = "pages/trade/backstage-page::paging";
	private static final int pageSize = 15;
	@Autowired
	private TradeService tradeService;
	

	/**
	 * 后台页面
	 * 
	 * @param tradeVarietiesValue
	 * @return
	 */
	@RequestMapping(value = "backstage", method = RequestMethod.GET)
	public String backstage() {
		return "/pages/trade/backstage";
	}

	/**
	 * 类型选择
	 */
	@RequestMapping(value = "backstagePage", method = RequestMethod.POST)
	public String tradeStyle(@RequestParam("tradeStyleValue") String tradeStyleValue,
			@RequestParam("tradeVarietiesValue") String tradeVarietiesValue,
			@RequestParam("pageOf") int pageOf, ModelMap model) {
		model.put("currentPage", pageOf);
		pageOf = (pageOf - 1) * pageSize;
		ZxQueryResult result = tradeService.backstagePage(tradeStyleValue, tradeVarietiesValue, pageOf,
				pageSize);
		model.put("result", result);
		return BACKSTAGE_PAGE_INFORMATION;
	}

	/**
	 * 
	 * 删除选中的记录
	 */
	@RequestMapping(value = "deleteRecord", method = RequestMethod.POST)
	@ResponseBody
	public String deleteRecord(@RequestBody Integer[] id) {
		List<Integer> idList = Arrays.asList(id);
		boolean flag = tradeService.deleteRecord(idList);
		if (flag) {
			return "删除成功";
		} else {
			return "删除失败";
		}
	}

	/**
	 * 修改保存的记录
	 */
	@RequestMapping(value = "savaRecord", method = RequestMethod.POST)
	@ResponseBody
	public String saveRecord(@RequestBody TradeTable[] saveData) {
		List<TradeTable> lists = new ArrayList<TradeTable>();
		for (int i = 0; i < saveData.length; i++) {
			lists.add(saveData[i]);
		}
		if (tradeService.saveRecord(lists)) {
			return "保存成功";
		} else {
			return "保存";
		}
	}
}
