package com.zx.crawler.stock.util;

import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentUtil {
    public static Document getDocument(String url) throws Exception {
        Document doc = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(10000);
            doc = Jsoup.parse(conn.getInputStream(), "gbk", url);
            return doc;
        } catch (Exception e) {
            throw e;
        }
    }
}
