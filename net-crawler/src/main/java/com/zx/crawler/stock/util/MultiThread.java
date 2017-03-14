package com.zx.crawler.stock.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.crawler.trade.entity.Entrace;
import com.zx.user.merge.entity.PlatformConstants;

public class MultiThread extends Thread {
    private static Logger LOG = LoggerFactory.getLogger(Entrace.class);
    private MultiThreadParameter mtp;
    private int thread;

    public MultiThread(int thread, MultiThreadParameter mtp) {
        this.thread = thread;
        this.mtp = mtp;
    }

    public void run() {
        int pagination = mtp.getTotalPages() / mtp.getThread();
        for (int i = 1; i <= mtp.getThread(); i++) {
            if (this.thread == i && this.thread != mtp.getThread()) {
                getUserUrl(pagination * (i - 1) + 1, pagination * i);
                break;
            }
            if (this.thread == mtp.getThread()) {
                getUserUrl(pagination * (i - 1) + 1, mtp.getTotalPages());
            }
        }
    }

    public void getUserUrl(int start, int end) {
        List<String> userPageUrl = new ArrayList<String>();
        Entrace entrace = new Entrace();
        for (int k = start; k <= end; k++) {// 获得某一个版块的页码页码段
            String link = mtp.getSectionUrl() + mtp.getTurnPageUrl() + k;
            try {
                URLConnection conn = new URL(link).openConnection();
                conn.setConnectTimeout(10000);
                Document doc = Jsoup.parse(conn.getInputStream(), "gbk", link);
                Elements ListAnodes = doc.select("div.list").select("div.listtitle").select("a");// 获得某一页里面的多个a链接
                for (Element listAnode : ListAnodes) {// 解析某一页里面的不同的a链接
                    userPageUrl.add(mtp.getHost() + listAnode.attr("href"));
                }
            } catch (Exception e) {
                try {
                    if (!StockTradeUtil.isCompare(mtp.getList().getAbnormalUrls(), link)) {
                        SaveAbnormalUrl.saveExceptionalUrl(link, 2, PlatformConstants.getSite());
                    }
                } catch (Exception ex) {
                    LOG.warn(
                            "An error occurs when the data into the database corresponding to url: {} grand:2 from yichen",
                            link, ex);
                }
                LOG.warn(
                        "An error occurs when entering a page of a section corresponding to url: {} grand:2 from yichen",
                        link, e);
            }
            if (userPageUrl.size() == 0) {
                continue;
            }
            entrace.getUserMassage(userPageUrl, mtp.getList(), mtp.getName());// userPageUrl存放的是某一页里面的所有a链接
            userPageUrl.clear();
        }
    }
}
