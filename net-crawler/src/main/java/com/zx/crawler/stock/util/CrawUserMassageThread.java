package com.zx.crawler.stock.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.modules.trade.service.UserUrlService;
import com.zx.user.merge.entity.PlatformConstants;

public class CrawUserMassageThread extends Thread {
    private static Logger LOG = LoggerFactory.getLogger(CrawUserMassageThread.class);
    private int thread;
    private MultiThreadParameter mtp;
    private static UserUrlService userUrlService = null;
    private static final int URL_GRAND = 3;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        userUrlService = (UserUrlService) context.getBean("userUrl");
    }
    public CrawUserMassageThread() {

    }
    public CrawUserMassageThread(int thread, MultiThreadParameter mtp) {
        this.thread = thread;
        this.mtp = mtp;
    }

    public void run() {
        int urlSize = mtp.getUrl().size() / mtp.getThread();
        for (int i = 1; i <= mtp.getThread(); i++) {
            if (this.thread == i && this.thread != mtp.getThread()) {
                getUserUrlMassage(urlSize * (i - 1) + 1, urlSize * i, mtp.getUrl());
                break;
            }
            if (this.thread == mtp.getThread()) {
                getUserUrlMassage(urlSize * (i - 1) + 1, mtp.getUrl().size(), mtp.getUrl());
            }
        }
    }

    public void getUserUrlMassage(int start, int end, List<String> listUrl) {
        for (int i = start - 1; i < end; i++) {
            String url = listUrl.get(i);
            Document doc = null;
            try {
                doc = DocumentUtil.getDocument(url);// 看看该页需不需要翻页
                LOG.info("connect successfully corresponding to getUserMassage():{} form yichen", url);
            } catch (Exception e) {
                LOG.warn("connection failure corresponding to getUserMassage():{} from  yichen", url, e);
            }
            if (doc == null) {
                continue;
            }
            String tdText = doc.select("td.tabletitle1").text();
            String str[] = tdText.split(" ");
            String temp = str[str.length - 1].split("/")[1];
            temp = StringUtils.replaceEach(temp, new String[] { "\u00A0", " ", " " }, new String[] { "", "", "" });
            temp = temp.substring(0, temp.length() - 1);
            int totalPage = ParseUtil.safeParseInt(temp);
            ListUtil listUtil = null;
            if (totalPage > 1) {
                getUserMsssage(url, listUtil);
                for (int j = 2; j <= totalPage; j++) {
                    getUserMsssage(url + mtp.getFlip() + j, listUtil);
                }
            } else {
                getUserMsssage(url, listUtil);
            }
        }
        if (end == mtp.getUrl().size()) {// 修改状态，同时修改之前的
            userUrlService.updateId(mtp.getOffset(), mtp.getLimit());
        }
    }

    public void getUserMsssage(String url, ListUtil listUtil) {
        Document doc = null;
        try {
            doc = DocumentUtil.getDocument(url);
            LOG.info("connect successfully corresponding to getUserMsssage():{} form yichen", url);
        } catch (Exception e) {
            try {
                if (!StockTradeUtil.isCompare(mtp.getList().getAbnormalUrls(), url)) {
                    SaveAbnormalUrl.saveExceptionalUrl(url, URL_GRAND, PlatformConstants.getSite());
                }
            } catch (Exception ex) {
                LOG.warn("An error occurs when the data into the database {} grand:3 from yichen", url, ex);
            }
            LOG.warn("connection failure corresponding to url:{} from  yichen", url, e);
        }
        if (doc == null) {
            return;
        }
        Elements imgNodes = doc.select("[src=skins/Default/sigline.gif]");
        Elements divNodes = doc.select("div.postuserinfo");
        if (imgNodes.size() == 0 || divNodes.size() == 0) {
            return;
        }
        com.zx.user.massage.entity.UserMassage userMassage = new com.zx.user.massage.entity.UserMassage();
        if (listUtil == null) {
            userMassage.getText(divNodes, imgNodes, url, mtp.getList(), mtp.getName());// 代表某一页用户信息
        } else {
            userMassage.getText(divNodes, imgNodes, url, listUtil, listUtil.getName());
        }
    }
}
