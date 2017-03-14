package com.zx.frontend.stock.entity;

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
import com.zx.modules.stock.service.StockService;
import com.zx.stock.datamodel.StockModelInfo;
import com.zx.stock.datamodel.StockOrigin;

public class NjTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static final int START_INDEX = 4;
    private static final int END_INDEX = 191;
    private static Map<String, Provider> providerMap = null;
    private static List<Elements> listElement = null;
    private static List<String> listAnode = null;
    private static String aNode = "";
    private static final String KEY_WORD_ONE = "最新锁定信息";
    private static final String KEY_WORD_TWO = "发行机构";
    private static final String KEY_WORD_THREE = "发行单位";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";
    private static final int FULL_WIDTH_SPACE = 160;
    private static final int HALF_WIDTH_SPACE = 32;

    public String getStockMassage(Element div, String stockMassage, int count) {
        Elements pNodes = div.select("p");
        int len = 0;
        String str[] = new String[pNodes.size()];
        for (Element p : pNodes) {
            str[len] = Jsoup.parse(p.text()).body().text().replaceAll("\u00A0", " ").trim();
            len++;
        }
        boolean flag = false;
        for (len = 0; len < str.length; len++) {
            String tr = "";
            if (str[len].equals("")) {
                continue;
            }
            if (str[len].contains(KEY_WORD_ONE)) {
                break;
            }
            if (flag) {
                if (str[len].contains(SPLIT_SIGN_ONE) && str[len].contains(SPLIT_SIGN_FOUR)) {
                    tr = "<tr><td style='width=50%;'>" + str[len].split(SPLIT_SIGN_THREE)[0].replace(SPLIT_SIGN_ONE, "")
                            .replace(SPLIT_SIGN_TWO, "") + "</td>";
                    tr = tr + "<td>" + str[len].split(SPLIT_SIGN_FOUR)[1] + "</td></tr>";
                } else {
                    tr = "<tr><td colspan='2'>" + str[len] + "</td></tr>";
                }
            }
            if (!flag) {
                if (str[len].indexOf('：') != -1 || str[len].indexOf(':') != -1) {
                    if (str[len].indexOf('：') != -1) {
                        for (int x = 0; x < str[len].split(SPLIT_SIGN_FOUR).length; x++) {
                            if (x == 0) {
                                tr = "<tr><td style='width:50%;'>" + str[len].split(SPLIT_SIGN_FOUR)[x]
                                        .replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, "") + "</td>";
                            } else {
                                tr = tr + "<td>" + str[len].split(SPLIT_SIGN_FOUR)[x] + "</td></tr>";
                            }
                        }
                    } else {
                        for (int x = 0; x < str[len].split(SPLIT_SIGN_THREE).length; x++) {
                            if (x == 0) {
                                tr = "<tr><td style='width=50%;'>" + str[len].split(SPLIT_SIGN_THREE)[x]
                                        .replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, "") + "</td>";
                            } else {
                                tr = tr + "<td>" + str[len].split(SPLIT_SIGN_THREE)[x] + "</td></tr>";
                            }
                        }
                    }
                } else {
                    if (count == 1) {
                        String s = str[len];
                        for (int t = 0; t < s.length(); t++) {
                            if (s.charAt(t) != FULL_WIDTH_SPACE && s.charAt(t) != HALF_WIDTH_SPACE) {
                                str[len] = s.substring(t, s.length());
                                break;
                            }
                        }
                        tr = "<tr ><td></td><td>" + str[len] + "</td></tr>";
                    } else {
                        tr = "<tr><td colspan='2'>" + str[len] + "</td></tr>";
                    }
                }
            }
            if (str[len].contains(KEY_WORD_TWO) || str[len].contains(KEY_WORD_THREE)) {
                flag = true;
            }
            stockMassage += tr;
            stockMassage += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
        }
        return stockMassage;
    }

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("nj_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("nj_market")));
            provider.setFirstKey(props.getProperty("nj_firstKey"));
            provider.setSecondKey(props.getProperty("nj_secondKey"));
            providerMap.put("provider", provider);
        } catch (Exception e) {
            throw new RuntimeException("读取配置文件出错!", e);
        }
    }

    @Override
    public List<StockModelInfo> analylizeStock() {
        List<StockModelInfo> smi = new ArrayList<StockModelInfo>();
        for (int i = 0; i < listElement.size(); i++) {
            int count = 0;
            String stockCode = "";
            String stockMassage = "";
            String stockPrice = "";
            for (Element div : listElement.get(i)) {
                count++;
                if (count == 1) {
                    String text = div.text();
                    if (text.contains(providerMap.get("provider").getFirstKey())) {
                        boolean flag = false;
                        for (int n = 0; n < text.length(); n++) {
                            if (text.charAt(n) >= '0' && text.charAt(n) <= '9') {
                                stockCode += text.charAt(n);
                                flag = true;
                            } else {
                                if (flag) {
                                    break;
                                }
                            }
                        }
                    }
                    String stockText[] = text.split(" ");
                    for (int j = 0; j < stockText.length; j++) {
                        if (stockText[j].contains(providerMap.get("provider").getSecondKey())) {
                            boolean flag = false;
                            for (int n = 0; n < stockText[j].length(); n++) {
                                if (stockText[j].charAt(n) >= '0' && stockText[j].charAt(n) <= '9'
                                        || stockText[j].charAt(n) == '.') {
                                    stockPrice += stockText[j].charAt(n);
                                    flag = true;
                                } else {
                                    if (flag) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    stockMassage = getStockMassage(div, stockMassage, count);
                }
                if (count == 2) {
                    stockMassage = getStockMassage(div, stockMassage, count);
                    break;
                }
            }
            stockMassage = "<tr><td colspan='2'>" + listAnode.get(i) + "</td></tr>" + stockMassage;
            ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
            StockService stockService = (StockService) context.getBean("stockService");
            List<StockOrigin> stockInfo = stockService.selectStockMassage(providerMap.get("provider").getMarket(),
                    stockCode);
            for (int k = 0; k < stockInfo.size(); k++) {
                StockModelInfo stockModelInfos = new StockModelInfo();
                stockModelInfos.setMarket(providerMap.get("provider").getMarket());
                stockModelInfos.setStockCode(stockCode);
                stockModelInfos.setStockDescription(stockMassage);
                stockModelInfos.setIsDel(false);
                stockModelInfos.setUpdateDate(new Date());
                smi.add(stockModelInfos);
            }
        }
        return smi;
    }

    @Override
    public void crawContent() {
        listElement = new ArrayList<Elements>();
        listAnode = new ArrayList<String>();
        for (int i = START_INDEX; i < END_INDEX; i++) {
            try {
                Jsoup.connect(providerMap.get("provider").getHost() + i + ".html").timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + i + ".html").get();
                Elements divs = doc.select("div.list").attr("style", "");
                aNode = "<center>" + divs.select("a").remove().select("img").attr("align", "center")
                        .attr("class", "img").attr("width", "").attr("height", "").toString() + "</center>";
                divs.select("p").attr("style", "");
                listElement.add(divs);
                listAnode.add(aNode);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
