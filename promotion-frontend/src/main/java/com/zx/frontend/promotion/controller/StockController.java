package com.zx.frontend.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zx.datamodels.market.bean.entity.StockInfo;
import com.zx.modules.market.service.StockInfoService;

@Controller
@RequestMapping(value = "stock/")
public class StockController {
    @Autowired
    private StockInfoService stockInfoService;

    @RequestMapping(value = "show")
    public String show(@RequestParam("marketId") int marketId, @RequestParam("stockCode") String stockCode,
            ModelMap model) {
        StockInfo stockInfo = stockInfoService.findStockInfo(marketId, stockCode);
        model.put("stockInfo", stockInfo);
        return "/pages/stock/stock";
    }
}
