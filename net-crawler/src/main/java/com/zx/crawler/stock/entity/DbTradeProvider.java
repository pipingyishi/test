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

public class DbTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<String> textNode = null;
    private static List<NameValuePair> pairs = null;
    private static final String PUNCTUATIION = "。”……!!!?:";
    private static int amount = 0;
    private static final String KEY_WORD_ONE = "简  介";
    private static final String KEY_WORD_TWO = "藏品特点";
    private static final String KEY_WORD_THREE = "投资价值分析";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("db_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("db_market")));
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
            amount = 0;
            StockModelInfo stockModelInfos = new StockModelInfo();
            stockModelInfos.setIsDel(false);
            stockModelInfos.setMarket(providerMap.get("provider").getMarket());
            stockModelInfos.setStockCode(stockInfo.get(i).getStockcode());
            stockModelInfos.setUpdateDate(new Date());
            String stockTextArray[] = textNode.get(i).split(" ");
            if (stockTextArray[0].equals("")) {
                continue;
            }
            boolean flag = false;
            for (int j = 0; j < stockTextArray.length;) {
                if (stockTextArray[j].equals("　　")) {
                    j++;
                    continue;
                }
                amount = 0;
                NameValuePair pair = new NameValuePair();
                if (flag) {
                    if ((stockTextArray[j].contains(SPLIT_SIGN_FOUR) && stockTextArray[j].length() <= 10)
                            || (stockTextArray[j].length() <= 10)) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(stockTextArray[j].replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, "")
                                .replace(SPLIT_SIGN_FOUR, ""));
                        j++;
                        pairs.add(pair);
                        continue;
                    }
                    if (!PUNCTUATIION.contains(stockTextArray[j].charAt(stockTextArray[j].length() - 1) + "")) { // 没结束
                        String value = "";
                        boolean end = false;
                        int temp = 0;
                        for (int n = j; n < stockTextArray.length; n++) {
                            if (PUNCTUATIION.contains(stockTextArray[n].charAt(stockTextArray[n].length() - 1) + "")) {
                                value += stockTextArray[n];
                                j = n + 1;
                                end = true;
                                break; // 表示找到结束符
                            } else { // 表示一直没有找到结束符
                                value += stockTextArray[n];
                            }
                            temp = n;
                        }
                        if (!end && temp == stockTextArray.length - 1) {
                            j++;
                        }
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(value);
                    } else { // 结束
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(stockTextArray[j]);
                        j++;
                    }
                    pairs.add(pair);
                    continue;
                }
                String next = "";
                if (stockTextArray[j].contains(SPLIT_SIGN_FOUR)) {
                    if (stockTextArray[j].contains("简介：")) {
                        next += stockTextArray[j];
                        for (int m = j + 1; m < stockTextArray.length; m++) {
                            if (!stockTextArray[m].contains(SPLIT_SIGN_FOUR)
                                    && !stockTextArray[m].contains(SPLIT_SIGN_THREE)) {
                                next += stockTextArray[m];
                            } else {
                                amount = 1;
                                j = m;
                                break;
                            }
                        }
                    }
                    if (amount == 1) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(next.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                    } else {
                        pair.setName(split(stockTextArray[j], SPLIT_SIGN_FOUR).split(SPLIT_SIGN_THREE)[0]);
                        pair.setValue(split(stockTextArray[j], SPLIT_SIGN_FOUR).split(SPLIT_SIGN_THREE)[1]);
                        j++;
                    }
                } else if (stockTextArray[j].contains(SPLIT_SIGN_THREE)) {
                    if (stockTextArray[j].contains(KEY_WORD_ONE)) {
                        next += stockTextArray[j];
                        for (int m = j + 1; m < stockTextArray.length; m++) {
                            if (!stockTextArray[m].contains(SPLIT_SIGN_FOUR)
                                    && !stockTextArray[m].contains(SPLIT_SIGN_THREE)) {
                                next += stockTextArray[m];
                            } else {
                                amount = 1;
                                j = m;
                                break;
                            }
                        }
                    }
                    if (amount == 1) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(next.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                    } else {
                        pair.setName(split(stockTextArray[j], SPLIT_SIGN_THREE).split(SPLIT_SIGN_THREE)[0]);
                        pair.setValue(split(stockTextArray[j], SPLIT_SIGN_THREE).split(SPLIT_SIGN_THREE)[1]);
                        j++;
                    }
                } else {
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(stockTextArray[j].replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                    j++;
                }
                pairs.add(pair);
                if (stockTextArray[j].contains(KEY_WORD_TWO) || stockTextArray[j].contains(KEY_WORD_THREE)) {
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
                value = string.split(str)[1];
            } else {
                if (string.split(str).length > 2) {
                    name = string.split(str)[0];
                    value = string.split(str)[2];
                } else {
                    name = string.split(str)[0];
                    value = string.split(str)[1];
                }
            }
        }
        return name + SPLIT_SIGN_THREE + value;
    }

    public String createTable() {
        String table = "";
        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).getName() != null) {
                if (pairs.get(i).getName().equals(LIST_ANALYSIS_ONE)) {
                    table += "<tr><td colspan='2'>" + pairs.get(i).getValue() + "</td></tr>";
                    table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                } else {
                    table += "<tr><td style='width:50%;'>" + pairs.get(i).getName() + "</td>";
                    table += "<td>" + pairs.get(i).getValue() + "</td></tr>";
                    table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                }
            }
        }
        return table;
    }
}
