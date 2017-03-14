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

public class HbTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<String> textNode = null;
    private static List<String> imgNode = null;
    private static List<NameValuePair> pairs = null;
    private static final String PUNCTUATIION = "。”……!!!?";
    private static final String KEY_WORD_ONE = "邮票介绍";
    private static final String KEY_WORD_TWO = "面值";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String LIST_ANALYSIS_TWO = "allRight";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";
    private static final int LINR_LENGTH = 24;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("hb_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("hb_market")));
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
            stockModelInfos.setMarket(providerMap.get("provider").getMarket());
            stockModelInfos.setStockCode(stockInfo.get(i).getStockcode());
            stockModelInfos.setUpdateDate(new Date());
            stockModelInfos.setIsDel(false);
            String stockTextArray[] = textNode.get(i).split(" ");
            if (stockTextArray[0].equals("")) {
                continue;
            }
            boolean flag = false;
            for (int j = 0; j < stockTextArray.length;) {
                NameValuePair pair = new NameValuePair();
                if (flag) {
                    if (j == stockTextArray.length - 1
                            || stockTextArray[j].contains(SPLIT_SIGN_FOUR) && stockTextArray[j].length() < 8) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(stockTextArray[j]);
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
                if (stockTextArray[j].contains(SPLIT_SIGN_FOUR)) {
                    if (stockTextArray[j].contains(KEY_WORD_TWO)
                            && stockTextArray[j].split(SPLIT_SIGN_FOUR).length == 2) {
                        String temp = stockTextArray[j].split(SPLIT_SIGN_FOUR)[1];
                        boolean b = false;
                        for (int m = 0; m < temp.length(); m++) {
                            if ((temp.charAt(m) >= '0' && temp.charAt(m) < '9')) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) {
                            pair.setName(split(stockTextArray[j] + stockTextArray[j + 1], SPLIT_SIGN_FOUR)
                                    .split(SPLIT_SIGN_THREE)[0]);
                            pair.setValue(split(stockTextArray[j] + stockTextArray[j + 1], SPLIT_SIGN_FOUR)
                                    .split(SPLIT_SIGN_THREE)[1]);
                            j++;
                        }
                    } else {
                        pair.setName(split(stockTextArray[j], SPLIT_SIGN_FOUR).split(SPLIT_SIGN_THREE)[0]);
                        pair.setValue(split(stockTextArray[j], SPLIT_SIGN_FOUR).split(SPLIT_SIGN_THREE)[1]);
                    }
                } else if (stockTextArray[j].contains(SPLIT_SIGN_THREE)) {
                    pair.setName(split(stockTextArray[j], SPLIT_SIGN_THREE).split(SPLIT_SIGN_THREE)[0]);
                    pair.setValue(split(stockTextArray[j], SPLIT_SIGN_THREE).split(SPLIT_SIGN_THREE)[1]);
                } else if (stockTextArray[j].contains(SPLIT_SIGN_ONE) && stockTextArray[j].length() < LINR_LENGTH) {
                    pair.setName(stockTextArray[j].split(SPLIT_SIGN_TWO)[0].replace(SPLIT_SIGN_ONE, ""));
                    pair.setValue(stockTextArray[j].split(SPLIT_SIGN_TWO)[1]);
                } else {
                    pair.setName(LIST_ANALYSIS_TWO);
                    pair.setValue(stockTextArray[j]);
                }
                pairs.add(pair);
                if (stockTextArray[j].contains(KEY_WORD_ONE)) {
                    flag = true;
                }
                j++;
            }
            String table = createTable();
            table = "<tr><td colspan='2''><center>" + imgNode.get(i) + "</center></td></tr>" + table;
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
            imgNode = new ArrayList<String>();
            for (int i = 0; i < stockInfo.size(); i++) {
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode())
                        .get();
                imgNode.add(doc.select("div").select("img").attr("class", "img").toString());
                String str = doc.body().html().replace("&nbsp;", "");
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
            if (pairs.get(i).getName() != null) {
                if (pairs.get(i).getName().equals(LIST_ANALYSIS_ONE)) {
                    table += "<tr><td colspan='2'>"
                            + pairs.get(i).getValue().replace(SPLIT_SIGN_FOUR, "").replace(SPLIT_SIGN_THREE, "")
                            + "</td></tr>";
                    table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                } else if (pairs.get(i).getName().equals(LIST_ANALYSIS_TWO)) {
                    table += "<tr><td></td><td>" + pairs.get(i).getValue() + "</td></tr>";
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

    public String split(String string, String str) {
        String name = "";
        String value = "";
        if (string.split(str).length == 1) {
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
}
