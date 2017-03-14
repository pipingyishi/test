package com.zx.crawler.stock.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

public class ZnTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<NameValuePair> pairs = null;
    private static List<Elements> pNodes = null;
    private static List<String> listAnode = null;
    private static List<String> code = null;
    private static final String KEY_WORD_ONE = "藏品简介";
    private static final String KEY_WORD_TWO = "基本信息";
    private static final String KEY_WORD_THREE = "最新锁定信息";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String LIST_ANALYSIS_TWO = "allRight";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";
    private static final int ROW_LENGTH = 8;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("zn_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("zn_market")));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            throw new RuntimeException("读取配置文件出错!", e);
        }
    }

    @Override
    public List<StockModelInfo> analylizeStock() {
        List<StockModelInfo> smi = new ArrayList<StockModelInfo>();
        pairs = new ArrayList<NameValuePair>();
        for (int i = 0; i < pNodes.size(); i++) {
            if (!effectiveCode(stockInfo.get(i).getStockcode())) {
                continue;
            }
            StockModelInfo stockModelInfos = new StockModelInfo();
            stockModelInfos.setIsDel(false);
            stockModelInfos.setMarket(providerMap.get("provider").getMarket());
            stockModelInfos.setStockCode(stockInfo.get(i).getStockcode());
            stockModelInfos.setUpdateDate(new Date());
            boolean flag = false;
            for (Element pNode : pNodes.get(i)) {
                Elements pds = pNode.select("p");
                for (Element p : pds) {
                    String pText = p.text();
                    pText = Jsoup.parse(pText).body().text().replaceAll("\u00A0", " ").trim();
                    String sign = "";
                    if (pText.contains(SPLIT_SIGN_FOUR)) {
                        sign = SPLIT_SIGN_FOUR;
                    } else {
                        sign = SPLIT_SIGN_THREE;
                    }
                    if (pText.equals("") || pText.equals(" ")) {
                        continue;
                    }
                    if (flag) {
                        NameValuePair pair = new NameValuePair();
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(pText);
                        pairs.add(pair);
                        continue;
                    }
                    if (pText.contains(KEY_WORD_ONE)) {
                        if (pText.length() > 6) {// 连在一起
                            String text[] = pText.split(" ");
                            for (int m = 0; m < text.length; m++) {
                                NameValuePair pair = new NameValuePair();
                                pair.setName(LIST_ANALYSIS_ONE);
                                pair.setValue(text[m]);
                                pairs.add(pair);
                            }
                        } else {
                            NameValuePair pair = new NameValuePair();
                            pair.setName(LIST_ANALYSIS_ONE);
                            pair.setValue(pText);
                            pairs.add(pair);
                        }
                        flag = true;
                        continue;
                    }
                    if (!pText.contains(sign)) {
                        NameValuePair pair = new NameValuePair();
                        if (pText.length() > ROW_LENGTH && !pText.contains(SPLIT_SIGN_ONE)) {
                            pair.setName(LIST_ANALYSIS_TWO);
                            pair.setValue(pText);
                            pairs.add(pair);
                        } else {
                            pair.setName(LIST_ANALYSIS_ONE);
                            pair.setValue(pText.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                            pairs.add(pair);
                        }
                    } else {
                        int count = 0;
                        for (int j = 0; j < pText.length(); j++) {
                            if (pText.charAt(j) == '：') {
                                count++;
                            }
                        }
                        if (count > 1) {
                            if (pText.contains(KEY_WORD_TWO)) {
                                pText = pText.substring(5);
                                for (int n = 0; n < pText.length(); n++) {
                                    String name = "";
                                    String value = "";
                                    if (pText.charAt(n) == '【') {
                                        int amount = 0;
                                        for (int k = n;; k++) {
                                            if (pText.charAt(k) == '】') {
                                                name = pText.substring(k, n - 1);
                                            }
                                            if (pText.charAt(k) == '【') {
                                                for (int p1 = k;; p1--) {
                                                    if (p1 < 0) {
                                                        break;
                                                    }
                                                    if (pText.charAt(p1) != '：') {
                                                        value += pText.charAt(p1);
                                                    } else {
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else if (pText.contains(KEY_WORD_THREE)) {
                                NameValuePair pair = new NameValuePair();
                                pair.setName(LIST_ANALYSIS_ONE);
                                pair.setValue(pText);
                                pairs.add(pair);
                            } else {
                                NameValuePair pair = new NameValuePair();
                                pair.setName(LIST_ANALYSIS_ONE);
                                pair.setValue(pText.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                                pairs.add(pair);
                            }
                        } else {
                            if (pText.contains(KEY_WORD_TWO) && pText.contains(sign)) {
                                NameValuePair pair = new NameValuePair();
                                pair.setName(LIST_ANALYSIS_ONE);
                                pair.setValue(pText.substring(0, 5));
                                pairs.add(pair);
                                NameValuePair pair1 = new NameValuePair();
                                pair1.setName(pText.substring(5).split(sign)[0].replace(SPLIT_SIGN_ONE, "")
                                        .replace(SPLIT_SIGN_TWO, ""));
                                pair1.setValue(pText.substring(5).split(sign)[1]);
                                pairs.add(pair1);
                            } else {
                                NameValuePair pair = new NameValuePair();
                                if (pText.split(sign).length == 1 || pText.contains("注" + sign)) {
                                    pair.setName(LIST_ANALYSIS_ONE);
                                    pair.setValue(pText);
                                } else {
                                    pair.setName(pText.split(sign)[0].replace(SPLIT_SIGN_ONE, "")
                                            .replace(SPLIT_SIGN_TWO, ""));
                                    pair.setValue(pText.split(sign)[1]);
                                }
                                pairs.add(pair);
                            }
                        }
                    }
                }
            }
            String table = createTable();
            if (listAnode.get(i).equals("noImg")) {
                stockModelInfos.setStockDescription(table);
            } else {
                stockModelInfos.setStockDescription("<tr><td colspan='2'>" + listAnode.get(i) + "</td></tr>" + table);
            }
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
            pNodes = new ArrayList<Elements>();
            listAnode = new ArrayList<String>();
            code = new ArrayList<String>();
            for (int i = 0; i < stockInfo.size(); i++) {
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode())
                        .get();
                Elements pNode = doc.select("p");
                if (pNode.size() == 0) {
                    code.add(stockInfo.get(i).getStockcode());
                }
                String imgNode = "<center>" + doc.select("img").attr("class", "img").attr("width", "")
                        .attr("height", "").attr("style", "").toString() + "</center>";
                if (!imgNode.contains("img") || imgNode.contains("C:/") || imgNode.contains("D:/")
                        || imgNode.contains("E:/") || imgNode.contains("F:/") || imgNode.contains("c:/")
                        || imgNode.contains("d:/") || imgNode.contains("e:/") || imgNode.contains("f:/")) {
                    listAnode.add("noImg");
                } else {
                    listAnode.add(imgNode);
                }
                pNodes.add(pNode);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String createTable() {
        String table = "";
        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).getName() == null || pairs.get(i).getValue() == null) {
                continue;
            }
            String name = Jsoup.parse(pairs.get(i).getName()).body().text().replaceAll("\u00A0", " ").trim();
            String value = Jsoup.parse(pairs.get(i).getValue()).body().text().replaceAll("\u00A0", " ").trim();
            if (pairs.get(i).getName() != null) {
                if (pairs.get(i).getName().equals(LIST_ANALYSIS_ONE)) {
                    if (value.equals("")) {
                        continue;
                    } else {
                        table += "<tr><td colspan='2'>" + value + "</td></tr>";
                        table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                    }
                } else if (pairs.get(i).getName().equals(LIST_ANALYSIS_TWO)) {
                    if (value.equals("")) {
                        continue;
                    } else {
                        table += "<tr><td></td><td>" + value + "</td></tr>";
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
