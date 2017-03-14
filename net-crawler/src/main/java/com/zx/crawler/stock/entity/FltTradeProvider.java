package com.zx.crawler.stock.entity;

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

public class FltTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<NameValuePair> pairs = null;
    private static List<Elements> pNodes = null;
    private static List<String> code = null;
    private static final String KEY_WORD = "交易参数";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String LIST_ANALYSIS_TWO = "allRight";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = ":";
    private static final String SPLIT_SIGN_FOUR = "：";
    private static final char SBC_SPACE = 12288;
    private static final char DBC_SPACE = ' ';
    private static final char LINE_LENGTH = 25;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("flt_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("flt_market")));
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
            Elements pds = pNodes.get(i).select("p");
            boolean flag = false;
            for (int j = 0; j < pds.size(); j++) {
                String pText = pds.get(j).text();
                pText = Jsoup.parse(pText).body().text().replaceAll("\u00A0", " ").trim();
                if (pText.equals("")) {
                    continue;
                }
                if (pText.charAt(0) == SBC_SPACE || pText.charAt(pText.length() - 1) == SBC_SPACE) {
                    pText = fullToHalf(pText).trim();
                }
                if (pText.contains(KEY_WORD)) {
                    flag = true;
                }
                if (!flag && !pText.contains(KEY_WORD)) {
                    NameValuePair pair = new NameValuePair();
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(KEY_WORD);
                    pairs.add(pair);
                    flag = true;
                    continue;
                }
                if (!pText.contains(SPLIT_SIGN_TWO)) {
                    NameValuePair pair = new NameValuePair();
                    if (pText.length() < LINE_LENGTH && j < pds.size() - 1) {
                        pair.setName(LIST_ANALYSIS_TWO);
                        pair.setValue(pText);
                        pairs.add(pair);
                    } else {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(pText);
                        pairs.add(pair);
                    }
                } else {
                    NameValuePair pair = new NameValuePair();
                    if (pText.split(SPLIT_SIGN_TWO).length == 1) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(pText.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                        pairs.add(pair);
                    } else {
                        pair.setName(pText.split(SPLIT_SIGN_TWO)[0].replace(SPLIT_SIGN_ONE, ""));
                        pair.setValue(pText.split(SPLIT_SIGN_TWO)[1].replace(SPLIT_SIGN_THREE, "")
                                .replace(SPLIT_SIGN_FOUR, ""));
                        pairs.add(pair);
                    }
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
            pNodes = new ArrayList<Elements>();
            code = new ArrayList<String>();
            trustEveryone();
            for (int i = 0; i < stockInfo.size(); i++) {
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode())
                        .get();
                Elements pNode = doc.select("p");
                if (pNode.size() == 0) {
                    code.add(stockInfo.get(i).getStockcode());
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

    public String fullToHalf(String text) {
        StringBuilder buf = new StringBuilder(text.length());
        char ch[] = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            if (ch[i] == SBC_SPACE) {
                buf.append(DBC_SPACE);
            } else {
                buf.append(ch[i]);
            }
        }
        return buf.toString();
    }
}
