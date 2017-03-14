package com.zx.frontend.stock.entity;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

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

public class BJTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<String> textNode = null;
    private static List<String> code = null;
    private static List<NameValuePair> pairs = null;
    private static final String KEY_WORD = "藏品介绍";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";
    private static final int START_LENGTH = 4;
    private static final int END_LENGTH = 8;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("jmj_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("jmj_market")));
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
            boolean flag = false;
            for (int j = START_LENGTH; j < stockTextArray.length - END_LENGTH; j++) {
                if (flag) {
                    NameValuePair pair = new NameValuePair();
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(stockTextArray[j].replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                    pairs.add(pair);
                    continue;
                }
                String sign = "";
                if (stockTextArray[j].contains(SPLIT_SIGN_FOUR)) {
                    sign = SPLIT_SIGN_FOUR;
                } else {
                    sign = SPLIT_SIGN_THREE;
                }
                if (!stockTextArray[j].contains(sign)) {
                    NameValuePair pair = new NameValuePair();
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(stockTextArray[j].replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                    pairs.add(pair);
                } else {
                    int count = 0;
                    for (int k = 0; k < stockTextArray[j].length(); k++) {
                        if (stockTextArray[j].charAt(k) == sign.charAt(0)) {
                            count++;
                        }
                    }
                    if (count > 1) {
                        NameValuePair pair = new NameValuePair();
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(stockTextArray[j].replace("", "&nbsp;").replace(SPLIT_SIGN_ONE, "")
                                .replace(SPLIT_SIGN_TWO, "").replace(SPLIT_SIGN_FOUR, SPLIT_SIGN_THREE));
                        pairs.add(pair);
                    } else {
                        NameValuePair pair = new NameValuePair();
                        if (stockTextArray[j].split(sign).length == 1 || stockTextArray[j].contains("（注" + sign)) {
                            pair.setName(LIST_ANALYSIS_ONE);
                            pair.setValue(stockTextArray[j].substring(0, stockTextArray[j].length() - 1)
                                    .replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                            pairs.add(pair);
                        } else {
                            pair.setName(stockTextArray[j].split(sign)[0].replace(SPLIT_SIGN_ONE, "")
                                    .replace(SPLIT_SIGN_TWO, ""));
                            pair.setValue(stockTextArray[j].split(sign)[1]);
                            pairs.add(pair);
                        }
                    }
                }
                if (stockTextArray[j].contains(KEY_WORD)) {
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
            code = new ArrayList<String>();
            trustEveryone();
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

    public void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean effectiveCode(String ecode) {
        for (int m = 0; m < code.size(); m++) {
            if (ecode.equals(code.get(m))) {
                return false;
            }
        }
        return true;
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
                        table += "<tr><td colspan='2'>" + value + "</td></tr>";
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
}
