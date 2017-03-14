package com.zx.crawler.stock.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zx.crawler.trade.entity.Entrace;
import com.zx.modules.trade.service.UserUrlService;
import com.zx.trade.datamodel.UserUrlMassage;

public class CrawUrlThread extends Thread {
    private static final int BOARD_ID = 0;
    private static final int URL_ID = 1;
    private static final int ID_NUMBER = 3;
    private static final int PAGE_SIZE = 20;
    private static Logger LOG = LoggerFactory.getLogger(Entrace.class);
    private int thread;
    private MultiThreadParameter mtp;
    private static UserUrlService userUrlService = null;
    static {
        ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");
        userUrlService = (UserUrlService) context.getBean("userUrl");
    }

    public CrawUrlThread(int thread, MultiThreadParameter mtp) {
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
        List<UserUrlMassage> userUrlMassages = new ArrayList<UserUrlMassage>();
        for (int k = start; k <= end; k++) {// 获得某一个版块的总页数
            String link = mtp.getSectionUrl() + mtp.getTurnPageUrl() + k;
            Document doc = null;
            try {
                doc = DocumentUtil.getDocument(link);
                LOG.info("enter {} page corresponding to url{} from yichen in grabing urlId", k, link);
            } catch (Exception e) {
                try {
                    if (!StockTradeUtil.isCompare(mtp.getAbnormalUrl(), link)) {
                        SaveAbnormalUrl.saveExceptionalUrl(link, 2, "url");
                    }
                } catch (Exception ex) {
                    LOG.warn(
                            "An error occurs when the abnormalUrl into the database {} grand:2 from yichen in grabing urlId",
                            link, ex);
                }
                LOG.warn("An error occurs when entering a page of a section {} from yichen in grabing urlId", link, e);
            }
            if (doc == null) {
                continue;
            }
            Elements ListAnodes = doc.select("div.list").select("div.listtitle").select("a");// 获得某一页里面的多个a链接
            for (Element listAnode : ListAnodes) {// 解析某一页里面的不同的a链接
                List<String> id = InterceptBlockUtil.getNumberBlock(listAnode.attr("href"));
                if (id.size() != ID_NUMBER) {
                    continue;
                }
                userUrlMassages.add(getUserUrlMassage(id));
            }
        }
        flipPage(userUrlMassages);
    }

    public void flipPage(List<UserUrlMassage> userUrlMassages) {
        try {
            if (userUrlMassages.size() > PAGE_SIZE) {
                int beginIndex = 0, endIndex = PAGE_SIZE, count = 1;
                while (true) {
                    count++;
                    if (endIndex > userUrlMassages.size()) {
                        userUrlService.batchInsertUpdate(
                                getUserUrlMassage(beginIndex, userUrlMassages.size() - 1, userUrlMassages));
                        break;
                    }
                    userUrlService.batchInsertUpdate(getUserUrlMassage(beginIndex, endIndex, userUrlMassages));
                    beginIndex = endIndex + 1;
                    endIndex *= count;
                }
            } else {
                userUrlService.batchInsertUpdate(userUrlMassages);
            }
        } catch (Exception e) {
            LOG.warn("An error occurs when the urlId into the database from yichen in grabing urlId", e);
        }
    }

    public UserUrlMassage getUserUrlMassage(List<String> id) {
        UserUrlMassage userUrlMassage = new UserUrlMassage();
        userUrlMassage.setBoardId(id.get(BOARD_ID));
        userUrlMassage.setUrlId(id.get(URL_ID));
        userUrlMassage.setDate(new Date());
        userUrlMassage.setIsDel(false);
        userUrlMassage.setSection(mtp.getSection());
        userUrlMassage.setFlag(false);
        return userUrlMassage;
    }

    public List<UserUrlMassage> getUserUrlMassage(int start, int end, List<UserUrlMassage> url) {
        List<UserUrlMassage> userUrl = new ArrayList<UserUrlMassage>();
        for (int i = start; i <= end; i++) {
            userUrl.add(url.get(i));
        }
        return userUrl;
    }
}
