package com.zx.crawler.stock.entity;

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
import com.zx.crawler.stock.util.NameValuePair;
import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;
import com.zx.stock.datamodel.StockOrigin;

public class YjsTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<String> textNode = null;
    private static List<NameValuePair> pairs = null;
    private static final String KEY_WORD = "藏品特点";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String LIST_ANALYSIS_TWO = "allRight";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";
    private static final String LINK_SIGN = "&nbsp;&nbsp;&nbsp;&nbsp;";
    private static final int TITLE_LENGTH = 5;
    private static final int SIGN_COUNT = 2;
    private static final int CODE_LENGTH = 6;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("yjs_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("yjs_market")));
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
            StockModelInfo stockModelInfos = new StockModelInfo();
            stockModelInfos.setIsDel(false);
            stockModelInfos.setMarket(providerMap.get("provider").getMarket());
            stockModelInfos.setStockCode(stockInfo.get(i).getStockcode());
            stockModelInfos.setUpdateDate(new Date());
            String stockTextArray[] = textNode.get(i).split(" ");
            if (stockTextArray[0].equals("") || stockTextArray[0].equals(" ")) {
                continue;
            }
            boolean flag = false;
            for (int j = 0; j < stockTextArray.length; j++) {
                if (stockTextArray[j].equals("　")) {
                    continue;
                }
                NameValuePair pair = new NameValuePair();
                if (flag) {
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(stockTextArray[j]);
                    pairs.add(pair);
                    continue;
                }
                int count = 0;
                for (int k = 0; k < stockTextArray[j].length(); k++) {
                    if (stockTextArray[j].charAt(k) == '：') {
                        count++;
                    }
                }
                if (count >= SIGN_COUNT) {// 代表一行，不用拆分
                    pair.setName(LIST_ANALYSIS_ONE);
                    int num = 0;
                    String value = "";
                    for (int n = 0; n < stockTextArray[j].length(); n++) {
                        if (stockTextArray[j].charAt(n) == '：' || stockTextArray[j].charAt(n) == ':') {
                            num++;
                            if (num == 2) {
                                value += stockTextArray[j].substring(0, n - 4) + LINK_SIGN
                                        + stockTextArray[j].substring(n - 4);
                                num = 0;
                                break;
                            }
                        }
                    }
                    for (int n = 0; n < value.length(); n++) {
                        if (value.charAt(n) == '：' || value.charAt(n) == ':') {
                            num++;
                            if (num == 3) {
                                value += value.substring(0, n - 4) + LINK_SIGN + value.substring(n - 4);
                                break;
                            }
                        }
                    }
                    pair.setValue(value);
                    pairs.add(pair);
                    continue;
                }
                if (stockTextArray[j].contains(SPLIT_SIGN_FOUR)) {
                    pair.setName(split(stockTextArray[j], SPLIT_SIGN_FOUR).split(SPLIT_SIGN_THREE)[0]);
                    pair.setValue(split(stockTextArray[j], SPLIT_SIGN_FOUR).split(SPLIT_SIGN_THREE)[1]);
                } else if (stockTextArray[j].contains(SPLIT_SIGN_THREE)) {
                    pair.setName(split(stockTextArray[j], SPLIT_SIGN_THREE).split(SPLIT_SIGN_THREE)[0]);
                    pair.setValue(split(stockTextArray[j], SPLIT_SIGN_THREE).split(SPLIT_SIGN_THREE)[1]);
                } else {
                    if (stockTextArray[j].length() <= TITLE_LENGTH || stockTextArray[j].contains(SPLIT_SIGN_ONE)
                            && !stockTextArray[j].contains(SPLIT_SIGN_THREE)
                            && !stockTextArray[j].contains(SPLIT_SIGN_FOUR)) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(stockTextArray[j]);
                    } else {
                        pair.setName(LIST_ANALYSIS_TWO);
                        pair.setValue(stockTextArray[j]);
                    }
                }
                pairs.add(pair);
                if (j < stockTextArray.length - 2 && stockTextArray[j + 1].contains(KEY_WORD)) {
                    flag = true;
                }
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
            for (int i = 0; i < stockInfo.size(); i++) {
                if (stockInfo.get(i).getStockcode().length() > CODE_LENGTH) {
                    continue;
                }
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost()
                        + stockInfo.get(i).getStockcode() ).get();
                String str = doc.body().html().replace("&nbsp;", "");
                doc = Jsoup.parse(str);
                str = doc.body().text();
                textNode.add(str);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String split(String string, String str) {
        String name = "";
        String value = "";
        if (string.split(str).length == 1 || !string.contains(str)) {
            name = LIST_ANALYSIS_ONE;
            if (string.contains(SPLIT_SIGN_ONE) && string.contains(SPLIT_SIGN_TWO)) {
                value = string.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, "");
            } else {
                value = string;
            }
        } else {
            if (string.split(str)[0].contains(SPLIT_SIGN_ONE) && string.split(str)[0].contains(SPLIT_SIGN_TWO)) {
                name = string.split(str)[0].replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, "");
            } else {
                name = string.split(str)[0];
            }
            value = string.split(str)[1];
        }
        return name + SPLIT_SIGN_THREE + value;
    }

    public String createTable() {
        String table = "";
        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).getName() != null) {
                if (pairs.get(i).getName().equals(LIST_ANALYSIS_ONE)) {
                    table += "<tr><td colspan='2'>" + pairs.get(i).getValue().replace(SPLIT_SIGN_ONE, "")
                            .replace(SPLIT_SIGN_TWO, "").replace(SPLIT_SIGN_FOUR, "") + "</td></tr>";
                    table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                } else if (pairs.get(i).getName().equals(LIST_ANALYSIS_TWO)) {
                    table += "<tr><td></td><td>" + pairs.get(i).getValue() + "</td></tr>";
                    table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                } else {
                    table += "<tr><td style='width:50%;'>" + pairs.get(i).getName() + "</td>";
                    table += "<td >" + pairs.get(i).getValue() + "</td></tr>";
                    table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                }
            }
        }
        return table;
    }
}
