package com.zx.crawler.stock.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.collect.Maps;
import com.zx.crawler.stock.entity.Provider;
import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;
import com.zx.stock.datamodel.StockOrigin;

public class NsTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<String> textNode = null;
    private static List<StockOrigin> stockInfo = null;
    private static final int LINE_LENGTH = 20;
    private static final int TITLE_LENGTH = 5;
    private static final String KEY_WORD = "申购总量";

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("ns_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("ns_market")));
            provider.setFirstKey(props.getProperty("ns_firstKey"));
            provider.setSecondKey(props.getProperty("ns_secondKey"));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            throw new RuntimeException("读取配置文件出错!", e);
        }
    }

    @Override
    public List<StockModelInfo> analylizeStock() {
        List<StockModelInfo> smi = new ArrayList<StockModelInfo>();
        for (int i = 0; i < textNode.size(); i++) {
            String rows = "";
            StockModelInfo stockModelInfos = new StockModelInfo();
            stockModelInfos.setIsDel(false);
            stockModelInfos.setMarket(providerMap.get("provider").getMarket());
            stockModelInfos.setStockCode(stockInfo.get(i).getStockcode());
            String stockTextArray[] = textNode.get(i).split(" ");
            if (stockTextArray[0].equals("")) {
                continue;
            }
            double beginPrice = 0.0;
            String firstKey = providerMap.get("provider").getFirstKey();
            String secondKey = providerMap.get("provider").getSecondKey();
            for (int j = 0; j < stockTextArray.length; j++) {
                if (stockTextArray[j].equals(firstKey) || stockTextArray[j].equals(secondKey)) {
                    beginPrice = Double.parseDouble(stockTextArray[j + 1]);
                    break;
                }
                if (stockTextArray[j].contains(firstKey) || stockTextArray[j].contains(secondKey)) {
                    String str = "";
                    if (stockTextArray[j].contains(firstKey)) {
                        boolean flag = false;
                        for (int m = 0; m < stockTextArray[j].length(); m++) {
                            if (stockTextArray[j].charAt(m) >= '0' && stockTextArray[j].charAt(m) <= '9'
                                    || stockTextArray[j].charAt(m) == '.') {
                                str += stockTextArray[j].charAt(m);
                                flag = true;
                            }
                        }
                        if (!flag) {
                            for (int m = 0; m < stockTextArray[j + 1].length(); m++) {
                                if (stockTextArray[j + 1].charAt(m) >= '0' && stockTextArray[j + 1].charAt(m) <= '9'
                                        || stockTextArray[j + 1].charAt(m) == '.') {
                                    str += stockTextArray[j + 1].charAt(m);
                                }
                            }
                        }
                    } else {
                        for (int m = 0; m < stockTextArray[j].length(); m++) {
                            if (stockTextArray[j].charAt(m) >= '0' && stockTextArray[j].charAt(m) <= '9'
                                    || stockTextArray[j].charAt(m) == '.') {
                                str += stockTextArray[j].charAt(m);
                            }
                        }
                    }
                    beginPrice = Double.parseDouble(str);
                    break;
                }
            }
            for (int j = 0; j < stockTextArray.length;) {
                if (stockTextArray[j].contains("：")) {
                    if (stockTextArray[j].split("：").length > 1) {
                        rows += "<tr><td style='width:50%;'>" + stockTextArray[j].split("：")[0] + "</td>";
                        rows += "<td>" + stockTextArray[j].split("：")[1] + "<td></tr>";
                    } else {
                        rows += "<tr><td colspan='2' style='line-height: 20px;'>" + stockTextArray[j].replace("：", "")
                                + "<td></tr>";
                    }
                    rows += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                    j++;
                    continue;
                }
                if (stockTextArray[j].contains(KEY_WORD) && stockTextArray[j].length() > TITLE_LENGTH) {
                    rows += "<tr><td style='width:50%;'>" + KEY_WORD + "</td>";
                    rows += "<td>" + stockTextArray[j].split(KEY_WORD)[1] + "</td></tr>";
                    rows += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                    j++;
                    continue;
                }
                if (stockTextArray[j].length() > LINE_LENGTH) {
                    rows += "<tr><td colspan='2' style='line-height: 20px;'>" + stockTextArray[j] + "<td></tr>";
                    rows += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                    j++;
                    continue;
                } else {
                    if (j == stockTextArray.length - 1) {
                        rows += "<tr><td colspan='2' style='line-height: 20px;'>" + stockTextArray[j].replace("：", "")
                                + "<td></tr>";
                        rows += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                        j++;
                    } else {
                        if (stockTextArray[j + 1].length() > LINE_LENGTH) {
                            rows += "<tr><td colspan='2' style='line-height: 20px;'>" + stockTextArray[j] + "<td></tr>";
                            rows += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                            j++;
                        } else {
                            rows += "<tr><td style='width:50%;'>" + stockTextArray[j] + "</td>";
                            rows += "<td>" + stockTextArray[j + 1] + "<td></tr>";
                            rows += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                            j = j + 2;
                        }
                        continue;
                    }
                }
            }
            stockModelInfos.setStockDescription(rows);
            stockModelInfos.setUpdateDate(new Date());
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
            for (int i = 0; i < stockInfo.size(); i++) {
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode())
                        .get();
                String str = doc.body().html().replace("&nbsp;", "");
                doc = Jsoup.parse(str);
                str = doc.body().text();
                textNode.add(str);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
