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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

public class NfTradeProvider extends AbsStockCrawler {
    private static final String PROPERTY_NAME = "trade-provider.properties";
    private static Map<String, Provider> providerMap = null;
    private static List<StockOrigin> stockInfo = null;
    private static List<NameValuePair> pairs = null;
    private static List<Elements> pNodes = null;
    private static List<String> listAnode = null;
    private static List<String> code = null;
    private static final int CODE_LENGTH = 6;
    private static final String KEY_WORD = "相关公告";
    private static final String LIST_ANALYSIS_ONE = "all";
    private static final String SPLIT_SIGN_ONE = "【";
    private static final String SPLIT_SIGN_TWO = "】";
    private static final String SPLIT_SIGN_THREE = "：";
    private static final int LINE_LENGTH = 33;

    @Override
    public void getPropert() {
        try {
            Resource resource = new ClassPathResource(PROPERTY_NAME);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            providerMap = Maps.newConcurrentMap();
            Provider provider = new Provider();
            provider.setHost(props.getProperty("nf_envPRDHost"));
            provider.setMarket(Integer.parseInt(props.getProperty("nf_market")));
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
            int amount = 0;
            for (int j = 0; j < pds.size(); j++) {
                String pText = pds.get(j).text();
                if (pText.length() > CODE_LENGTH) {
                    amount++;
                }
                if (amount == 1) {
                    pText = pText.substring(CODE_LENGTH);
                    amount++;
                }
                pText = Jsoup.parse(pText).body().text().replaceAll("\u00A0", " ").trim();
                if (pText.equals(KEY_WORD)) {
                    break;
                }
                if (!pText.contains(SPLIT_SIGN_TWO)) {
                    NameValuePair pair = new NameValuePair();
                    pair.setName(LIST_ANALYSIS_ONE);
                    pair.setValue(pText);
                    pairs.add(pair);
                } else {
                    NameValuePair pair = new NameValuePair();
                    if (pText.split(SPLIT_SIGN_TWO).length == 1) {
                        pair.setName(LIST_ANALYSIS_ONE);
                        pair.setValue(pText.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, ""));
                        pairs.add(pair);
                    } else {
                        if (pText.length() > LINE_LENGTH) {
                            pair.setName(LIST_ANALYSIS_ONE);
                            pair.setValue(pText.replace(SPLIT_SIGN_ONE, "").replace(SPLIT_SIGN_TWO, SPLIT_SIGN_THREE));
                            pairs.add(pair);
                        } else {
                            pair.setName(pText.split(SPLIT_SIGN_TWO)[0].replace(SPLIT_SIGN_ONE, ""));
                            pair.setValue(pText.split(SPLIT_SIGN_TWO)[1]);
                            pairs.add(pair);
                        }
                    }
                }
            }
            String table = createTable();
            if (listAnode.get(i).equals("noImg")) {
                stockModelInfos.setStockDescription(table);
            } else {
                String imgNodes = "";
                for (int n = 0; n < listAnode.get(i).split("node").length; n++) {
                    String imgs = "";
                    imgs += listAnode.get(i).split("node")[n];
                    imgs = "<tr><td colspan='2'>" + imgs + "</td></tr>";
                    imgNodes += imgs;
                }
                stockModelInfos.setStockDescription(imgNodes + table);
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
            trustEveryone();
            for (int i = 0; i < stockInfo.size(); i++) {
                Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode()).timeout(100000);
                Document doc = Jsoup.connect(providerMap.get("provider").getHost() + stockInfo.get(i).getStockcode())
                        .get();
                Elements pNode = doc.select("p");
                if (pNode.size() == 0) {
                    code.add(stockInfo.get(i).getStockcode());
                }
                Elements imgNodes = doc.select("img").attr("class", "img").attr("width", "").attr("height", "")
                        .attr("style", "");
                String imgs = "";
                int count = 0;
                for (int n = 0; n < imgNodes.size(); n++) {
                    String img = "<center>" + imgNodes.get(n).toString() + "</center>";
                    if (!img.contains("img") || img.contains("C:/") || img.contains("D:/") || img.contains("E:/")
                            || img.contains("F:/") || img.contains("c:/") || img.contains("d:/") || img.contains("e:/")
                            || img.contains("f:/")) {
                        count++;
                    } else {
                        imgs += img + "node";
                    }
                }
                if (count == imgNodes.size()) {
                    listAnode.add("noImg");
                } else {
                    listAnode.add(imgs);
                }
                pNodes.add(pNode);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String createTable() {
        String table = "";
        int count = 0;
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
                        count++;
                        if (count == 1) {
                            table += "<tr><td colspan='2'><center>" + value + "</center></td></tr>";
                            table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                        } else {
                            table += "<tr><td colspan='2'>" + value + "</td></tr>";
                            table += "<tr><td colspan='2' style='border-bottom: solid 1px #e9e9e9;'></td></tr>";
                        }
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
}
