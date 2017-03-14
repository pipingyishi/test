package com.zx.frontend.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;

@Controller
@RequestMapping(value = "stock/")
public class StockController {
    @Autowired
    private StockService stockService;
    @RequestMapping(value = "show")
    public String show(@RequestParam("marketId") int marketId, @RequestParam("stockCode") String stockCode,
            ModelMap model) {
        List<StockModelInfo> listInfo = stockService.selectMassage(marketId, stockCode);
        model.put("listInfo", listInfo);
        return "/pages/stock/stock";
    }
}
