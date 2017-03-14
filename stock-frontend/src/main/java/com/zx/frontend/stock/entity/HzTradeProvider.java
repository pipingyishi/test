package com.zx.frontend.stock.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.collect.Maps;
import com.zx.frontend.stock.util.NameValuePair;
import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;
import com.zx.stock.datamodel.StockOrigin;

public class HzTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<String> textNode = null;
    private static List<String> code = null;
    private static List<NameValuePair> pairs = null;
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("hz_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("hz_market")));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            throw new RuntimeException("读取配置文件出错!", e);
        }
    }

    @Override
    public List<StockModelInfo> analylizeStock() {
        List<StockModelInfo> smi = new ArrayList<StockModelInfo>();
        pairs = new ArrayList<NameValuePair>();
        for (int i = 0; i < textNode.size(); i++) {
            if (!effectiveCode(stockInfo.get(i).getStockcode())) {
                continue;
            }
            StockModelInfo stockModelInfos = new StockModelInfo();
            stockModelInfos.setIsDel(false);
            stockModelInfos.setMarket(providerMap.get("provider").getMarket());
            stockModelInfos.setStockCode(stockInfo.get(i).getStockcode());
            stockModelInfos.setUpdateDate(new Date());
            String stockTextArray[] = textNode.get(i).split(" ");
            for (int j = 0; j < stockTextArray.length; j++) {
                NameValuePair pair = new NameValuePair();
                if (stockTextArray[j].split(SPLIT_SIGN_TWO).length == 2) {
                    pair.setName(stockTextArray[j].split(SPLIT_SIGN_TWO)[0].replace(SPLIT_SIGN_ONE, ""));
                    pair.setValue(stockTextArray[j].split(SPLIT_SIGN_TWO)[1]);
                } else {
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(stockTextArray[j]);
                }
                pairs.add(pair);
            }
            String table = createTable();
            stockModelInfos.setStockDescription(table);
            pairs.clear();
            smi.add(stockModelInfos);
        }
        return smi;
    }

    @Override
    public void crawContent() {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        try {
            StockService stockService = (StockService) context.getBean("stockService");
            stockInfo = stockService.selectStock(providerMap.get("provider").getMarket());
            textNode = new ArrayList<String>();
            code = new ArrayList<String>();
            for (int i = 0; i < stockInfo.size(); i++) {
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode())
                        .get();
                String str = doc.body().html().replace("&nbsp;", "");
                if (str.equals("")) {
                    code.add(stockInfo.get(i).getStockcode());
                }
                doc = Jsoup.parse(str);
                str = doc.body().text();
                textNode.add(str);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String createTable() {
        String table = "";
        for (int i = 0; i < pairs.size(); i++) {
            String name = Jsoup.parse(pairs.get(i).getName()).body().text().replaceAll("\u00A0", " ").trim();
            String value = Jsoup.parse(pairs.get(i).getValue()).body().text().replaceAll("\u00A0", " ").trim();
            if (pairs.get(i).getName() != null) {
                if (pairs.get(i).getName().equals(LIST_ANALYSIS_ONE)) {
                    if (value.equals("")) {
                        continue;
                    } else {
                        table += "<tr><td colspan='2'>" + value.replace("【", "").replace("】", "") + "</td></tr>";
                        table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                    }
                } else {
                    if (!(value.equals("") || name.equals(""))) {
                        table += "<tr><td style='width:50%;'>" + name + "</td>";
                        table += "<td>" + value + "</td></tr>";
                        table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                    } else {
                        continue;
                    }
                }
            }
        }
        return table;
    }

    public boolean effectiveCode(String ecode) {
        for (int m = 0; m < code.size(); m++) {
            if (ecode.equals(code.get(m))) {
                return false;
            }
        }
        return true;
    }
}
