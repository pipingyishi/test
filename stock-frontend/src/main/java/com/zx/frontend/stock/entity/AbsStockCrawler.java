package com.zx.frontend.stock.entity;

import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;

public abstract class AbsStockCrawler {
    public AbsStockCrawler() {

    }

    public void save(List<StockModelInfo> dtos) {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        StockService stockService = (StockService) context.getBean("stockService");
        stockService.batchInsertUpdate(dtos);
    }

    public void execute() {
        getPropert();
        crawContent();
        List<StockModelInfo> dtos = analylizeStock();
        save(dtos);// 对象保存到数据库
    }

    abstract public void getPropert();

    abstract public List<StockModelInfo> analylizeStock();

    abstract public void crawContent();

}
